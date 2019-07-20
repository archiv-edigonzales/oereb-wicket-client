package ch.so.agi.oereb.wicketclient.wicket;

import java.util.Date;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;


@WicketHomePage
public class HomePage extends WebPage {
    private String egrid = "CH158782774974";

    public HomePage() {
        Form<HomePage> form = new Form<HomePage>("form", new CompoundPropertyModel<HomePage>(this)) {
            @Override
            protected void onSubmit() {
                //setResponsePage(SecondPage.class);
                System.out.println("Form submitted.");
                System.out.println(egrid);
            }
        };
        add(form);
        
        TextField<String> egridField = new TextField<String>("egrid"); 
        form.add(egridField);
        
        
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

        
    }
}