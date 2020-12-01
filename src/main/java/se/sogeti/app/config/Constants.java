package se.sogeti.app.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Properties;

public class Constants {

    private Constants() {
    }

    public static final String BASE_URL = "https://www.tradera.com";
    public static final String DEFAULT_DATABASE_IP = "172.17.0.2";
    public static final String DEFAULT_DATABASE_PORT = "27017";

    public static String databaseIp = DEFAULT_DATABASE_IP;
    public static String databasePort = DEFAULT_DATABASE_PORT;

    public static final String INTERNAL_USER_AGENT = "Scraper HttpClient JDK11+";
    public static final String EXTERNAL_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36";

    private static ZonedDateTime dateTimeNow;

    public static void init() {
        dateTimeNow = ZonedDateTime.now(ZoneId.of("Europe/Paris"));

        try {
            Path dbPath = Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath().toString(),
                    "src/main/resources/config/database_config.xml");
            Properties dbProps = new Properties();
            dbProps.loadFromXML(new FileInputStream(dbPath.toFile()));

            databaseIp = dbProps.getProperty("database_ip", DEFAULT_DATABASE_IP);
            databasePort = dbProps.getProperty("database_port", DEFAULT_DATABASE_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ZonedDateTime getDateTimeNow() {
        return dateTimeNow;
    }

}
