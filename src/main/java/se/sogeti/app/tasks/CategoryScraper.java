package se.sogeti.app.tasks;

import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sogeti.app.config.Settings;
import se.sogeti.app.database.Database;
import se.sogeti.app.models.Category;
import se.sogeti.app.models.dto.CategoryDTO;

public class CategoryScraper extends BaseTask {

    public CategoryScraper(long n, String id) {
        super(n, id);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final Database database = new Database();
    private final Settings settings = Settings.getInstance();

    @Override
    public void run() {
        settings.updateSettings();

        Date d = new Date();
        DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");

        long startTime = System.currentTimeMillis();
        d.setTime(startTime);
        Gson gson = new Gson();

        LOGGER.info("Starting task {} at {}, this will take awhile...", super.id, df.format(d));
        LOGGER.info("Fetching top level categories...");
        JsonArray jsonArray = JsonParser.parseString(database.callGet(settings.getBaseUrl().concat("/categories")))
                .getAsJsonArray();

        Set<Category> categories = new HashSet<>();
        Set<CategoryDTO> nodes = new HashSet<>();

        jsonArray.forEach(obj -> categories.add(gson.fromJson(obj.getAsJsonObject(), Category.class)));
        LOGGER.info("Done fetching!");

        LOGGER.info("Fetching low level categories...");
        categories.forEach(c -> c.getCategoryNodes().forEach(node -> {
            if (!node.isTopLevel() && !node.getHref().contains("rabatt")) {

                JsonObject activeCategory = JsonParser
                        .parseString(database.callGet(settings.getBaseUrl().concat(node.getHref()).concat(".json")))
                        .getAsJsonObject().get("filters").getAsJsonObject().get("categoryFilter").getAsJsonObject()
                        .get("activeCategory").getAsJsonObject();

                JsonArray children = activeCategory.get("children").getAsJsonArray();

                if (children.size() != 0) {
                    children.forEach(child -> {
                        JsonObject obj = child.getAsJsonObject();
                        nodes.add(new CategoryDTO(obj.get("name").getAsString(), obj.get("url").getAsString(), true));
                    });
                } else {
                    nodes.add(new CategoryDTO(activeCategory.get("name").getAsString(),
                            activeCategory.get("url").getAsString(), true));
                }
            }
        }));
        LOGGER.info("Done fetching!");

        LOGGER.info("Sending list of CategoryDTO!");
        Set<CategoryDTO> responseCategories = database.postMultiple(nodes,
                settings.getApiURL().concat("/api/categories/all"));
        LOGGER.info("Done sending!");

        long endTime = System.currentTimeMillis();
        d.setTime(endTime);

        LOGGER.info("Response stats : Size == {}, ", responseCategories.size());
        LOGGER.info("Toggled active, active == {}", database.toggleActive());
        LOGGER.info("Ending task {} at {} after {} s", super.id, df.format(d), (endTime - startTime) / 1000);
    }

}
