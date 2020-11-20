package se.sogeti.app;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sogeti.app.config.Constants;
import se.sogeti.app.scrapers.BaseScraper;
import se.sogeti.app.scrapers.CategoryScraper;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static Set<BaseScraper> scrapers = new HashSet<>();


	public static void main(String[] args) {
		Constants.init();
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




	private static void sleep(long seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			LOGGER.error("sleep.InterruptedException == {}", e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	private static void kill() {
		try {
			if (!scrapers.isEmpty()) {
				scrapers.forEach(s -> s.kill());
				scrapers.clear();
			}
		} catch (Exception e) {
			LOGGER.info("kill().Exception == {}", e.getMessage());
		}
	}

}
