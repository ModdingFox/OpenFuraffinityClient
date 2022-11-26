package open.furaffinity.client.webClient.androidSpecific;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import open.furaffinity.client.webClient.constants.LogMessages;
import open.furaffinity.client.webClient.dataTypes.SimpleCookie;

public final class SharedPrefCookieJar implements CookieJar {
    private static final String TAG = SharedPrefCookieJar.class.getName();

    private final Set<String> allowPersistCookies;
    private final String cookiePreferenceNamePrefix;
    private final List<SimpleCookie> nonPersistentCookies;
    private final SharedPreferences sharedPreferences;

    public SharedPrefCookieJar(
        Set<String> allowPersistCookies,
        String cookiePreferenceNamePrefix,
        SharedPreferences sharedPreferences
    ) {
        this.allowPersistCookies = new HashSet<>(allowPersistCookies);
        this.cookiePreferenceNamePrefix = cookiePreferenceNamePrefix;
        this.nonPersistentCookies = new ArrayList<>();
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull @Override public List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
        final List<Cookie> result = new ArrayList<>();

        for (String cookieName : this.allowPersistCookies) {
            final String cookiePreferenceName = this.cookiePreferenceNamePrefix + cookieName;
            final String cookieStringData = this.sharedPreferences.getString(
                cookiePreferenceName,
                null
            );
            if (cookieStringData != null) {
                final SimpleCookie simpleCookie = new SimpleCookie(cookieName, cookieStringData);
                result.add(simpleCookie.getCookie(httpUrl));
            }
            else {
                Log.d(TAG, String.format(LogMessages.NO_PREFERENCE_WITH_NAME, cookieName));
            }
        }

        result.addAll(
            nonPersistentCookies
                .stream()
                .map(cookie -> cookie.getCookie(httpUrl))
                .collect(Collectors.toList())
        );

        return result;
    }

    @Override public void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> list) {
        for (Cookie cookie : list) {
            if (this.allowPersistCookies.contains(cookie.name())) {
                final String cookiePreferenceName = this.cookiePreferenceNamePrefix + cookie.name();
                sharedPreferences.edit().putString(
                    cookiePreferenceName,
                    cookie.value()
                ).apply();
                Log.i(
                    TAG,
                    String.format(LogMessages.SAVED_COOKIE_WITH_NAME, cookie.name())
                );
            }
            else {
                Log.w(
                    TAG,
                    String.format(LogMessages.IGNORE_SET_COOKIE, cookie.name())
                );
            }
        }
    }

    public void clearPersistCookies() {
        for (String cookieName : this.allowPersistCookies) {
            final String cookiePreferenceName = this.cookiePreferenceNamePrefix + cookieName;
            this.sharedPreferences.edit().remove(cookiePreferenceName).apply();
        }
    }

    public List<String> getCurrentCookieNames() {
        final List<String> result = new ArrayList<>();

        for (String cookieName : this.allowPersistCookies) {
            final String cookiePreferenceName = this.cookiePreferenceNamePrefix + cookieName;
            final String cookieStringData = this.sharedPreferences.getString(
                cookiePreferenceName,
                null
            );
            if (cookieStringData != null) {
                result.add(cookieName);
            }
            else {
                Log.d(TAG, String.format(LogMessages.NO_PREFERENCE_WITH_NAME, cookieName));
            }
        }

        result.addAll(
            nonPersistentCookies
                .stream()
                .map(SimpleCookie::getKey)
                .collect(Collectors.toList())
        );

        return result;
    }

    public void setNonPersistentCookies(List<SimpleCookie> nonPersistentCookies) {
        this.nonPersistentCookies.clear();
        this.nonPersistentCookies.addAll(nonPersistentCookies);
    }
}
