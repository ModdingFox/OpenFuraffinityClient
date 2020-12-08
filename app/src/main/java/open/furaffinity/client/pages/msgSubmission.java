package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.fragmentDrawers.settings;
import open.furaffinity.client.utilities.imageResultsTool;

import static open.furaffinity.client.utilities.imageResultsTool.getResultsData;

public class msgSubmission extends abstractPage {
    private final boolean isNewestFirst;
    private final imageResultsTool.imageResolutions currentResolution;
    private String pagePath;
    private int page = 1;
    private String perPage = "24";
    private String prevPage = null;
    private String nextPage = null;
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public msgSubmission(Context context, pageListener pageListener, boolean isNewestFirst) {
        super(context, pageListener);
        this.isNewestFirst = isNewestFirst;

        if (isNewestFirst) {
            pagePath = "/msg/submissions/new@" + perPage;
        } else {
            pagePath = "/msg/submissions/old@" + perPage;
        }

        currentResolution = imageResultsTool.getimageResolutionFromInt(sharedPref.getInt(context.getString(R.string.imageResolutionSetting), settings.imageResolutionDefault));
    }

    public msgSubmission(msgSubmission msgSubmission) {
        super(msgSubmission);
        this.pagePath = msgSubmission.pagePath;
        this.isNewestFirst = msgSubmission.isNewestFirst;
        this.page = msgSubmission.page;
        this.perPage = msgSubmission.perPage;
        this.prevPage = msgSubmission.prevPage;
        this.nextPage = msgSubmission.nextPage;
        this.currentResolution = msgSubmission.currentResolution;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element prevA = doc.selectFirst("a[class*=more].prev");
        Element moreA = doc.selectFirst("a[class*=more]:not(.prev)");

        if (prevA != null) {
            prevPage = prevA.attr("href");
        }

        if (moreA != null) {
            nextPage = moreA.attr("href");
        }

        pageResults = getResultsData(html, currentResolution);

        //not the greatest here. need to eventually find a good way to check the page is loaded/valid
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        String html;
        html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public HashMap<String, String> getPerpage() {
        HashMap<String, String> result = new HashMap<>();
        result.put("24", "24");
        result.put("48", "48");
        result.put("72", "72");
        return result;
    }

    public void setPerpage(String value) {
        if (getPerpage().containsKey(value)) {
            pagePath = pagePath.replace("@" + perPage, "@" + value);
            perPage = value;
        }
    }

    public String getCurrentPerpage() {
        return perPage;
    }

    public boolean getIsNewestFirst() {
        return isNewestFirst;
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
