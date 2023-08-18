package ext.library.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * http 工具
 */
public class HttpUtils {
    static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    /**
     * 得到
     *
     * @param url url
     * @return {@link String}
     */
    public static <T> CompletableFuture<String> getAsync(String url, Class<T> tClass) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

    public static String get(String url) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> response;
        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    public static <T> T get(String url, Class<T> tClass) {
        return JsonUtils.parseObject(get(url), tClass);
    }
}