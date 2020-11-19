package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import open.furaffinity.client.abstractClasses.abstractPage;

public class commissions extends abstractPage {
    private String pagePath;
    private String commissionBody = "";

    public commissions(Context context, pageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
    }

    public commissions(commissions commissions) {
        super(commissions);
        this.pagePath = commissions.pagePath;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element userPageFlexItemUsernameH = doc.selectFirst("div.section-body :first-child > table :first-child > table");

        if(userPageFlexItemUsernameH != null) {
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
