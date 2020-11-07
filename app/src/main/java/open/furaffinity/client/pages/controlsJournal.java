package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

public class controlsJournal extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = controlsJournal.class.getName();

    private String pagePath = "/controls/journal/";
    private List<HashMap<String, String>> pageResults = new ArrayList<>();
    private String key;
    private String nextPage;

    private String id;
    private String subject;
    private String body;

    public controlsJournal() {
    }

    public controlsJournal(String pagePath) {
        this.pagePath = pagePath;
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements pageControlsJournalLinksDiv = doc.select("div.page-controls-journal-links");
        Element msgFormForm = doc.selectFirst("form[name=MsgForm][method=post]");
        Elements formGet = doc.select("form[action^=/controls/journal/][method=get]");

        if(pageControlsJournalLinksDiv != null) {
            for (Element pageControlsJournalLinkDiv : pageControlsJournalLinksDiv) {
                Element autoLinkA = pageControlsJournalLinkDiv.selectFirst("a.auto_link");
                Element editA = pageControlsJournalLinkDiv.selectFirst("a.edit");
                Element deleteA = pageControlsJournalLinkDiv.selectFirst("a.delete");
                Element popupDateSpan = pageControlsJournalLinkDiv.selectFirst("span.popup_date");

                if (autoLinkA != null && editA != null && deleteA != null && popupDateSpan != null) {
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

        for(Element currentFormGet : formGet) {
            if(currentFormGet.text().equals("Older")) {
                nextPage = currentFormGet.attr("action");
            }
        }

        if(msgFormForm != null){
            Element keyInput = msgFormForm.selectFirst("input[name=key]");
            Element idInput = msgFormForm.selectFirst("input[name=id]");
            if(keyInput != null) {
                key = keyInput.attr("value");
            }

            if(idInput != null) {
                id = idInput.attr("value");
            }
        }

        Element subjectInput = doc.selectFirst("input[name=subject]");
        if(subjectInput != null) {
            subject = subjectInput.attr("value");
        }

        Element messageTextarea = doc.selectFirst("textarea[name=message]");
        if(messageTextarea != null) {
            body = messageTextarea.text();
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
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
        if(nextPage != null) {
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
