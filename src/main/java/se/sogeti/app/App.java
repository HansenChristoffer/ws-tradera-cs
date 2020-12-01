package se.sogeti.app;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sogeti.app.config.Settings;
import se.sogeti.app.scrapers.CategoryScraper;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) {
		Settings settings = Settings.getInstance();
		settings.updateSettings();
		app();
	}

	private static void app() {
		try {
			CategoryScraper cScraper = new CategoryScraper();
			cScraper.run();
		} catch (Exception e) {
			LOGGER.error("app.Exception == {}", e.getMessage());
		}
	}
}
