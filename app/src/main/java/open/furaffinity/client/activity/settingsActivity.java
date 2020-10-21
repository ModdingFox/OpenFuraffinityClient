package open.furaffinity.client.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;

public class settingsActivity extends AppCompatActivity {
    private static final String TAG = settingsActivity.class.getName();
    private static final String loginPath = "https://www.furaffinity.net/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_web_view);

        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                CookieManager cookieManager = CookieManager.getInstance();
                String cookies = cookieManager.getCookie(url);
                List<String> cookieEntries = Arrays.asList(cookies.split(";"));
                HashMap<String, String> cookieMap = new HashMap<>();

                for (String currentElement : cookieEntries) {
                    String[] splitCookie = currentElement.split("=");
                    if (splitCookie.length == 2) {
                        cookieMap.put(splitCookie[0].trim(), splitCookie[1].trim());
                    }
                }

                if (cookieMap.containsKey("a") && cookieMap.containsKey("b")) {
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.webClientCookieA), cookieMap.get("a"));
                    editor.putString(getString(R.string.webClientCookieB), cookieMap.get("b"));
                    editor.apply();
                    editor.commit();
                    finish();
                }

                return;
            }
        });

        webView.loadUrl(loginPath);
    }
}
