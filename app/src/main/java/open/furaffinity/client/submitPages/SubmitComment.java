package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class SubmitComment extends AbstractPage {
    private final String pagePath;
    private final String message;

    public SubmitComment(Context context, PageListener pageListener, String pagePath,
                         String message) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.message = message;
    }

    @Override protected Boolean processPageData(String html) {
        //for this page adding validation is really not reasonable at the moment as even in error
        // it just comes back to the original page.
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "reply");
        params.put("reply", this.message);
        params.put("submit", "Post+Comment");

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
