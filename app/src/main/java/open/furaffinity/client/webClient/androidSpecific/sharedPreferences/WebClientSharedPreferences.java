package open.furaffinity.client.webClient.androidSpecific.sharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.SharedPreferences;
import com.google.common.collect.ImmutableSet;

public final class WebClientSharedPreferences {
    public static final String SHARED_PREFERENCE_FILE_NAME =
        WebClientSharedPreferences.class.getName();

    public static final String SHARED_PREFERENCE_COOKIE_PREFERENCE_PREFIX_KEY =
        "cookie_preference_prefix";
    public static final String SHARED_PREFERENCE_SAVABLE_LOGIN_COOKIE_NAMES_KEY =
        "shared_preference_savable_login_cookie_names";
    public static final String SHARED_PREFERENCE_REQUEST_TIMEOUT_MILLIS_KEY =
        "request_timeout_millis ";
    public static final String SHARED_PREFERENCE_REQUEST_MAX_RETRIES_KEY =
        "request_max_retries";

    public static final String SHARED_PREFERENCE_COOKIE_PREFERENCE_PREFIX_DEFAULT = "cookie";
    public static final Set<String> SHARED_PREFERENCE_SAVABLE_LOGIN_COOKIE_NAMES_DEFAULT =
        ImmutableSet.copyOf(new HashSet<>(Arrays.asList("a", "b")));
    public static final long SHARED_PREFERENCE_REQUEST_TIMEOUT_MILLIS_DEFAULT = 5000;
    public static final long SHARED_PREFERENCE_REQUEST_MAX_RETRIES_DEFAULT = 3;

    private final SharedPreferences sharedPreferences;

    public WebClientSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getCookiePreferencePrefix() {
        return this.sharedPreferences.getString(
            SHARED_PREFERENCE_COOKIE_PREFERENCE_PREFIX_KEY,
            SHARED_PREFERENCE_COOKIE_PREFERENCE_PREFIX_DEFAULT
        );
    }

    public Set<String> getSavableLoginCookieNames() {
        return this.sharedPreferences.getStringSet(
            SHARED_PREFERENCE_SAVABLE_LOGIN_COOKIE_NAMES_KEY,
            SHARED_PREFERENCE_SAVABLE_LOGIN_COOKIE_NAMES_DEFAULT
        );
    }

    public Long getSharedPreferenceRequestTimeoutMillis() {
        return this.sharedPreferences.getLong(
            SHARED_PREFERENCE_REQUEST_TIMEOUT_MILLIS_KEY,
            SHARED_PREFERENCE_REQUEST_TIMEOUT_MILLIS_DEFAULT
        );
    }

    public Long getRequestMaxRetries() {
        return this.sharedPreferences.getLong(
            SHARED_PREFERENCE_REQUEST_MAX_RETRIES_KEY,
            SHARED_PREFERENCE_REQUEST_MAX_RETRIES_DEFAULT
        );
    }

    public void setCookiePreferencePrefix(String cookiePreferencePrefix) {
        this.sharedPreferences
            .edit()
            .putString(
                SHARED_PREFERENCE_COOKIE_PREFERENCE_PREFIX_KEY,
                cookiePreferencePrefix
            )
            .apply();
    }

    public void setSavableLoginCookieNames(Set<String> savableLoginCookieNames) {
        this.sharedPreferences
            .edit()
            .putStringSet(
                SHARED_PREFERENCE_SAVABLE_LOGIN_COOKIE_NAMES_KEY,
                savableLoginCookieNames
            )
            .apply();
    }

    public void setSharedPreferenceRequestTimeoutMillis(Long sharedPreferenceRequestTimeoutMillis) {
        this.sharedPreferences
            .edit()
            .putLong(
                SHARED_PREFERENCE_REQUEST_TIMEOUT_MILLIS_KEY,
                sharedPreferenceRequestTimeoutMillis
            )
            .apply();
    }

    public void setRequestMaxRetries(Long requestMaxRetries) {
        this.sharedPreferences
            .edit()
            .putLong(
                SHARED_PREFERENCE_REQUEST_MAX_RETRIES_KEY,
                requestMaxRetries
            )
            .apply();
    }
}
