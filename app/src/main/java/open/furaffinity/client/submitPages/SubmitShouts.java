package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.pages.ControlsShouts;
import open.furaffinity.client.utilities.WebClient;

public class SubmitShouts extends AbstractPage {
    private final HashMap<String, String> params;

    public SubmitShouts(Context context, PageListener pageListener,
                        HashMap<String, String> params) {
        super(context, pageListener);
        this.params = params;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("do", "update");
        params.putAll(this.params);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() + ControlsShouts.getPagePath(),
            params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
