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

        JsonArray jsonArray = JsonParser.parseString(database.callGet(settings.getBaseUrl().concat("/categories")))
                .getAsJsonArray();

        Set<Category> categories = new HashSet<>();
        Set<CategoryDTO> nodes = new HashSet<>();

        jsonArray.forEach(obj -> categories.add(gson.fromJson(obj.getAsJsonObject(), Category.class)));

        categories.forEach(c -> c.getCategoryNodes().forEach(node -> {

            if (!node.getTitle().contains("Allt inom ") && !node.getHref().contains("rabatt")) {
                JsonArray children = JsonParser
                        .parseString(database.callGet(settings.getBaseUrl().concat(node.getHref()).concat(".json")))
                        .getAsJsonObject().get("filters").getAsJsonObject().get("categoryFilter").getAsJsonObject()
                        .get("categoryTree").getAsJsonObject().get("children").getAsJsonArray();

                if (children.get(0).getAsJsonObject().keySet().contains("children")) {
                    JsonArray secondChild = children.get(0).getAsJsonObject().get("children").getAsJsonArray();

                    secondChild.forEach(index -> {
                        JsonObject obj = index.getAsJsonObject();
                        nodes.add(new CategoryDTO(obj.get("name").getAsString(), obj.get("url").getAsString(), true));
                    });
                } else {
                    children.forEach(index -> {
                        JsonObject obj = index.getAsJsonObject();
                        nodes.add(new CategoryDTO(obj.get("name").getAsString(), obj.get("url").getAsString(), true));
                    });
                }
            }
        }));

        Set<CategoryDTO> responseCategories = database.postMultiple(nodes,
                settings.getApiURL().concat("/api/categories/all"));

        long endTime = System.currentTimeMillis();
        d.setTime(endTime);

        LOGGER.info("Response stats : Size == {}, ", responseCategories.size());
        LOGGER.info("Ending task {} at {} after {} s", super.id, df.format(d), (endTime - startTime) / 1000);

        boolean b = database.toggleActive();
        LOGGER.info("Toggled active, active == {}", b);
    }

}
