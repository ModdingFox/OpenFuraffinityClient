package open.furaffinity.client.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.fragmentDrawers.User;
import open.furaffinity.client.utilities.MessageIds;
import open.furaffinity.client.utilities.WebClient;

public class WatchList extends AbstractPage {
    private static final String TAG = WatchList.class.getName();

    String pagePath;
    private String page;
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public WatchList(Context context, PageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
        setPage("1");
    }

    public WatchList(WatchList watchList) {
        super(watchList);
        this.pagePath = watchList.pagePath;
        this.page = watchList.page;
    }

    public static List<HashMap<String, String>> processWatchList(String html,
                                                                 boolean isUserPageData) {
        List<HashMap<String, String>> result = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements watchListItems = doc.select(((isUserPageData) ? ("a") : ("div.watch-list-items")));

        for (Element currentElement : watchListItems) {
            HashMap<String, String> newUser = new HashMap<>();
            newUser.put("item", currentElement.text());
            newUser.put("path", currentElement.selectFirst("a").attr("href"));
            newUser.put("class", User.class.getName());
            newUser.put("messageId", MessageIds.pagePath_MESSAGE);
            result.add(newUser);
        }

        return result;
    }

    protected Boolean processPageData(String html) {
        pageResults = processWatchList(html, false);
        return true;
    }

    @Override protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(
            WebClient.getBaseUrl() + pagePath + getCurrentPage());
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public int getPage() {
        try {
            return Integer.parseInt(Objects.requireNonNull(page));
        } catch (NumberFormatException e) {
            Log.e(TAG, "getPage: ", e);
        }

        return 1;
    }

    public void setPage(String value) {
        try {
            if (Integer.parseInt(value) > 0) {
                page = value;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "setPage: ", e);
        }
    }

    public String getCurrentPage() {
        return page;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
