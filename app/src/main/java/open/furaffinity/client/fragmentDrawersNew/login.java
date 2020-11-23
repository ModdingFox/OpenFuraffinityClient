package open.furaffinity.client.fragmentDrawersNew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.HashMap;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.pages.loginCheck;

public class login extends appFragment {
    private WebView webView;
    private WebSettings webSettings;

    private static final String loginPath = "/login";

    private loginCheck loginCheck;

    @Override
    protected int getLayout() {
        return R.layout.fragment_login;
    }

    protected void getElements(View rootView) {
        webView = rootView.findViewById(R.id.webView);
        webSettings = webView.getSettings();
    }

    protected void fetchPageData() {
        loginCheck.execute();
    }

    @Override
    protected void updateUIElements() {

    }

    @Override
    protected void updateUIElementListeners(View rootView) {

    }

    protected void initPages() {
        loginCheck = new loginCheck(getActivity(), new abstractPage.pageListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (((loginCheck)abstractPage).getIsLoggedIn()) {
                    webView.setVisibility(View.GONE);

                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();

                    SharedPreferences sharedPref = requireContext().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.remove(getString(R.string.webClientCookieA));
                    editor.remove(getString(R.string.webClientCookieB));
                    editor.apply();
                    editor.commit();

                    ((mainActivity)requireActivity()).updateUILoginState();
                } else {
                    webView.setVisibility(View.VISIBLE);
                    webSettings.setJavaScriptEnabled(true);

                    //noinspection deprecation
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
                            String[] cookieEntries = cookies.split(";");
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
                                ((mainActivity)requireActivity()).updateUILoginState();
                            }
                        }
                    });

                    webView.loadUrl(open.furaffinity.client.utilities.webClient.getBaseUrl() + loginPath);
                }
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to load data for loginCheck", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
