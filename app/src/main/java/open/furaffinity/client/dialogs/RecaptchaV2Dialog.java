package open.furaffinity.client.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;

public class RecaptchaV2Dialog extends DialogFragment {
    private WebView webView;

    private String pagePath;
    private RecaptchaV2DialogListener listener;

    public void setListener(RecaptchaV2DialogListener recaptchaV2DialogListener) {
        listener = recaptchaV2DialogListener;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    @SuppressLint("SetJavaScriptEnabled") @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
            getActivity(),
            android.R.style.Theme_DeviceDefault_DialogWhenLarge);

        final View rootView = requireActivity()
            .getLayoutInflater()
            .inflate(R.layout.fragment_web_view, null);
        rootView.setVisibility(View.INVISIBLE);

        webView = rootView.findViewById(R.id.webView);
        final WebSettings webSettings = webView.getSettings();

        webView.setBackgroundColor(Color.TRANSPARENT);
        webSettings.setJavaScriptEnabled(true);

        class WebAppInterface {
            final Context mContext;

            WebAppInterface(Context context) {
                mContext = context;
            }

            @JavascriptInterface public void passGRecaptchaResponse(String gRecaptchaResponse) {
                dismiss();
                listener.gRecaptchaResponseFound(gRecaptchaResponse);
            }

            @JavascriptInterface public void setWebViewVisible() {
                rootView.setVisibility(View.VISIBLE);
            }
        }

        webView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");

        webView.loadUrl(pagePath);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.evaluateJavascript(
                    "document.head.innerHTML = \"\"; document.body.innerHTML = '<div "
                        + "id=\"g-recaptcha\" style=\"display: none;\" class=\"g-recaptcha\" "
                        + "data-sitekey=\"6LcQyPMUAAAAAN-wUp7pQ81ex5U7BpnG2bQHKClm\" "
                        + "data-size=\"compact\" data-theme=\"dark\"></div>'; ",
                    value1 -> {
                        webView.evaluateJavascript(
                            "function onloadCallback() { grecaptcha.execute(grecaptcha.render"
                                + "('g-recaptcha', { 'sitekey' : "
                                + "'6LcQyPMUAAAAAN-wUp7pQ81ex5U7BpnG2bQHKClm', 'badge'   : "
                                + "'bottomright', 'size'    : 'invisible', 'theme'   : 'dark', "
                                + "'callback': function(){ Android.passGRecaptchaResponse(document"
                                + ".getElementsByClassName('g-recaptcha-response')[0].value); }, "
                                + "'expired-callback': window['recaptcha_error_callback'], "
                                + "'error-callback'  : window['recaptcha_error_callback']})); }",
                            value2 -> {
                                webView.evaluateJavascript(
                                    "var body = document.getElementsByTagName('body')[0]; "
                                        + "var script= "
                                        + "document.createElement('script'); script.type= "
                                        + "'text/javascript'; script.src= 'https://www.google"
                                        + ".com/recaptcha/api.js"
                                        + "?onload=onloadCallback&render=explicit'; "
                                        + "body.appendChild(script); Android.setWebViewVisible();",
                                    null);
                            });
                    });
            }
        });

        builder.setView(rootView);

        return builder.create();
    }

    public interface RecaptchaV2DialogListener {
        void gRecaptchaResponseFound(String gRecaptchaResponse);
    }
}
