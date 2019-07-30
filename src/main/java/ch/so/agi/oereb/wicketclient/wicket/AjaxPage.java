package ch.so.agi.oereb.wicketclient.wicket;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/ajax")
public class AjaxPage extends WebPage {
    private static final long serialVersionUID = 1L;
    
    int _counter;

    public AjaxPage() {
        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        CompoundPropertyModel<AjaxPage> formModel = new CompoundPropertyModel<AjaxPage>(this);
        Form<AjaxPage> form = new Form<AjaxPage>("form",formModel);
        
        TextField<String> name = new TextField<String>("text", Model.of()); 
        form.add(name);
        
        form.add(new AjaxButton("ajaxSubmit") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                    info("AjaxSubmit Button(ajax): " + _counter++);
                    target.add(feedbackPanel);
          
            }
            
            @Override
            protected void onError(AjaxRequestTarget target)
            {
                error("AjaxSubmit Button(ajax): " + _counter++);
                target.add(feedbackPanel);
            }
        });
        add(form);
        
        
        add(new Label("foo", Model.of("bar")));
    }

}
