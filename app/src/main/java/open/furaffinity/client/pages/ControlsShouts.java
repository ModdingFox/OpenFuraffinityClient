package open.furaffinity.client.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class ControlsShouts extends AbstractPage {
    private static final String pagePath = "/controls/shouts/";
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public ControlsShouts(Context context, PageListener pageListener) {
        super(context, pageListener);
    }

    public ControlsShouts(ControlsShouts controlsShouts) {
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
                        pageResults = User.processShouts(aShoutParentParent.html());
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(
            WebClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
