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

public class journals extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = journals.class.getName();

    String pagePath;
    private String page;
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public journals(String pagePath) {
        this.pagePath = pagePath;
        setPage("1");
    }

    public journals(journals journals) {
        this.pagePath = journals.pagePath;
        this.page = journals.page;
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements journalSections = doc.select("section[id^=jid:]");

        for (Element journalSection : journalSections) {
            HashMap<String, String> newJournalEntry = new HashMap<>();
            newJournalEntry.put("journalTitle", journalSection.selectFirst("div.section-header").selectFirst("h2").text());
            newJournalEntry.put("journalDate", journalSection.selectFirst("div.section-header").selectFirst("span").text());
            newJournalEntry.put("journalPath", journalSection.selectFirst("div.section-footer").selectFirst("a").attr("href"));
            pageResults.add(newJournalEntry);
        }
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
