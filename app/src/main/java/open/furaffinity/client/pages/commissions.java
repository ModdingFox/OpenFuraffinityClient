package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import open.furaffinity.client.utilities.webClient;

public class commissions extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = commissions.class.getName();

    private String pagePath;
    private String commissionBody = "";

    public commissions(String pagePath) {
        this.pagePath = pagePath;
    }

    private void processPageData(String html) {
        if (html != null) {
            Document doc = Jsoup.parse(html);

            Element userPageFlexItemUsernameH = doc.selectFirst("div.section-body :first-child > table :first-child > table");
            open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(userPageFlexItemUsernameH);
            commissionBody = userPageFlexItemUsernameH.html();
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public String getCommissionBodyBody() {
        return commissionBody;
    }
}
