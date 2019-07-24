package ch.so.agi.oereb.wicketclient.wicket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wicketstuff.annotation.mount.MountPath;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.google.common.collect.Lists;

import ch.so.agi.oereb.wicketclient.Person;
import ch.so.agi.oereb.wicketclient.services.ExtractService;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.IeEdgeMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MobileViewportMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxLazyLoadTextContentTab;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.Collapsible;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.TextContentTab;


@WicketHomePage
//@MountPath(value = "/javascript", alt = "/js")
public class HomePage extends WebPage {
    @SpringBean
    ExtractService extractService;
    
    private String egrid = "CH158782774974";

    List tabs=new ArrayList();
    Collapsible Collapsible;
    
    public HomePage() {
        
        add(new HtmlTag("html"));
        MobileViewportMetaTag mvt = new MobileViewportMetaTag("viewport");
        mvt.setWidth("device-width");
        mvt.setInitialScale("1");
        add(mvt);
        add(new IeEdgeMetaTag("ie-edge"));
        add(new MetaTag("description", Model.of("description"), Model.of("OEREB...")));
        add(new MetaTag("author", Model.of("author"), Model.of("Stefan Ziegler <stefan.ziegler.de@gmail.com>")));
        
        Form<HomePage> form = new Form<HomePage>("form", new CompoundPropertyModel<HomePage>(this)) {
            @Override
            protected void onSubmit() {
                //setResponsePage(SecondPage.class);
                System.out.println("Form submitted.");
                System.out.println(egrid);
                
                try {
//                    extractService = new ExtractService();
                    extractService.getExtractByEgrid(egrid);
                    
                    tabs.add(new TextContentTab(Model.of("Title 3"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.")));

//                    collapsible.clearOriginalDestination();
//                    collapsible.remove();
                    Collapsible collapsible = new Collapsible("accordion42", tabs, Model.of(-1));
                    this.getParent().replace(collapsible);
//                    collapsible.modelChanged();
//                    collapsible.render();


//                    add(collapsible);

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
        
//        Collapsible collapsible = new Collapsible("accordion42", Lists.<ITab>newArrayList(
//                new TextContentTab(Model.of("Title 1"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.")),
//                new TextContentTab(Model.of("Title 2"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.")),
//                new TextContentTab(Model.of("Title 3"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.")),
//                new AjaxLazyLoadTextContentTab(Model.of("Title 4"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS."))),
//                Model.of(-1));
        
        
//        Lists.<ITab>newArrayList(
//                new TextContentTab(Model.of("Title 1"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.")),
//                new TextContentTab(Model.of("Title 2"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.")),
//                new TextContentTab(Model.of("Title 3"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.")),
//                new TextContentTab(Model.of("Title 4"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS."))
//        
//        Collapsible collapsible = new Collapsible("accordion42", ),
//                Model.of(-1));        
        
        
        tabs.add(new TextContentTab(Model.of("Title 1"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.")));
        tabs.add(new TextContentTab(Model.of("Title 2"), Model.of("Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.")));
        
        Collapsible collapsible = new Collapsible("accordion42", tabs, Model.of(-1));        
          add(collapsible);

//        add(new Collapsible("accordion42", tabs));

//        add(collapsible);
//        
//        System.out.println(collapsible.getAjaxRegionMarkupId());
//        System.out.println(collapsible.getMarkupAttributes());
        
        
//        TextContentTab textContentTab = new TextContentTab(Model.of("Title zzz"), Model.of("aaaaaa"));
//        System.out.println(textContentTab.getTitle());
//        System.out.println(textContentTab.toString());
//        System.out.println(textContentTab);
//        
//        System.out.println(collapsible.getPageRelativePath());
//        System.out.println(collapsible.getPath());
//        System.out.println(collapsible.get("accordion42:accordion42_tabs_2:accordion42_tabs_2_title"));
        
//        System.out.println(collapsible.getMarkupId());
//        System.out.println(collapsible.getId());
//        System.out.println(collapsible.getDefaultModelObjectAsString());

//        add(new Link<Void>("mylink") {
//            @Override
//            public void onClick() {
//                setResponsePage(SecondPage.class);
//            }
//        });
        
        IModel timeStampModel = new Model<String>(){
            @Override
            public String getObject() {
                return new Date().toString();
            }
        };
        //add(new Label("timeStamp", timeStampModel));
        add(new Label("timeStamp", () -> java.time.LocalDateTime.now()));

        List<Person> persons = Arrays.asList(new Person("John", "Smith"), new Person("Dan", "Wong"));

        add(new ListView<Person>("persons", persons) {
            @Override
            protected void populateItem(ListItem<Person> item) {
                item.add(new Label("fullName", new PropertyModel(item.getModel(), "fullName")));
            }
        });
        
        
    }
}