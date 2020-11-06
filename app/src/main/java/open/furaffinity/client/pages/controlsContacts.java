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

public class controlsContacts extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = controlsContacts.class.getName();

    private static String pagePath = "/controls/contacts/";
    private List<HashMap<String, String>> pageResults = new ArrayList<>();
    private String key;

    public controlsContacts() {
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements controlPanelContactItemDiv = doc.select("div.control-panel-contact-item");
        Element msgFormForm = doc.selectFirst("form[name=MsgForm]");

        if(controlPanelContactItemDiv != null) {
            for(Element currentControlPanelContactItemDiv : controlPanelContactItemDiv) {
                Element cellDiv = currentControlPanelContactItemDiv.selectFirst("div.cell");
                if(cellDiv != null) {
                    Element h4 = cellDiv.selectFirst("h4");
                    Element input = cellDiv.selectFirst("input");

                    if(h4 != null && input != null) {
                        HashMap<String, String> newElement = new HashMap<>();
                        newElement.put("label", h4.text());
                        newElement.put("value", input.attr("value"));
                        newElement.put("name", input.attr("name"));

                        if(input.hasAttr("placeholder")) {
                            newElement.put("placeholder", input.attr("placeholder"));
                        }

                        pageResults.add(newElement);
                    }
                }
            }
        }

        if(msgFormForm != null){
            Element keyInput = msgFormForm.selectFirst("input[name=key]");
            if(keyInput != null) {
                key = keyInput.attr("value");
            }
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public static String getPagePath() {
        return pagePath;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }

    public String getKey() {
        return key;
    }
}
