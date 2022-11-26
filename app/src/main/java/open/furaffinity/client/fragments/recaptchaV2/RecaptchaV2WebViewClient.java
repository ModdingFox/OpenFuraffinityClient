package open.furaffinity.client.fragments.recaptchaV2;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RecaptchaV2WebViewClient extends WebViewClient {
    private static final String INIT_PAGE =
        "document.head.innerHTML = \"\"; document.body.innerHTML = '"
            + "<div id=\"g-recaptcha\" style=\"display: none;\" class=\"g-recaptcha\" "
            + "data-sitekey=\"6LcQyPMUAAAAAN-wUp7pQ81ex5U7BpnG2bQHKClm\" "
            + "data-size=\"compact\" data-theme=\"dark\"></div>'; ";

    private static final String INIT_RECAPTCHA_CALLBACK =
        "function onloadCallback() { grecaptcha.execute(grecaptcha.render"
            + "('g-recaptcha', { 'sitekey' : "
            + "'6LcQyPMUAAAAAN-wUp7pQ81ex5U7BpnG2bQHKClm', 'badge'   : "
            + "'bottomright', 'size'    : 'invisible', 'theme'   : 'dark', "
            + "'callback': function(){ "
            + RecaptchaV2WebAppInterface.INTERFACE_NAME + ".passgRecaptchaResponse(document"
            + ".getElementsByClassName('g-recaptcha-response')[0].value); }, "
            + "'expired-callback': window['recaptcha_error_callback'], "
            + "'error-callback'  : window['recaptcha_error_callback']})); }";

    private static final String INIT_COMPLETE =
        "var body = document.getElementsByTagName('body')[0]; "
            + "var script= "
            + "document.createElement('script'); script.type= "
            + "'text/javascript'; script.src= 'https://www.google"
            + ".com/recaptcha/api.js"
            + "?onload=onloadCallback&render=explicit'; "
            + "body.appendChild(script); "
            + RecaptchaV2WebAppInterface.INTERFACE_NAME + ".setWebViewVisible();";

    @Override public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        webView.evaluateJavascript(
            INIT_PAGE,
            result1 -> {
                webView.evaluateJavascript(
                    INIT_RECAPTCHA_CALLBACK,
                    result2 -> {
                        webView.evaluateJavascript(INIT_COMPLETE, null);
                    }
                );
            }
        );
    }
}
