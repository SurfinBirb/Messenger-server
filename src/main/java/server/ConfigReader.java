package server;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by SurfinBirb on 23.04.2017.
 */
public class ConfigReader {
    private static volatile ConfigReader instance;

    public static ConfigReader getInstance() throws Exception {
        ConfigReader localInstance = instance;
        if (localInstance == null) {
            synchronized (ConfigReader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ConfigReader();
                }
            }
        }
        return localInstance;
    }

private Config config;

    /**
     * Configuration file reader
     * @throws FileNotFoundException
     */
    public ConfigReader() throws FileNotFoundException {
        XStream xstream =  new XStream();
        File configFile = new File("src/main/java/config/config.xml");
        xstream.alias("ServerConfiguration", ServerConfiguration.class);
        xstream.alias("config", Config.class);
        this.config = (Config) xstream.fromXML(configFile);
    }

    public Config getConfig() {
        return config;
    }
}
