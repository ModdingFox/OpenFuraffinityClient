package open.furaffinity.client.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class ControlsJournal extends AbstractPage {
    private String pagePath = "/controls/journal/";

    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    private String key;
    private String nextPage;

    private String id;
    private String subject;
    private String body;

    public ControlsJournal(Context context, PageListener pageListener) {
        super(context, pageListener);
    }

    public ControlsJournal(Context context, PageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
    }

    public ControlsJournal(ControlsJournal controlsJournal) {
        super(controlsJournal);
        this.pagePath = controlsJournal.pagePath;
        this.pageResults = controlsJournal.pageResults;
        this.key = controlsJournal.key;
        this.nextPage = controlsJournal.nextPage;
        this.id = controlsJournal.id;
        this.subject = controlsJournal.subject;
        this.body = controlsJournal.body;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements pageControlsJournalLinksDiv = doc.select("div.page-controls-journal-links");
        Element msgFormForm = doc.selectFirst("form[name=MsgForm][method=post]");
        Elements formGet = doc.select("form[action^=/controls/journal/][method=get]");

        if (pageControlsJournalLinksDiv != null) {
            for (Element pageControlsJournalLinkDiv : pageControlsJournalLinksDiv) {
                Element autoLinkA = pageControlsJournalLinkDiv.selectFirst("a.auto_link");
                Element editA = pageControlsJournalLinkDiv.selectFirst("a.edit");
                Element deleteA = pageControlsJournalLinkDiv.selectFirst("a.delete");
                Element popupDateSpan = pageControlsJournalLinkDiv.selectFirst("span.popup_date");

                if (autoLinkA != null && editA != null && deleteA != null &&
                    popupDateSpan != null) {
                    HashMap<String, String> newResult = new HashMap<>();
                    newResult.put("postPath", autoLinkA.attr("href"));
                    newResult.put("postSubject", autoLinkA.text());
                    newResult.put("editPath", editA.attr("href"));

                    String deletePath = deleteA.attr("onclick").split(",")[1];
                    deletePath = deletePath.substring(1, deletePath.length() - 2);

                    newResult.put("deletePath", deletePath);
                    newResult.put("postDate", popupDateSpan.text());
                    pageResults.add(newResult);
                }
            }
        }

        for (Element currentFormGet : formGet) {
            if (currentFormGet.text().equals("Older")) {
                nextPage = currentFormGet.attr("action");
            }
        }

        if (msgFormForm != null) {
            Element keyInput = msgFormForm.selectFirst("input[name=key]");
            Element idInput = msgFormForm.selectFirst("input[name=id]");
            if (keyInput != null) {
                key = keyInput.attr("value");
            }

            if (idInput != null) {
                id = idInput.attr("value");
            }
        }

        Element subjectInput = doc.selectFirst("input[name=subject]");
        if (subjectInput != null) {
            subject = subjectInput.attr("value");
        }

        Element messageTextarea = doc.selectFirst("textarea[name=message]");
        if (messageTextarea != null) {
            body = messageTextarea.text();
        }

        return msgFormForm != null && subjectInput != null && messageTextarea != null;
    }

    @Override protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(
            WebClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getPagePath() {
        return pagePath;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }

    public String getKey() {
        return key;
    }

    public void setNextPage() {
        if (nextPage != null) {
            pagePath = nextPage;
        }
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
