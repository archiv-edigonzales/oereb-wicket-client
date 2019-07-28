package ch.so.agi.oereb.wicketclient;

import java.util.Iterator;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.CsrfPreventionRequestCycleListener;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.springframework.stereotype.Component;

import com.giffing.wicket.spring.boot.starter.app.WicketBootStandardWebApplication;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;

@Component
public class BootstrapConfig extends WicketBootStandardWebApplication {
    @Override
    protected void init() {
        super.init();
        
//        if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
//            getDebugSettings().setComponentPathAttributeName("wicketpath");
//        }
        
        CsrfPreventionRequestCycleListener listener = new CsrfPreventionRequestCycleListener();
        listener.addAcceptedOrigin("http://oereb.catais.org");
        listener.addAcceptedOrigin("https://oereb.catais.org");
        
//        getRequestCycleListeners().add(new CsrfPreventionRequestCycleListener().addAcceptedOrigin("http://oereb.catais.org")); 
//        getRequestCycleListeners().add(new CsrfPreventionRequestCycleListener().addAcceptedOrigin("https://oereb.catais.org")); 
        
        
        BootstrapSettings settings = new BootstrapSettings();
        Bootstrap.install(this, settings);
        
//        Iterator<IRequestCycleListener> it = getRequestCycleListeners().iterator();
//        while(it.hasNext()) {
//            Object listener = it.next();
//            if (listener instanceof CsrfPreventionRequestCycleListener) {
//                System.out.println(listener.getClass());
//                
//                CsrfPreventionRequestCycleListener csrfListener = (CsrfPreventionRequestCycleListener) listener;
//            }
//        }
    }
}
