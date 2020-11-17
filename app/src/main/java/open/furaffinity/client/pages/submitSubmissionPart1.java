package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.utilities.webClient;

public class submitSubmissionPart1 extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = submitSubmissionPart1.class.getName();

    private static String pagePath = "/submit/";

    private HashMap<String, String> submissionType = new HashMap<>();

    private String submissionTypeCurrent = "";

    public submitSubmissionPart1() {
    }

    private void processPageDataPart(String html) {
        Document doc = Jsoup.parse(html);

        Elements submissionTypeInputs = doc.select("input[name=submission_type]");

        if(submissionTypeInputs != null) {
            for(Element submissionTypeInput : submissionTypeInputs) {
                String key = submissionTypeInput.attr("value");
                String value = submissionTypeInput.parent().text().trim();

                if(submissionTypeInput.hasAttr("checked")) {
                    submissionTypeCurrent = key;
                }

                submissionType.put(key, value);
            }
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageDataPart(html);
        return null;
    }

    public static String getPagePath() {
        return pagePath;
    }

    public HashMap<String, String> getSubmissionType() {
        return submissionType;
    }

    public String getSubmissionTypeCurrent() {
        return submissionTypeCurrent;
    }

    public void setSubmissionTypeCurrent(String submissionTypeCurrent) {
        if(submissionType.containsKey(submissionTypeCurrent)) {
            this.submissionTypeCurrent = submissionTypeCurrent;
        }
    }
}
