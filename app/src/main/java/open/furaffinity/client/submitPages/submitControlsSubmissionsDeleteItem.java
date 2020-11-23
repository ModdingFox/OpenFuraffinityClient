package open.furaffinity.client.submitPages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.fragmentTabs.manageSubmissions;

public class submitControlsSubmissionsDeleteItem extends abstractPage {

    private final HashMap<String, String> params;
    private final String password;

    public submitControlsSubmissionsDeleteItem(Context context, abstractPage.pageListener pageListener, HashMap<String, String> params, String password) {
        super(context, pageListener);
        this.params = params;
        this.password = password;
    }

    @Override
    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element errorMessage = doc.selectFirst("div.error");

        if(errorMessage != null) {
            return false;
        }

        Element confirmButton = doc.selectFirst("button.type-remove");

        if (confirmButton != null) {
            String confirmationCode = confirmButton.attr("value");
            params.put("confirm", confirmationCode);
            params.put("password", password);
            return true;
        }

        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("delete_submissions_submit", "1");
        params.putAll(this.params);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + manageSubmissions.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            if(processPageData(html)){
                params.clear();
                params.put("delete_submissions_submit", "1");
                params.putAll(this.params);

                html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + manageSubmissions.getPagePath(), params);
                if (webClient.getLastPageLoaded() && html != null) {
                    return processPageData(html);
                }
            }
        }
        return false;
    }
}
