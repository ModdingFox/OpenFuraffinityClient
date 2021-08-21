package open.furaffinity.client.submitPages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsSettings;

public class submitControlsSettings extends open.furaffinity.client.abstractClasses.abstractPage {
    private final String fa_useremail;
    private final String bdaymonth;
    private final String bdayday;
    private final String bdayyear;
    private final String viewmature;
    private final String timezone;
    private final String timezone_dst;
    private final String fullview;
    private final String style;
    private final String stylesheet;
    private final String scales_enabled;
    private final String paypal_email;
    private final String display_mode;
    private final String scales_message_enabled;
    private final String scales_name;
    private final String scales_plural_name;
    private final String scales_cost;
    private final String account_disabled;
    private final String newpassword;
    private final String newpassword2;
    private final String oldpassword;

    private String errorMessage;

    public submitControlsSettings(Context context, abstractPage.pageListener pageListener, String fa_useremail, String bdaymonth, String bdayday, String bdayyear, String viewmature, String timezone, String timezone_dst, String fullview, String style, String stylesheet, String scales_enabled, String paypal_email, String display_mode, String scales_message_enabled, String scales_name, String scales_plural_name, String scales_cost, String account_disabled, String newpassword, String newpassword2, String oldpassword) {
        super(context, pageListener);
        this.fa_useremail = fa_useremail;
        this.bdaymonth = bdaymonth;
        this.bdayday = bdayday;
        this.bdayyear = bdayyear;
        this.viewmature = viewmature;
        this.timezone = timezone;
        this.timezone_dst = timezone_dst;
        this.fullview = fullview;
        this.style = style;
        this.stylesheet = stylesheet;
        this.scales_enabled = scales_enabled;
        this.paypal_email = paypal_email;
        this.display_mode = display_mode;
        this.scales_message_enabled = scales_message_enabled;
        this.scales_name = scales_name;
        this.scales_plural_name = scales_plural_name;
        this.scales_cost = scales_cost;
        this.account_disabled = account_disabled;
        this.newpassword = newpassword;
        this.newpassword2 = newpassword2;
        this.oldpassword = oldpassword;
    }

    @Override
    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element sectionBody = doc.selectFirst("div.success-msg-box");

        if (sectionBody != null) {
            return true;
        }

        Element errorMsgBox = doc.selectFirst("div.error-msg-box");

        if (errorMsgBox != null) {
            errorMessage = errorMsgBox.text();
        } else {
            errorMessage = "unknown";
        }

        return false;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("do", "update");
        params.put("fa_useremail", fa_useremail);
        params.put("bdaymonth", bdaymonth);
        params.put("bdayday", bdayday);
        params.put("bdayyear", bdayyear);
        params.put("viewmature", viewmature);
        params.put("timezone", timezone);
        params.put("timezone_dst", timezone_dst);
        params.put("fullview", fullview);
        params.put("style", style);
        params.put("stylesheet", stylesheet);
        params.put("scales_enabled", scales_enabled);
        params.put("paypal_email", paypal_email);
        params.put("display_mode", display_mode);
        params.put("scales_message_enabled", scales_message_enabled);
        params.put("scales_name", scales_name);
        params.put("scales_plural_name", scales_plural_name);
        params.put("scales_cost", scales_cost);
        params.put("account_disabled", account_disabled);
        params.put("newpassword", newpassword);
        params.put("newpassword2", newpassword2);
        params.put("oldpassword", oldpassword);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsSettings.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
