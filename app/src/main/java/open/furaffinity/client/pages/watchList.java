package open.furaffinity.client.pages;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class watchList extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = watchList.class.getName();

    String pagePath;
    private String page;
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public watchList(String pagePath) {
        this.pagePath = pagePath;
        setPage("1");
    }

    public watchList(watchList watchList) {
        this.pagePath = watchList.pagePath;
        this.page = watchList.page;
    }

    public static List<HashMap<String, String>> processWatchList(String html, boolean isUserPageData) {
        List<HashMap<String, String>> result = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements watchListItems = doc.select(((isUserPageData) ? ("a") : ("div.watch-list-items")));

        for (Element currentElement : watchListItems) {
            HashMap<String, String> newUser = new HashMap<>();
            newUser.put("item", currentElement.text());
            newUser.put("path", currentElement.selectFirst("a").attr("href"));
            newUser.put("class", open.furaffinity.client.fragments.user.class.getName());
            newUser.put("messageId", messageIds.pagePath_MESSAGE);
            result.add(newUser);
        }

        return result;
    }

    private void processPageData(String html) {
        pageResults = processWatchList(html, false);
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath + getCurrentPage());
        processPageData(html);
        return null;
    }

    public int getPage() {
        try {
            return Integer.parseInt(Objects.requireNonNull(page));
        } catch (NumberFormatException e) {
            Log.e(TAG, "getPage: ", e);
        }

        return 1;
    }

    public String getCurrentPage() {
        return page;
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

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
