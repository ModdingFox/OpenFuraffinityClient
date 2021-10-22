package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import open.furaffinity.client.abstractClasses.BasePage;

public class login extends BasePage {

    private static final String pagePath = "/login";

    private boolean recaptchaRequired;

    public login(Context context, BasePage.pageListener pageListener) {
        super(context, pageListener);
    }

    public static String getPagePath() {
        return pagePath;
    }

    @Override
    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);
        Element gRecaptcha = doc.selectFirst("[id=g-recaptcha]");
        recaptchaRequired = gRecaptcha != null;
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String html;
        html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public boolean isRecaptchaRequired() {
        return recaptchaRequired;
    }
}
