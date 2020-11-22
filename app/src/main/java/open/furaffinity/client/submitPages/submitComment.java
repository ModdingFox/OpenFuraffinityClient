package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitComment extends open.furaffinity.client.abstractClasses.abstractPage {
    private String pagePath;
    private String message;

    public submitComment(Context context, abstractPage.pageListener pageListener, String pagePath, String message) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.message = message;
    }

    public submitComment(submitComment submitComment) {
        super(submitComment);
        this.pagePath = submitComment.pagePath;
        this.message = submitComment.message;
    }

    @Override
    protected Boolean processPageData(String html) {
        //Will at some point add validation to this to ensure that the correct message is displayed
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "reply");
        params.put("reply", this.message);
        params.put("submit", "Post+Comment");

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
