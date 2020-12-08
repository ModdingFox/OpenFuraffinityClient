package open.furaffinity.client.submitPages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitSubmissionPart1 extends open.furaffinity.client.abstractClasses.abstractPage {
    private static String pagePath = "/submit/";

    private HashMap<String, String> submissionType = new HashMap<>();

    private String submissionTypeCurrent = "";

    public submitSubmissionPart1(Context context, abstractPage.pageListener pageListener) {
        super(context, pageListener);
    }

    public submitSubmissionPart1(submitSubmissionPart1 submitSubmissionPart1) {
        super(submitSubmissionPart1.context, submitSubmissionPart1.pageListener);
        this.submissionType = submitSubmissionPart1.submissionType;
        this.submissionTypeCurrent = submitSubmissionPart1.submissionTypeCurrent;
    }

    @Override
    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements submissionTypeInputs = doc.select("input[name=submission_type]");

        if (submissionTypeInputs != null) {
            for (Element submissionTypeInput : submissionTypeInputs) {
                String key = submissionTypeInput.attr("value");
                String value = submissionTypeInput.parent().text().trim();

                if (submissionTypeInput.hasAttr("checked")) {
                    submissionTypeCurrent = key;
                }

                submissionType.put(key, value);
            }
            return true;
        }
        return false;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
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
        if (submissionType.containsKey(submissionTypeCurrent)) {
            this.submissionTypeCurrent = submissionTypeCurrent;
        }
    }
}
