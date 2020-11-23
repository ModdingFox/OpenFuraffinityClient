package open.furaffinity.client.submitPageOld;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.utilities.webClient;

public class submitSubmissionPart2 extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = submitSubmissionPart2.class.getName();

    private static String pagePath = "/submit/";

    private HashMap<String, String> params = new HashMap<>();
    private open.furaffinity.client.submitPageOld.submitSubmissionPart1 submitSubmissionPart1;

    public submitSubmissionPart2(open.furaffinity.client.submitPageOld.submitSubmissionPart1 submitSubmissionPart1) {
        this.submitSubmissionPart1 = submitSubmissionPart1;
    }

    private void processPageDataPart(String html) {
        Document doc = Jsoup.parse(html);

        Element myformForm = doc.selectFirst("form[name=myform]");

        if (myformForm != null) {
            params = new HashMap<>();

            Elements hiddenInputs = myformForm.select("input[type=hidden]");

            if (hiddenInputs != null) {
                for (Element hiddenInput : hiddenInputs) {
                    params.put(hiddenInput.attr("name"), hiddenInput.attr("value"));
                }
            }
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        HashMap<String, String> postParams = new HashMap<>();
        postParams.put("part", "2");
        postParams.put("submission_type", submitSubmissionPart1.getSubmissionTypeCurrent());

        html = webClient[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, postParams);
        processPageDataPart(html);
        return null;
    }

    public static String getPagePath() {
        return pagePath;
    }

    public HashMap<String, String> getParams() {
        return params;
    }
}
