package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import open.furaffinity.client.abstractClasses.BasePage;

public class CommissionsPage extends BasePage {
    private final String pagePath;
    private String commissionBody = "";

    public CommissionsPage(Context context, pageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element userPageFlexItemUsernameH = doc.selectFirst("div.section-body :first-child > table :first-child > table");

        if (userPageFlexItemUsernameH != null) {
            open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(userPageFlexItemUsernameH);
            commissionBody = userPageFlexItemUsernameH.html();
            return true;
        }

        return false;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }

        return false;
    }

    public String getCommissionBodyBody() {
        return commissionBody;
    }
}
