package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class SubmitControlsFoldersSubmissionsMoveFolder extends AbstractPage {

    private final String pagePath;
    private final String key;
    private final String direction;
    private final String idName;
    private final String id;

    public SubmitControlsFoldersSubmissionsMoveFolder(Context context,
                                                      PageListener pageListener,
                                                      String pagePath, String key, String direction,
                                                      String idName, String id) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.key = key;
        this.direction = direction;
        this.idName = idName;
        this.id = id;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("direction", direction);
        params.put(idName, id);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
