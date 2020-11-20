package se.sogeti.app.controllers;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sogeti.app.config.Constants;
import se.sogeti.app.drivers.HttpClientSingleton;
import se.sogeti.app.models.dto.CategoryDTO;

public class Controller<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final Gson gson;

    private final HttpClient client = HttpClientSingleton.getInstance();

    public Controller() {
        this.gson = new GsonBuilder().setLenient().create();
    }

    public CategoryDTO getOpenCategory() {
        return gson.fromJson(callGet("http://".concat(Constants.databaseIp).concat(":").concat(Constants.databasePort)
                .concat("/api/categories/open")), CategoryDTO.class);
    }

    public Set<T> postMultiple(Set<T> objects, String uri) {
        Set<T> responseObjects = new HashSet<>();

        try {
            String bodyJson = gson.toJson(objects);
            LOGGER.info("Objects size == {}", objects.size());

            HttpRequest request = HttpRequest.newBuilder().POST(BodyPublishers.ofString(bodyJson)).uri(URI.create(uri))
                    .header("Content-Type", "application/json").header("User-Agent", Constants.INTERNAL_USER_AGENT)
                    .build();

            Type listType;
            String className = objects.toArray()[0].getClass().getName();

            if (className.contains("Category")) {
                listType = new TypeToken<Set<CategoryDTO>>() {
                }.getType();
            } else {
                listType = new TypeToken<Set<Object>>() {
                }.getType();
            }

            CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
                    HttpResponse.BodyHandlers.ofString());

            response.join();

            responseObjects = gson.fromJson(response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS), listType);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            LOGGER.error("postMultiple.InterruptedException == {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LOGGER.info("postMultiple.Exception == {}", e.getMessage());
        }

        return responseObjects;
    }

    public String getPublished(String objectNumber) {
        return JsonParser.parseString(callGet(Constants.BASE_URL.concat("/item/").concat(objectNumber).concat(".json")))
                .getAsJsonObject().get("itemDetails").getAsJsonObject().get("startDate").getAsString()
                .concat("[Europe/Paris]");
    }

    public String callGet(String href) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(href))
                .setHeader("User-Agent", Constants.EXTERNAL_USER_AGENT).build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());

        response.join();

        String bodyJson = "";

        try {
            bodyJson = response.thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            LOGGER.error("callGet.InterruptedException == {}", ie.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            LOGGER.error("callGet.ExecutionException == {}", ee.getMessage());
        } catch (TimeoutException te) {
            LOGGER.error("callGet.TimeoutException == {}", te.getMessage());
        }

        return bodyJson;
    }

}
