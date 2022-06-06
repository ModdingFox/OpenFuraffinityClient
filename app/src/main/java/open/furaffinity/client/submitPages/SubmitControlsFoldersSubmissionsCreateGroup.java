package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class SubmitControlsFoldersSubmissionsCreateGroup extends AbstractPage {

    private static final String pagePath = "/controls/folders/submissions/group/add";
    private final String key;
    private final String group_name;

    public SubmitControlsFoldersSubmissionsCreateGroup(Context context,
                                                       PageListener pageListener,
                                                       String key, String group_name) {
        super(context, pageListener);
        this.key = key;
        this.group_name = group_name;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("position", "-1");
        params.put("group_name", group_name);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
