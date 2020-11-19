package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.abstractClasses.abstractPage;

public class controlsBuddyList extends abstractPage {
    private static final String TAG = controlsBuddyList.class.getName();

    private static String pagePath = "/controls/buddylist/";
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public controlsBuddyList(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public controlsBuddyList(controlsBuddyList controlsBuddyList) {
        super(controlsBuddyList);
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements flexItemWatchList = doc.select("div.flex-item-watchlist");

        if (flexItemWatchList != null) {
            for (Element currentElement : flexItemWatchList) {
                Element flexItemWatchlistAvatar = currentElement.selectFirst("div.flex-item-watchlist-avatar");
                Element flexItemWatchlistControls = currentElement.selectFirst("div.flex-item-watchlist-controls");

                if (flexItemWatchlistAvatar != null && flexItemWatchlistControls != null) {
                    HashMap<String, String> newEntry = new HashMap<>();

                    Element userLink = flexItemWatchlistAvatar.selectFirst("a");
                    Element userProfileImg = flexItemWatchlistAvatar.selectFirst("img");
                    open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(userProfileImg);

                    Elements flexItemWatchlistControlsA = flexItemWatchlistControls.select("a");

                    if (userLink != null && userProfileImg != null && flexItemWatchlistControlsA != null && flexItemWatchlistControlsA.size() == 2) {
                        newEntry.put("userLink", userLink.attr("href"));
                        newEntry.put("userIcon", userProfileImg.attr("src"));
                        newEntry.put("userName", userProfileImg.attr("alt"));
                        newEntry.put("userRemoveLink", flexItemWatchlistControlsA.get(1).attr("href"));
                        pageResults.add(newEntry);
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

    public static String getPagePath() {
        return pagePath;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
