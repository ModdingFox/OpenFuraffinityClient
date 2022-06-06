package open.furaffinity.client.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.Html;
import open.furaffinity.client.utilities.WebClient;

public class Commissions extends AbstractPage {
    private final String pagePath;
    private String commissionBody = "";

    public Commissions(Context context, PageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element userPageFlexItemUsernameH =
            doc.selectFirst("div.section-body :first-child > table :first-child > table");

        if (userPageFlexItemUsernameH != null) {
            Html.correctHtmlAHrefAndImgScr(
                userPageFlexItemUsernameH);
            commissionBody = userPageFlexItemUsernameH.html();
            return true;
        }

        return false;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        String html = webClient.sendGetRequest(
            WebClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }

        return false;
    }

    public String getCommissionBodyBody() {
        return commissionBody;
    }
}
