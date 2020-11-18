package open.furaffinity.client.pagesOld;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

public class controlsShouts extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = controlsShouts.class.getName();

    private static String pagePath = "/controls/shouts/";
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public controlsShouts() {
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element aShout = doc.selectFirst("a[id^=shout]");

        if (aShout != null) {
            Element aShoutParent = aShout.parent();
            if (aShoutParent != null) {
                Element aShoutParentParent = aShoutParent.parent();
                if (aShoutParentParent != null) {
                    pageResults = open.furaffinity.client.pagesOld.user.processShouts(aShoutParentParent.html());
                }
            }
        }

    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public static String getPagePath() {
        return pagePath;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
