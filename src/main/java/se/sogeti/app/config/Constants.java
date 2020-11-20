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
    public static final String FILTER_SETTINGS_URL = "?sortBy=AddedOn&sellerType=Private";
    public static final String DEFAULT_DATABASE_IP = "172.17.0.2";
    public static final String DEFAULT_DATABASE_PORT = "27017";
    public static final String DRIVER_RUNNER = "local";
    public static final long PAGE_LOAD_TIMEOUT = 45;
    public static final long IMPLICIT_WAIT_TIMEOUT = 10;
    public static final String DRIVERS_PATH = "src/main/resources/drivers/";
    public static final String SCREENSHOT_PATH = "src/main/resources/logs/";

    public static String databaseIp = DEFAULT_DATABASE_IP;
    public static String databasePort = DEFAULT_DATABASE_PORT;

    public static final String NEXT_BUTTON_SELECTOR = "body > div.site-container.bp > div.site-main > div.search-results-page.site-width > div > div > div.col-md-9.col-sm-12.search-results-wrapper > div.pb-3 > nav > ul > li:nth-child(3) > a";
    public static final String SELECT_LINK_PATTERN = "a[id*=item_]";
    public static final String BUTTON_COOKIES_SELECTOR = "//*[@id=\"qc-cmp2-ui\"]/div[2]/div/button[2]";
    public static final String ZERO_DATA_RESULTS = "article.data-zero-result";
    public static final String CATEGORY_TITLE_ON_LINK_PAGE = "body > div.site-container.bp > div.site-main > div.search-results-page.site-width > div > div > div.col-md-9.col-sm-12.search-results-wrapper > header > div.search-results-header__heading.row.justify-content-between.pb-3.py-md-3 > div.col-12.d-flex.align-items-center.justify-content-center.justify-content-md-start > h1";
    public static final String ATTR_CONTENT = "content";
    public static final String ZDT_FORMAT_END_PATTERN = ":01.1+01:00[Europe/Paris]";
    private static ZonedDateTime dateTimeNow;

    public static final String INTERNAL_USER_AGENT = "Scraper HttpClient JDK11+";
    public static final String EXTERNAL_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36";

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
