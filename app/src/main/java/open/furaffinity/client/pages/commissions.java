package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import open.furaffinity.client.utilities.webClient;

public class commissions extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = commissions.class.getName();

    String pagePath;
    String comissionBody;

    public commissions(String pagePath) {
        this.pagePath = pagePath;
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element userPageFlexItemUsernameH = doc.selectFirst("div.section-body :first-child > table :first-child > table");
        comissionBody = userPageFlexItemUsernameH.html();
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public String getComissionBody() {
        return comissionBody;
    }
}
