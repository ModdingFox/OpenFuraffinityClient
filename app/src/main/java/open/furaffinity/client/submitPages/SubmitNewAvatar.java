package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.pages.ControlsAvatar;
import open.furaffinity.client.utilities.WebClient;

public class SubmitNewAvatar extends AbstractPage {
    private final String filePath;

    public SubmitNewAvatar(Context context, PageListener pageListener,
                           String filePath) {
        super(context, pageListener);
        this.filePath = filePath;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        List<HashMap<String, String>> params = new ArrayList<>();

        HashMap<String, String> newParam = new HashMap<>();
        newParam.put("name", "do");
        newParam.put("value", "uploadavatar");
        params.add(newParam);

        newParam = new HashMap<>();
        newParam.put("name", "avatarfile");
        newParam.put("filePath", filePath);
        params.add(newParam);

        String html = webClient.sendFormPostRequest(
            WebClient.getBaseUrl() + ControlsAvatar.getPagePath(),
            params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
