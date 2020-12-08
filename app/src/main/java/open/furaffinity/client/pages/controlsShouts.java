package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.abstractClasses.abstractPage;

public class controlsShouts extends abstractPage {
    private static final String pagePath = "/controls/shouts/";
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public controlsShouts(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public controlsShouts(controlsShouts controlsShouts) {
        super(controlsShouts);
    }

    public static String getPagePath() {
        return pagePath;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element shoutsFormform = doc.selectFirst("form[id=shouts-form]");

        if (shoutsFormform != null) {
            Element aShout = shoutsFormform.selectFirst("a[id^=shout]");

            if (aShout != null) {
                Element aShoutParent = aShout.parent();
                if (aShoutParent != null) {
                    Element aShoutParentParent = aShoutParent.parent();
                    if (aShoutParentParent != null) {
                        pageResults = user.processShouts(aShoutParentParent.html());
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
