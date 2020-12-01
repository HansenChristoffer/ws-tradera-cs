package se.sogeti.app.controllers;

import java.lang.invoke.MethodHandles;
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

import se.sogeti.app.config.Settings;
import se.sogeti.app.drivers.HttpClientSingleton;
import se.sogeti.app.models.dto.CategoryDTO;

public class Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final Gson gson;

    private final HttpClient client = HttpClientSingleton.getInstance();
    private final Settings settings = Settings.getInstance();

    public Controller() {
        this.gson = new GsonBuilder().setLenient().create();
    }

    public CategoryDTO getOpenCategory() {
        return gson.fromJson(callGet(settings.getApiURL().concat("/api/categories/open")), CategoryDTO.class);
    }

    public Set<CategoryDTO> postMultiple(Set<CategoryDTO> objects, String uri) {
        Set<CategoryDTO> responseObjects = new HashSet<>();

        try {
            String rtnBody = gson.toJson(objects);

            HttpRequest request = HttpRequest.newBuilder().POST(BodyPublishers.ofString(rtnBody)).uri(URI.create(uri))
                    .header("Content-Type", "application/json").header("User-Agent", settings.getInternalUserAgent())
                    .build();

            CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
                    HttpResponse.BodyHandlers.ofString());

            response.join();

            responseObjects = gson.fromJson(response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS),
                    new TypeToken<Set<CategoryDTO>>() {
                    }.getType());
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
        return JsonParser
                .parseString(callGet(settings.getBaseUrl().concat("/item/").concat(objectNumber).concat(".json")))
                .getAsJsonObject().get("itemDetails").getAsJsonObject().get("startDate").getAsString()
                .concat("[Europe/Paris]");
    }

    public String callGet(String href) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(href))
                .setHeader("User-Agent", settings.getExternalUserAgent()).build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());

        response.join();

        String rtnBody = "";

        try {
            rtnBody = response.thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            LOGGER.error("callGet.InterruptedException == {}", ie.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            LOGGER.error("callGet.ExecutionException == {}", ee.getMessage());
        } catch (TimeoutException te) {
            LOGGER.error("callGet.TimeoutException == {}", te.getMessage());
        }

        return rtnBody;
    }

    public Boolean toggleActive() {
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(settings.getApiURL().concat("/api/status/toggle?value=cs")))
                .setHeader("User-Agent", settings.getInternalUserAgent()).build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());

        response.join();

        String rtnBody = "";

        try {
            rtnBody = response.thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            LOGGER.error("callGet.InterruptedException == {}", ie.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            LOGGER.error("callGet.ExecutionException == {}", ee.getMessage());
        } catch (TimeoutException te) {
            LOGGER.error("callGet.TimeoutException == {}", te.getMessage());
        }

        return Boolean.parseBoolean(rtnBody);
    }

}
