package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class SubmitControlsFoldersSubmissionsRenameGroup
    extends AbstractPage {

    private static final String pagePath = "/controls/folders/submissions/group/edit";
    private final String key;
    private final String group_id;
    private final String group_name;

    public SubmitControlsFoldersSubmissionsRenameGroup(Context context,
                                                       PageListener pageListener,
                                                       String key, String group_id,
                                                       String group_name) {
        super(context, pageListener);
        this.key = key;
        this.group_id = group_id;
        this.group_name = group_name;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("group_id", group_id);
        params.put("group_name", group_name);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
