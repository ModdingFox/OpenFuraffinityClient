package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.fragmentTabs.ManageSubmissions;
import open.furaffinity.client.utilities.WebClient;

public class SubmitControlsSubmissionsAssignItemToNewFolder extends AbstractPage {

    private final String create_folder_name;
    private final String create_folder_submit;
    private final HashMap<String, String> params;

    public SubmitControlsSubmissionsAssignItemToNewFolder(Context context,
                                                          PageListener pageListener,
                                                          String create_folder_name,
                                                          String create_folder_submit,
                                                          HashMap<String, String> params) {
        super(context, pageListener);
        this.create_folder_name = create_folder_name;
        this.create_folder_submit = create_folder_submit;
        this.params = params;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("create_folder_name", create_folder_name);
        params.put("create_folder_submit", create_folder_submit);
        params.putAll(this.params);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() +
                ManageSubmissions.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
