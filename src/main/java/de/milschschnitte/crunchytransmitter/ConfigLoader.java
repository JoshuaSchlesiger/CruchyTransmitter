package de.milschschnitte.crunchytransmitter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.ConfigurationException;


public class ConfigLoader {
    private Properties properties;

    public ConfigLoader() throws ConfigurationException {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() throws ConfigurationException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                throw new ConfigurationException("Cannot find config.properties");
            }
            // load a properties file from class path, inside static method
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getDatabaseUrl() {
        return properties.getProperty("database.url");
    }

    public String getDatabaseUsername() {
        return properties.getProperty("database.username");
    }

    public String getDatabasePassword() {
        return properties.getProperty("database.password");
    }
}
