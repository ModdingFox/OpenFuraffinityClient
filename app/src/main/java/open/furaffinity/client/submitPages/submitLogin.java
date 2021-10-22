package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import okhttp3.Cookie;
import open.furaffinity.client.abstractClasses.BasePage;

public class submitLogin extends BasePage {
    private final String name;
    private final String pass;
    private final String gRecaptchaResponse;

    private Cookie a;
    private Cookie b;

    public submitLogin(Context context, BasePage.pageListener pageListener, String name, String pass, String gRecaptchaResponse) {
        super(context, pageListener);
        this.name = name;
        this.pass = pass;
        this.gRecaptchaResponse = gRecaptchaResponse;
    }

    @Override
    protected Boolean processPageData(String html) {
        for (Cookie currentCookie : webClient.getLastPageResponceCookies()) {
            switch (currentCookie.name()) {
                case "a":
                    a = currentCookie;
                    break;
                case "b":
                    b = currentCookie;
                    break;
            }
        }

        return a != null && b != null;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "login");
        params.put("name", this.name);
        params.put("pass", this.pass);
        params.put("g-recaptcha-response", this.gRecaptchaResponse);

        webClient.setFollowRedirects(false);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + open.furaffinity.client.pages.login.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public Cookie getA() {
        return a;
    }

    public Cookie getB() {
        return b;
    }
}
