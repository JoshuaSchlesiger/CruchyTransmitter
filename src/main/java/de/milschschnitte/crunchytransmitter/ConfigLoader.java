package de.milschschnitte.crunchytransmitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:application.properties")
public class ConfigLoader {

    private static Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        ConfigLoader.environment = environment;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }
}
