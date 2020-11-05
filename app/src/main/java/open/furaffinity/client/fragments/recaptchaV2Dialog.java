package open.furaffinity.client.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;

public class recaptchaV2Dialog extends DialogFragment {
    private String TAG = recaptchaV2Dialog.class.getName();

    private WebView webView;
    private WebSettings webSettings;

    private String pagePath;

    public interface recaptchaV2DialogListener {
        public void gRecaptchaResponseFound(String gRecaptchaResponse);
    }

    private recaptchaV2DialogListener listener;

    public void setListener(recaptchaV2DialogListener recaptchaV2DialogListener) {
        listener = recaptchaV2DialogListener;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.fragment_web_view, null);
        rootView.setVisibility(View.INVISIBLE);

        webView = rootView.findViewById(R.id.webView);
        webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        class WebAppInterface {
            Context mContext;

            WebAppInterface(Context c) {
                mContext = c;
            }

            @JavascriptInterface
            public void passGRecaptchaResponse(String gRecaptchaResponse) {
                dismiss();
                listener.gRecaptchaResponseFound(gRecaptchaResponse);
            }

            @JavascriptInterface
            public void setWebViewVisible() {
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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.evaluateJavascript("if(document.getElementById(\"g-recaptcha\") == null) { Android.passGRecaptchaResponse(\"\"); } else { " +
                        "document.body.innerHTML = '<div id=\"g-recaptcha\" style=\"display: none;\" class=\"g-recaptcha\" data-sitekey=\"6LcQyPMUAAAAAN-wUp7pQ81ex5U7BpnG2bQHKClm\" data-size=\"invisible\"></div>'; " +
                        "grecaptcha.render('g-recaptcha', { 'sitekey' : '6LcQyPMUAAAAAN-wUp7pQ81ex5U7BpnG2bQHKClm', 'badge'   : 'bottomright', 'size'    : 'invisible', 'theme'   : 'dark', 'callback': function(){ Android.passGRecaptchaResponse(document.getElementsByClassName('g-recaptcha-response')[0].value); }, 'expired-callback': window['recaptcha_error_callback'], 'error-callback'  : window['recaptcha_error_callback']}); " +
                        "grecaptcha.execute(); " +
                        "Android.setWebViewVisible(); }", null);
                return;
            }
        });

        builder.setView(rootView);

        return builder.create();
    }
}
