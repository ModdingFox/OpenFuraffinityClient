package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.pages.ControlsUserSettings;
import open.furaffinity.client.utilities.WebClient;

public class SubmitControlsUserSettings extends AbstractPage {
    private final String key;
    private final String accept_trades;
    private final String accept_commissions;
    private final String featured_journal_id;

    public SubmitControlsUserSettings(Context context, PageListener pageListener,
                                      String key, String accept_trades, String accept_commissions,
                                      String featured_journal_id) {
        super(context, pageListener);
        this.key = key;
        this.accept_trades = accept_trades;
        this.accept_commissions = accept_commissions;
        this.featured_journal_id = featured_journal_id;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("do", "update");
        params.put("key", key);
        params.put("accept_trades", accept_trades);
        params.put("accept_commissions", accept_commissions);
        params.put("featured_journal_id", featured_journal_id);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() +
                ControlsUserSettings.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
