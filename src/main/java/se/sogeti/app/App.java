package se.sogeti.app;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sogeti.app.config.Settings;
import se.sogeti.app.database.Database;
import se.sogeti.app.tasks.BaseTask;
import se.sogeti.app.tasks.CategoryScraper;
import se.sogeti.app.tasks.ThreadExecutor;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

	private static final ThreadExecutor tpe = new ThreadExecutor(1, 1, 60000L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>());
	private static final Map<String, BaseTask> tasks = new HashMap<>();

	private static Settings settings;
	private static final Database DATABASE = new Database();

	private static boolean killAll = false;

	public static void main(String[] args) {
		settings = Settings.getInstance();
		settings.updateSettings();
		app();
	}

	private static void app() {
		try {
			// Adds the Category scraper as a task for ThreadExecutor to run. The Integer
			// argument is not in use yet, the 10 does nothing as of yet.
			addTask("ScrapeTask", new CategoryScraper(10, "ScrapeTask"));

			commenceTasking();
		} catch (Exception e) {
			LOGGER.error("app.Exception == {}", e.getMessage());
		}
	}

	/**
	 * Will start the whole process, essentially the scraper. Checks with API if it
	 * should be active, if the ScrapeTask exists and if ScrapeTask is already
	 * running or not. If advisable conditions then it will execute the ScrapeTask,
	 * otherwise it does nothing.
	 * 
	 * It will "sleep" for X seconds beginning of every iteration.
	 */
	private static void commenceTasking() {
		while (!killAll) {
			try {
				// How long it should wait until asking API if it should execute its task(s)
				sleep(60);

				boolean b = Boolean
						.parseBoolean(DATABASE.callGet(settings.getApiURL().concat("/api/status/isActive?value=cs")));

				if (b && tasks.containsKey("ScrapeTask") && !ThreadExecutor.contains("ScrapeTask")) {
					settings.updateSettings();
					// LOGGER.info(
					// "Advisable conditions! {\n\tisActive == {}\n\tTask exists == {}\n\tTask
					// isRunning == {}\n}\n",
					// b, tasks.containsKey("ScrapeTask"), ThreadExecutor.contains("ScrapeTask"));
					executeTask("ScrapeTask");

				} else {
					// LOGGER.info(
					// "Inadvisable conditions! {\n\tisActive == {}\n\tTask exists == {}\n\tTask
					// isRunning == {}\n}\n",
					// b, tasks.containsKey("ScrapeTask"), ThreadExecutor.contains("ScrapeTask"));
				}

			} catch (Exception e) {
				LOGGER.error("commenceTask().Exception == {}", e.getMessage());
			}
		}
	}

	private static void addTask(String s, BaseTask e) {
		tasks.put(s, e);
	}

	private static void executeTask(String s) {
		tpe.execute(tasks.get(s));
	}

	private static Boolean isTaskQueued(String s) {
		return tpe.getQueue().contains(tasks.get(s));
	}

	private static void sleep(long seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			LOGGER.error("sleep.InterruptedException == {}", e.getMessage());
			Thread.currentThread().interrupt();
		}
	}
}
