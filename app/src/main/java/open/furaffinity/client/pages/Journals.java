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
import open.furaffinity.client.utilities.WebClient;

public class Journals extends AbstractPage {
    private static final String TAG = Journals.class.getName();
    private final List<HashMap<String, String>> pageResults = new ArrayList<>();
    String pagePath;
    private String page;

    public Journals(Context context, PageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
        setPage("1");
    }

    public Journals(Journals journals) {
        super(journals);
        this.pagePath = journals.pagePath;
        this.page = journals.page;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements journalSections = doc.select("section[id^=jid:]");

        for (Element journalSection : journalSections) {
            HashMap<String, String> newJournalEntry = new HashMap<>();
            newJournalEntry.put("journalTitle",
                journalSection.selectFirst("div.section-header").selectFirst("h2").text());
            newJournalEntry.put("journalDate",
                journalSection.selectFirst("div.section-header").selectFirst("span").text());
            newJournalEntry.put("journalPath",
                journalSection.selectFirst("div.section-footer").selectFirst("a").attr("href"));
            pageResults.add(newJournalEntry);
        }

        //this is not great. really need a way to check if the page was actually loaded.
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
