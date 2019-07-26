package ch.so.agi.oereb.wicketclient.wicket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ThemeType;
import ch.so.agi.oereb.wicketclient.services.ExtractService;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.IeEdgeMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MobileViewportMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.Collapsible;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.TextContentTab;

@WicketHomePage
public class OerebPage extends WebPage {
    @SpringBean
    ExtractService extractService;
    
    private String egrid = "CH158782774974";
    
//    private List<ThemeType> concernedThemes = new ArrayList<ThemeType>();

    @SuppressWarnings("unchecked")
    public OerebPage() {
        add(new HtmlTag("html"));
        MobileViewportMetaTag mvt = new MobileViewportMetaTag("viewport");
        mvt.setWidth("device-width");
        mvt.setInitialScale("1");
        add(mvt);
        add(new IeEdgeMetaTag("ie-edge"));
        add(new MetaTag("description", Model.of("description"), Model.of("OEREB...")));
        add(new MetaTag("author", Model.of("author"), Model.of("Stefan Ziegler <stefan.ziegler.de@gmail.com>")));

        Form<OerebPage> form = new Form<OerebPage>("form", new CompoundPropertyModel<OerebPage>(this)) {
            @Override
            protected void onSubmit() {
                System.out.println("Form submitted.");
                System.out.println(egrid);
                
                try {
                    GetExtractByIdResponse extractResponse = extractService.getExtractByEgrid(egrid);
                    
                    List<ThemeType> concernedThemes = extractResponse.getValue().getExtract().getValue().getConcernedTheme();
                    
                    ListView concernedThemesListView = new ListView("concernedThemes", concernedThemes) {
                        @Override
                        protected void populateItem(ListItem item) {
                            ThemeType theme = (ThemeType) item.getModelObject();
                            item.add(new Label("themeText", theme.getText().getText()));
                            item.add(new Label("themeCode", theme.getCode()));
                        }
                    };
                    
                    this.getParent().replace(concernedThemesListView);


                } catch (IOException e) {                    
                    error(e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        add(form);
        
        form.add(new FeedbackPanel("feedback"));

        TextField<String> egridField = new TextField<String>("egrid"); 
        form.add(egridField);
        
        
        add(new ListView("concernedThemes", new ArrayList<ThemeType>()) {
            @Override
            protected void populateItem(ListItem item) {
                ThemeType theme = (ThemeType) item.getModelObject();
                item.add(new Label("themeText", theme.getText().getText()));
                item.add(new Label("themeCode", theme.getCode()));
            }
        });
        
        
        IModel timeStampModel = new Model<String>(){
            @Override
            public String getObject() {
                return new Date().toString();
            }
        };
        add(new Label("timeStamp", timeStampModel));
    }
}
