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

public class controlsContacts extends abstractPage {
    private static final String pagePath = "/controls/contacts/";
    private final List<HashMap<String, String>> pageResults = new ArrayList<>();
    private String key;

    public controlsContacts(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public static String getPagePath() {
        return pagePath;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements controlPanelContactItemDiv = doc.select("div.control-panel-contact-item");
        Element msgFormForm = doc.selectFirst("form[name=MsgForm]");

        if (controlPanelContactItemDiv != null) {
            for (Element currentControlPanelContactItemDiv : controlPanelContactItemDiv) {
                Element cellDiv = currentControlPanelContactItemDiv.selectFirst("div.cell");
                if (cellDiv != null) {
                    Element h4 = cellDiv.selectFirst("h4");
                    Element input = cellDiv.selectFirst("input");

                    if (h4 != null && input != null) {
                        HashMap<String, String> newElement = new HashMap<>();
                        newElement.put("label", h4.text());
                        newElement.put("value", input.attr("value"));
                        newElement.put("name", input.attr("name"));

                        if (input.hasAttr("placeholder")) {
                            newElement.put("placeholder", input.attr("placeholder"));
                        }

                        pageResults.add(newElement);
                    }
                }
            }
        }

        if (msgFormForm != null) {
            Element keyInput = msgFormForm.selectFirst("input[name=key]");
            if (keyInput != null) {
                key = keyInput.attr("value");
            }
        }

        return pageResults.size() > 0 && key != null;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }

    public String getKey() {
        return key;
    }
}
