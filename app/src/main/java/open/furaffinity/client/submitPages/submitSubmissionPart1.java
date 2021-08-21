package open.furaffinity.client.submitPages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitSubmissionPart1 extends open.furaffinity.client.abstractClasses.abstractPage {
    private static final String pagePath = "/submit/";

    private HashMap<String, String> submissionType = new HashMap<>();
    private String maxFileSize = "";
    private String submissionKey = "";

    private String submissionTypeCurrent = "";

    public submitSubmissionPart1(Context context, abstractPage.pageListener pageListener) {
        super(context, pageListener);
    }

    public submitSubmissionPart1(submitSubmissionPart1 submitSubmissionPart1) {
        super(submitSubmissionPart1.context, submitSubmissionPart1.pageListener);
        this.submissionType = submitSubmissionPart1.submissionType;
        this.submissionTypeCurrent = submitSubmissionPart1.submissionTypeCurrent;
        this.maxFileSize = submitSubmissionPart1.maxFileSize;
        this.submissionKey = submitSubmissionPart1.submissionKey;
    }

    public static String getPagePath() {
        return pagePath;
    }

    @Override
    protected Boolean processPageData(String html) {
        boolean result = true;
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

            if(submissionType.keySet().size() < 1) {
                result = false;
            }
        } else {
            result = false;
        }

        Element uploadFormForm = doc.selectFirst("form[id=myform]");
        if (uploadFormForm != null) {
            Element keyInput = uploadFormForm.selectFirst("input[name=key]");
            if (keyInput != null) {
                submissionKey = keyInput.attr("value");
            } else {
                result = false;
            }

            Element maxFileSizeInput = uploadFormForm.selectFirst("input[name=MAX_FILE_SIZE]");
            if (maxFileSizeInput != null) {
                maxFileSize = maxFileSizeInput.attr("value");
            } else {
                result = false;
            }
        } else {
            result = false;
        }

        return result;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public HashMap<String, String> getSubmissionType() {
        return submissionType;
    }

    public String getSubmissionTypeCurrent() {
        return submissionTypeCurrent;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public String getSubmissionKey() {
        return submissionKey;
    }

    public void setSubmissionTypeCurrent(String submissionTypeCurrent) {
        if (submissionType.containsKey(submissionTypeCurrent)) {
            this.submissionTypeCurrent = submissionTypeCurrent;
        }
    }
}
