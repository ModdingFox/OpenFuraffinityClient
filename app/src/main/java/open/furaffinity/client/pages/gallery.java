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

import open.furaffinity.client.utilities.webClient;

import static open.furaffinity.client.utilities.imageResultsTool.getResultsData;

public class gallery extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = gallery.class.getName();

    private String pagePath;
    private String page;
    private List<HashMap<String, String>> pageResults = new ArrayList<>();
    private HashMap<String, String> folderResults = new HashMap<>();

    public gallery(String pagePath) {
        this.pagePath = pagePath;
        setPage("1");
    }

    public gallery(gallery gallery) {
        this.pagePath = gallery.pagePath;
        this.page = gallery.page;
    }

    private void processPageData(String html) {
        pageResults = getResultsData(html);

        Document doc = Jsoup.parse(html);
        Element folderListDiv = doc.selectFirst("div.folder-list");

        if (folderListDiv != null) {
            Element currentFolder = folderListDiv.selectFirst("li.active");
            folderResults.put(pagePath, currentFolder.text());

            Elements folderLinks = folderListDiv.select("a");

            if (folderLinks != null) {
                for (Element currentElement : folderLinks) {
                    folderResults.put(currentElement.attr("href"), currentElement.text());
                }
            }
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath + getCurrentPage());
        processPageData(html);
        return null;
    }

    public String getPagePath() {
        return pagePath;
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
            if (Integer.parseInt(value) >= 0) {
                page = value;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "setPage: ", e);
        }
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }

    public HashMap<String, String> getFolderResults() {
        return folderResults;
    }
}
