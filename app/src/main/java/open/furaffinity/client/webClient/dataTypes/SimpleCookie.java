package open.furaffinity.client.webClient.dataTypes;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public final class SimpleCookie {
    private static final String COOKIE_STRING_FORMAT = "%1s=%2s";

    private final String key;
    private final String value;

    public SimpleCookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Cookie getCookie(HttpUrl httpUrl) {
        return Cookie.parse(
            httpUrl,
            String.format(COOKIE_STRING_FORMAT, this.key, this.value)
        );
    }

    public String getKey() {
        return this.key;
    }
}
