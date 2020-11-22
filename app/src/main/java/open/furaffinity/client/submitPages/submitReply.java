package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitReply extends open.furaffinity.client.abstractClasses.abstractPage {
    private final String pagePath;
    private final String message;

    public submitReply(Context context, abstractPage.pageListener pageListener, String pagePath, String message) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.message = message;
    }

    @Override
    protected Boolean processPageData(String html) {
        //Will at some point add validation to this to ensure that the correct message is displayed
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("send", "send");
        params.put("reply", message);
        params.put("submit", "Reply");

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
