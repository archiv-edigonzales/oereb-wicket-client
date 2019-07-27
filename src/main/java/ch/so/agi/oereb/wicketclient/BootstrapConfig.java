package ch.so.agi.oereb.wicketclient;

import org.apache.wicket.RuntimeConfigurationType;
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
        
        BootstrapSettings settings = new BootstrapSettings();
        Bootstrap.install(this, settings);
    }
}