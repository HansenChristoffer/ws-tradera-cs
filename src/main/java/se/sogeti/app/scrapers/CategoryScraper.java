package se.sogeti.app.scrapers;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sogeti.app.config.Constants;
import se.sogeti.app.database.Database;
import se.sogeti.app.models.Category;
import se.sogeti.app.models.dto.CategoryDTO;

public class CategoryScraper {

    public CategoryScraper() {
        super();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final Database<CategoryDTO> database = new Database<>();

    public void run() {
        long millisTime = System.currentTimeMillis();
        Gson gson = new Gson();

        LOGGER.info("Starting category scraping, this will take awhile...");

        JsonArray jsonArray = JsonParser.parseString(database.callGet(Constants.BASE_URL.concat("/categories")))
                .getAsJsonArray();

        Set<Category> categories = new HashSet<>();
        Set<CategoryDTO> nodes = new HashSet<>();

        jsonArray.forEach(obj -> {
            categories.add(gson.fromJson(obj.getAsJsonObject(), Category.class));
        });

        categories.forEach(c -> {
            c.getCategoryNodes().forEach(node -> {

                if (!node.getTitle().contains("Allt inom ") && !node.getHref().contains("rabatt")) {
                    JsonArray children = JsonParser
                            .parseString(database.callGet(Constants.BASE_URL.concat(node.getHref()).concat(".json")))
                            .getAsJsonObject().get("filters").getAsJsonObject().get("categoryFilter").getAsJsonObject()
                            .get("categoryTree").getAsJsonObject().get("children").getAsJsonArray();

                    if (children.get(0).getAsJsonObject().keySet().contains("children")) {
                        JsonArray secondChild = children.get(0).getAsJsonObject().get("children").getAsJsonArray();

                        secondChild.forEach(index -> {
                            JsonObject obj = index.getAsJsonObject();
                            nodes.add(
                                    new CategoryDTO(obj.get("name").getAsString(), obj.get("url").getAsString(), true));
                        });
                    } else {
                        children.forEach(index -> {
                            JsonObject obj = index.getAsJsonObject();
                            nodes.add(
                                    new CategoryDTO(obj.get("name").getAsString(), obj.get("url").getAsString(), true));
                        });
                    }
                }
            });
        });

        Set<CategoryDTO> responseCategories = database.postMultiple(nodes, "http://".concat(Constants.databaseIp)
                .concat(":").concat(Constants.databasePort).concat("/api/categories/all"));

        responseCategories.forEach(value -> LOGGER.info("{}", value));

        LOGGER.info("Response stats : Size == {}, ", responseCategories.size());
        LOGGER.info("Elapsed time: {}s", (System.currentTimeMillis() - millisTime) / 1000);
    }

}
