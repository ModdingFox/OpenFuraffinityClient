package open.furaffinity.client.pagesOld;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

import static open.furaffinity.client.utilities.imageResultsTool.getResultsData;

public class msgSubmission extends AsyncTask<webClient, Void, Void> {
    private String pagePath = "/msg/submissions";

    private boolean isNewestFirst;

    private int page = 1;
    private String perPage = "24";

    private String prevPage = null;
    private String nextPage = null;

    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public msgSubmission(boolean isNewestFirst) {
        this.isNewestFirst = isNewestFirst;

        if (isNewestFirst) {
            pagePath = "/msg/submissions/new@" + perPage;
        } else {
            pagePath = "/msg/submissions/old@" + perPage;
        }
    }

    public msgSubmission(msgSubmission msgSubmission) {
        this.pagePath = msgSubmission.pagePath;
        this.isNewestFirst = msgSubmission.isNewestFirst;
        this.page = msgSubmission.page;
        this.perPage = msgSubmission.perPage;
        this.prevPage = msgSubmission.prevPage;
        this.nextPage = msgSubmission.nextPage;
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element prevA = doc.selectFirst("a[class*=more].prev");
        Element moreA = doc.selectFirst("a[class*=more]:not(.prev)");

        if (prevA != null) {
            prevPage = prevA.attr("href");
        }

        if (moreA != null) {
            nextPage = moreA.attr("href");
        }

        pageResults = getResultsData(html);
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public HashMap<String, String> getPerpage() {
        HashMap<String, String> result = new HashMap<>();
        result.put("24", "24");
        result.put("48", "48");
        result.put("72", "72");
        return result;
    }

    public String getCurrentPerpage() {
        return perPage;
    }

    public void setPerpage(String value) {
        if (getPerpage().containsKey(value)) {
            pagePath.replace("@" + perPage, "@" + value);
            perPage = value;
        }
    }

    public int getCurrentPage() {
        return page;
    }

    public boolean getIsNewestFirst() {
        return isNewestFirst;
    }

    public boolean setPrevPage() {
        if (prevPage == null) {
            return false;
        }
        pagePath = prevPage;
        page--;
        return true;
    }

    public boolean setNextPage() {
        if (nextPage == null) {
            return false;
        }
        pagePath = nextPage;
        page++;
        return true;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }

    public String getPagePath() {
        return pagePath;
    }
}
