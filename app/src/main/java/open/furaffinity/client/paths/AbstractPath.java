package open.furaffinity.client.paths;

import android.content.Context;
import android.content.SharedPreferences;
import open.furaffinity.client.webClient.HttpConnection;
import open.furaffinity.client.webClient.androidSpecific.SharedPrefCookieJar;
import open.furaffinity.client.webClient.androidSpecific.sharedPreferences.WebClientSharedPreferences;

public abstract class AbstractPath {
    protected final HttpConnection httpConnection;
    protected final SharedPrefCookieJar sharedPrefCookieJar;
    protected final WebClientSharedPreferences abstractPathSharedPreferences;

    public AbstractPath(Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(
            WebClientSharedPreferences.SHARED_PREFERENCE_FILE_NAME,
            Context.MODE_PRIVATE
        );

        abstractPathSharedPreferences =
            new WebClientSharedPreferences(sharedPreferences);

        this.sharedPrefCookieJar = new SharedPrefCookieJar(
            abstractPathSharedPreferences.getSavableLoginCookieNames(),
            abstractPathSharedPreferences.getCookiePreferencePrefix(),
            sharedPreferences
        );

        this.httpConnection = new HttpConnection(
            this.sharedPrefCookieJar,
            abstractPathSharedPreferences.getSharedPreferenceRequestTimeoutMillis(),
            abstractPathSharedPreferences.getRequestMaxRetries()
        );
    }
}
