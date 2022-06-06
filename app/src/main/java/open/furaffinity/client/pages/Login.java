package open.furaffinity.client.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class Login extends AbstractPage {

    private static final String pagePath = "/login";

    private boolean recaptchaRequired;

    public Login(Context context, PageListener pageListener) {
        super(context, pageListener);
    }

    public static String getPagePath() {
        return pagePath;
    }

    @Override protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);
        Element gRecaptcha = doc.selectFirst("[id=g-recaptcha]");
        recaptchaRequired = gRecaptcha != null;
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        String html;
        html = webClient.sendGetRequest(
            WebClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public boolean isRecaptchaRequired() {
        return recaptchaRequired;
    }
}
