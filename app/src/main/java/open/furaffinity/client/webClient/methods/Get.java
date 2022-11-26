package open.furaffinity.client.webClient.methods;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import open.furaffinity.client.webClient.dataTypes.HttpResponse;

public class Get implements Callable<HttpResponse> {
    private final OkHttpClient okHttpClient;
    private final String url;

    public Get(
        @NonNull CookieJar cookieJar,
        long timeoutMillis,
        @NonNull String url
    ) {
        this.url = url;
        this.okHttpClient = new OkHttpClient
            .Builder()
            .callTimeout(Duration.ofMillis(timeoutMillis))
            .cookieJar(cookieJar)
            .build();
    }

    @SuppressFBWarnings("THROWS_METHOD_THROWS_CLAUSE_BASIC_EXCEPTION")
    @Override public HttpResponse call() throws IOException, IllegalStateException {
        final Request request = new Request.Builder()
            .url(url)
            .build();
        final Response response = okHttpClient.newCall(request).execute();
        final HttpResponse httpResponse = new HttpResponse(response);
        response.close();
        return httpResponse;
    }
}
