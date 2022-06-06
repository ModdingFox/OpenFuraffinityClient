package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class SubmitControlsFoldersSubmissionsAddEditFolder extends AbstractPage {

    private final String pagePath;
    private final String key;
    private final String folder_id;
    private final String group_id;
    private final String create_group_name;
    private final String folder_name;
    private final String folder_description;

    public SubmitControlsFoldersSubmissionsAddEditFolder(Context context,
                                                         PageListener pageListener,
                                                         String pagePath, String key,
                                                         String folder_id, String group_id,
                                                         String create_group_name,
                                                         String folder_name,
                                                         String folder_description) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.key = key;
        this.folder_id = folder_id;
        this.group_id = group_id;
        this.create_group_name = create_group_name;
        this.folder_name = folder_name;
        this.folder_description = folder_description;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("folder_id", folder_id);
        params.put("group_id", group_id);
        params.put("create_group_name", create_group_name);
        params.put("folder_name", folder_name);
        params.put("folder_description", folder_description);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
