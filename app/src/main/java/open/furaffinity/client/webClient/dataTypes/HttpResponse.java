package open.furaffinity.client.webClient.dataTypes;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;
import androidx.annotation.NonNull;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import open.furaffinity.client.webClient.constants.LogMessages;

public final class HttpResponse {
    public static final int REQUEST_FAILURE_RESPONSE_CODE = -1;

    private static final String TAG = HttpResponse.class.getName();
    private static final String SET_COOKIE_HEADER = "set-cookie";

    private final int responseCode;
    private final String responseData;
    private final Headers headers;
    private final List<Cookie> requestSetCookies;

    public HttpResponse(@NonNull Response response) {
        String responseDataTmp = null;
        int responseCodeTmp = REQUEST_FAILURE_RESPONSE_CODE;
        Headers headersTmp = null;
        requestSetCookies = new ArrayList<>();

        try {
            final ResponseBody responseBody = response.body();
            if (responseBody != null) {
                responseDataTmp = responseBody.string();
                responseCodeTmp = response.code();
                headersTmp = response.headers();

                final List<String> headersSetCookieTmp = response.headers(SET_COOKIE_HEADER);
                for (String currentCookie : headersSetCookieTmp) {
                    final Response networkResponse = response.networkResponse();

                    if (networkResponse == null) {
                        Log.e(
                            TAG,
                            String.format(LogMessages.IS_NULL, Response.class.getName())
                        );
                    }
                    else {
                        final Cookie cookie =
                            Cookie.parse(networkResponse.request().url(), currentCookie);

                        if (cookie != null) {
                            requestSetCookies.add(cookie);
                        }
                    }
                }
            }
            else {
                Log.e(TAG, LogMessages.NO_RESPONSE_BODY);
            }
        }
        catch (IOException ex) {
            Log.e(TAG, LogMessages.HTTP_RESPONSE_FAILED, ex);
        }

        this.responseData = responseDataTmp;
        this.responseCode = responseCodeTmp;
        this.headers = headersTmp;
    }

    public Document getDocument() {
        Document result = null;
        if (this.responseData != null && this.responseCode == HttpURLConnection.HTTP_OK) {
            result = Jsoup.parse(this.responseData);
        }
        return result;
    }

    public Headers getHeaders() {
        return headers;
    }

    public List<Cookie> getRequestSetCookies() {
        return new ArrayList<>(requestSetCookies);
    }

    public int getResponseCode() {
        return responseCode;
    }
}
