package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.pages.MsgPms;
import open.furaffinity.client.utilities.WebClient;

public class SubmitPm extends AbstractPage {
    private final String key;
    private final String to;
    private final String subject;
    private final String message;
    private final String gRecaptchaResponse;

    public SubmitPm(Context context, PageListener pageListener, String key, String to,
                    String subject, String message, String gRecaptchaResponse) {
        super(context, pageListener);
        this.key = key;
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.gRecaptchaResponse = gRecaptchaResponse;
    }

    @Override protected Boolean processPageData(String html) {
        //Document doc = Jsoup.parse(html);
        return true;//Really should test this at some point.
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("to", to);
        params.put("subject", subject);
        params.put("message", message);
        params.put("g-recaptcha-response", gRecaptchaResponse);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() + MsgPms.getSendPath(),
            params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
