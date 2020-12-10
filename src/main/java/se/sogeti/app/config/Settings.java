package se.sogeti.app.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sogeti.app.database.Database;

public class Settings {
    // Misc
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static volatile Settings instance = null;

    // DEFAULTS
    private static final String DEFAULT_BASE_URL = "https://www.tradera.com";
    // private static final String DEFAULT_API_URL =
    // "https://webscraperapi-1606300858222.azurewebsites.net";
    private static final String DEFAULT_API_URL = "http://192.168.0.145:8080";
    private static final String DEFAULT_CONFIG_PATH = "./config/";
    private static final String DEFAULT_INTERNAL_USER_AGENT = "Scraper HttpClient JDK11+";
    private static final String DEFAULT_EXTERNAL_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36";
    private static final long DEFAULT_ACTIVE_CALL_TIMEOUT = 120; // 2 seconds
    private static final String DEFAULT_TIME_ZONE_ID = "Europe/Paris";
    private static final int DEFAULT_API_CALL_PAUSE_TIMER = 30;
    // Scheduling related

    // Settings
    private String baseUrl = DEFAULT_BASE_URL;

    private String apiURL = DEFAULT_API_URL;
    private String apiVersion = "";

    private String configPath = DEFAULT_CONFIG_PATH;

    private String internalUserAgent = DEFAULT_INTERNAL_USER_AGENT;
    private String externalUserAgent = DEFAULT_EXTERNAL_USER_AGENT;

    private String timeZoneId = DEFAULT_TIME_ZONE_ID;
    private long activeCallTimeout = DEFAULT_ACTIVE_CALL_TIMEOUT;

    private int apiCallPauseTimer = DEFAULT_API_CALL_PAUSE_TIMER;

    private ZonedDateTime dateTimeNow;

    private static final String SETTINGS_FILE_PATH = DEFAULT_CONFIG_PATH.concat("categoryscraper-settings.xml");

    private Settings() {
        initFileStructure();
    }

    public static Settings getInstance() {
        if (instance == null) {
            synchronized (Settings.class) {
                if (instance == null) {
                    instance = new Settings();
                }
            }
        }

        return instance;
    }

    public void updateSettings() {
        LOGGER.info("Updating...");
        File fLss = new File(SETTINGS_FILE_PATH);

        if (!fLss.exists()) {
            fetchSettingsFile(fetchApiURL(DEFAULT_CONFIG_PATH.concat("DEFAULT.xml")));
        } else {
            fetchSettingsFile(fetchApiURL(SETTINGS_FILE_PATH));
        }

        try {
            LOGGER.info("Initilizing settings...");
            FileInputStream fis = new FileInputStream(new File(SETTINGS_FILE_PATH));

            Properties prop = getSortedPropertiesInstance();

            prop.loadFromXML(fis);

            baseUrl = prop.getProperty("base_url");

            apiURL = prop.getProperty("api_url");
            apiVersion = prop.getProperty("api_version");

            internalUserAgent = prop.getProperty("internal_user_agent");
            externalUserAgent = prop.getProperty("external_user_agent");

            activeCallTimeout = prop.getProperty("active_call_timeout") != null
                    ? Long.valueOf(prop.getProperty("active_call_timeout"))
                    : DEFAULT_ACTIVE_CALL_TIMEOUT;

            timeZoneId = prop.getProperty("time_zone_id") != null ? prop.getProperty("time_zone_id")
                    : DEFAULT_TIME_ZONE_ID;
            dateTimeNow = ZonedDateTime.now(ZoneId.of(timeZoneId));

            apiCallPauseTimer = Integer.valueOf(prop.getProperty("api_call_timer"));

            prop.setProperty("lastLoaded", dateTimeNow.toString());

            fis.close();

            FileOutputStream fos = new FileOutputStream(new File(SETTINGS_FILE_PATH));
            prop.storeToXML(fos, "Modified");

            fos.close();

            LOGGER.info("Initilization complete!");
            LOGGER.info("Update complete!");
        } catch (InvalidPropertiesFormatException e) {
            LOGGER.error("Error occured loading the properties file", e);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error occured loading the properties file", e);
        } catch (IOException e) {
            LOGGER.error("Error occured loading the properties file", e);
        }

    }

    private Properties getSortedPropertiesInstance() {
        return new Properties() {

            private static final long serialVersionUID = 1L;

            @Override
            public Set<Object> keySet() {
                return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
            }

            @Override
            public Set<Map.Entry<Object, Object>> entrySet() {

                Set<Map.Entry<Object, Object>> set1 = super.entrySet();
                Set<Map.Entry<Object, Object>> set2 = new LinkedHashSet<>(set1.size());

                Iterator<Map.Entry<Object, Object>> iterator = set1.stream()
                        .sorted(new Comparator<Map.Entry<Object, Object>>() {

                            @Override
                            public int compare(java.util.Map.Entry<Object, Object> o1,
                                    java.util.Map.Entry<Object, Object> o2) {
                                return o1.getKey().toString().compareTo(o2.getKey().toString());
                            }
                        }).iterator();

                while (iterator.hasNext())
                    set2.add(iterator.next());

                return set2;
            }

            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
    }

    private void fetchSettingsFile(String settingsURL) {
        LOGGER.info("Fetching newest settings from API");

        Database database = new Database();

        File settingsFile = new File(SETTINGS_FILE_PATH);
        settingsFile.getParentFile().mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(settingsFile, false))) {
            bw.write(database.callGet(settingsURL.concat("/api/settings?value=cs")));
            bw.flush();
            LOGGER.info("Fetching complete!");
        } catch (IOException e) {
            LOGGER.error("fetchSettingsFile().IOException == {}", e.getMessage());
        }
    }

    public String fetchApiURL(String settingsURL) {
        Properties prop = new Properties();

        try {
            FileInputStream fis = new FileInputStream(new File(settingsURL));
            prop.loadFromXML(fis);
            String str = prop.getProperty("api_url");
            fis.close();

            return str;
        } catch (InvalidPropertiesFormatException e) {
            LOGGER.error("Error occured loading the properties file", e);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error occured loading the properties file", e);
        } catch (IOException e) {
            LOGGER.error("Error occured loading the properties file", e);
        }

        return DEFAULT_API_URL;
    }

    private void initFileStructure() {
        File fCnf = new File(getConfigPath());
        File fDs = new File(DEFAULT_CONFIG_PATH.concat("DEFAULT.xml"));

        if (!fCnf.exists()) {
            LOGGER.info("fCnf not exist");
            fCnf.mkdirs();
        }

        if (!fDs.exists()) {
            LOGGER.info("fDs not exist");
            createDefaultSettingsFile();
        }
    }

    private void createDefaultSettingsFile() {
        Properties prop = getSortedPropertiesInstance();
        File f = new File(DEFAULT_CONFIG_PATH.concat("DEFAULT.xml"));

        f.setWritable(true);
        f.setReadable(true);

        try (FileOutputStream fos = new FileOutputStream(f)) {
            f.createNewFile();

            dateTimeNow = ZonedDateTime.now(ZoneId.of(DEFAULT_TIME_ZONE_ID));

            prop.setProperty("base_url", DEFAULT_BASE_URL);
            prop.setProperty("api_url", DEFAULT_API_URL);
            prop.setProperty("internal_user_agent", DEFAULT_INTERNAL_USER_AGENT);
            prop.setProperty("external_user_agent", DEFAULT_EXTERNAL_USER_AGENT);
            prop.setProperty("active_call_timeout", String.valueOf(DEFAULT_ACTIVE_CALL_TIMEOUT));
            prop.setProperty("time_zone_id", String.valueOf(DEFAULT_TIME_ZONE_ID));
            prop.setProperty("lastLoaded", dateTimeNow.toString());
            prop.setProperty("api_call_timer", String.valueOf(DEFAULT_API_CALL_PAUSE_TIMER));

            prop.storeToXML(fos, "DEFAULT");
        } catch (InvalidPropertiesFormatException e) {
            LOGGER.error("Error occured loading the properties file", e);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error occured loading the properties file", e);
        } catch (IOException e) {
            LOGGER.error("Error occured loading the properties file", e);
        } catch (Exception e) {
            LOGGER.error("createDefaultSettingsFile().Exception == {}", e.getMessage());
        }
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiURL() {
        return this.apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getApiVersion() {
        return this.apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getConfigPath() {
        return this.configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getInternalUserAgent() {
        return this.internalUserAgent;
    }

    public void setInternalUserAgent(String internalUserAgent) {
        this.internalUserAgent = internalUserAgent;
    }

    public String getExternalUserAgent() {
        return this.externalUserAgent;
    }

    public void setExternalUserAgent(String externalUserAgent) {
        this.externalUserAgent = externalUserAgent;
    }

    public long getActiveCallTimeout() {
        return this.activeCallTimeout;
    }

    public void setActiveCallTimeout(long activeCallTimeout) {
        this.activeCallTimeout = activeCallTimeout;
    }

    public String getTimeZoneId() {
        return this.timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public ZonedDateTime getDateTimeNow() {
        return this.dateTimeNow;
    }

    public void setDateTimeNow(ZonedDateTime dateTimeNow) {
        this.dateTimeNow = dateTimeNow;
    }

    public int getApiCallPauseTimer() {
        return this.apiCallPauseTimer;
    }

    public void setApiCallPauseTimer(int apiCallPauseTimer) {
        this.apiCallPauseTimer = apiCallPauseTimer;
    }

    @Override
    public String toString() {
        return "{" + " baseUrl='" + getBaseUrl() + "'" + ", apiURL='" + getApiURL() + "'" + ", apiVersion='"
                + getApiVersion() + "'" + ", configPath='" + getConfigPath() + "'" + ", internalUserAgent='"
                + getInternalUserAgent() + "'" + ", externalUserAgent='" + getExternalUserAgent() + "'"
                + ", timeZoneId='" + getTimeZoneId() + "'" + ", activeCallTimeout='" + getActiveCallTimeout() + "'"
                + ", apiCallPauseTimer='" + getApiCallPauseTimer() + "'" + ", dateTimeNow='" + getDateTimeNow() + "'"
                + "}";
    }

}
