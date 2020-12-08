package open.furaffinity.client.submitPages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitSubmissionPart2 extends open.furaffinity.client.abstractClasses.abstractPage {
    private static String pagePath = "/submit/";

    private HashMap<String, String> params = new HashMap<>();
    private open.furaffinity.client.submitPages.submitSubmissionPart1 submitSubmissionPart1;

    public submitSubmissionPart2(Context context, abstractPage.pageListener pageListener, open.furaffinity.client.submitPages.submitSubmissionPart1 submitSubmissionPart1) {
        super(context, pageListener);
        this.submitSubmissionPart1 = submitSubmissionPart1;
    }

    public static String getPagePath() {
        return pagePath;
    }

    @Override
    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element myformForm = doc.selectFirst("form[name=myform]");

        if (myformForm != null) {
            params = new HashMap<>();

            Elements hiddenInputs = myformForm.select("input[type=hidden]");

            if (hiddenInputs != null) {
                for (Element hiddenInput : hiddenInputs) {
                    params.put(hiddenInput.attr("name"), hiddenInput.attr("value"));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> postParams = new HashMap<>();
        postParams.put("part", "2");
        postParams.put("submission_type", submitSubmissionPart1.getSubmissionTypeCurrent());

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, postParams);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public HashMap<String, String> getParams() {
        return params;
    }
}
