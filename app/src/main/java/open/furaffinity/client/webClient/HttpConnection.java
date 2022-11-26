package open.furaffinity.client.webClient;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jsoup.nodes.Document;

import android.util.Log;
import androidx.annotation.NonNull;
import okhttp3.CookieJar;
import okhttp3.RequestBody;
import open.furaffinity.client.webClient.constants.LogMessages;
import open.furaffinity.client.webClient.dataTypes.HttpResponse;
import open.furaffinity.client.webClient.methods.Get;
import open.furaffinity.client.webClient.methods.Post;

public final class HttpConnection {
    private static final String TAG = HttpConnection.class.getName();

    private final CookieJar cookieJar;
    private final long timeoutMillis;
    private final long maxRetries;

    public HttpConnection(
        CookieJar cookieJar,
        long timeoutMillis,
        long maxRetries
    ) {
        this.cookieJar = cookieJar;
        this.timeoutMillis = timeoutMillis;
        this.maxRetries = maxRetries;
    }

    private HttpResponse sendRequest(
        @NonNull Callable<HttpResponse> webClientResponseCallable
    ) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        HttpResponse httpResponse = null;

        try {
            for (int id = 0; id < this.maxRetries; id++) {
                try {
                    httpResponse =
                        executorService
                            .submit(webClientResponseCallable)
                            .get(this.timeoutMillis, TimeUnit.MILLISECONDS);
                    break;
                }
                catch (ExecutionException | TimeoutException ex) {
                    Log.e(TAG, LogMessages.RETRYABLE_REQUEST_FAILED, ex);
                }
            }

            if (httpResponse == null) {
                Log.w(TAG, LogMessages.RETRYABLE_REQUEST_LIMIT_HIT);
            }
        }
        catch (InterruptedException ex) {
            Log.e(TAG, LogMessages.REQUEST_INTERRUPTED, ex);
        }
        finally {
            executorService.shutdown();
        }

        return httpResponse;
    }

    public Document sendGetRequest(@NonNull String url) {
        Document document = null;

        final HttpResponse httpResponse = this.sendRequest(
            new Get(
                this.cookieJar,
                timeoutMillis,
                url
            )
        );

        if (httpResponse == null) {
            Log.e(TAG, LogMessages.HTTP_REQUEST_FAILED);
        }
        else {
            document = httpResponse.getDocument();

            if (document == null) {
                Log.e(TAG, LogMessages.REQUEST_NO_DOCUMENT);
            }
        }

        return document;
    }

    public Document sendPostRequest(@NonNull String url, @NonNull RequestBody requestBody) {
        Document document = null;

        final HttpResponse httpResponse = this.sendRequest(
            new Post(
                this.cookieJar,
                requestBody,
                timeoutMillis,
                url
            )
        );

        if (httpResponse == null) {
            Log.e(TAG, LogMessages.HTTP_REQUEST_FAILED);
        }
        else {
            document = httpResponse.getDocument();

            if (document == null) {
                Log.e(TAG, LogMessages.REQUEST_NO_DOCUMENT);
            }
        }

        return document;
    }
}
