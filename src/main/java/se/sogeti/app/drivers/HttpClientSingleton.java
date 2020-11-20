package se.sogeti.app.drivers;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;

public class HttpClientSingleton {
    private static volatile HttpClient instance = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
            .followRedirects(Redirect.ALWAYS).build();

    private static boolean flag = true;

    private HttpClientSingleton() {
        if (flag) {
            flag = false;
        } else {
            throw new IllegalStateException("Already initialized.");
        }
    }

    public static HttpClient getInstance() {
        var result = instance;
        if (result == null) {
            synchronized (HttpClientSingleton.class) {
                result = instance;
                if (result == null) {
                    instance = result = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
                            .followRedirects(Redirect.ALWAYS).build();
                }
            }
        }
        return result;
    }
}