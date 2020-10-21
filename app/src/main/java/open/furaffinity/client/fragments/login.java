package open.furaffinity.client.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.utilities.webClient;

public class login extends Fragment {
    private static final String TAG = login.class.getName();

    WebView webView;
    WebSettings webSettings;

    private static final String loginPath = "/login";

    private webClient webClient;
    private loginTest loginTest;

    private void getElements(View rootView) {
        webView = rootView.findViewById(R.id.webView);
        webSettings = webView.getSettings();
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
        loginTest = new loginTest();
    }

    private void fetchPageData() {
        try {
            loginTest.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }
    }

    private void updateUIElements() {
        if (loginTest.getIsLoggedIn()) {
            webView.setVisibility(View.GONE);

            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();

            SharedPreferences sharedPref = requireContext().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(getString(R.string.webClientCookieA));
            editor.remove(getString(R.string.webClientCookieB));
            editor.apply();
            editor.commit();

            ((mainActivity)getActivity()).updateUILoginState();
        } else {
            webView.setVisibility(View.VISIBLE);
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
                        SharedPreferences sharedPref = requireContext().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.webClientCookieA), cookieMap.get("a"));
                        editor.putString(getString(R.string.webClientCookieB), cookieMap.get("b"));
                        editor.apply();
                        editor.commit();
                        ((mainActivity)getActivity()).updateUILoginState();
                    }

                    return;
                }
            });

            webView.loadUrl(webClient.getBaseUrl() + loginPath);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        return rootView;
    }
}
