package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.BasePage;

public class controlsSettings extends BasePage {
    private static final String pagePath = "/controls/settings/";

    private String fa_useremail;
    private HashMap<String, String> ssl_enable;
    private String ssl_enable_current;
    private HashMap<String, String> bdaymonth;
    private String bdaymonth_current;
    private HashMap<String, String> bdayday;
    private String bdayday_current;
    private HashMap<String, String> bdayyear;
    private String bdayyear_current;
    private HashMap<String, String> viewmature;
    private String viewmature_current;
    private HashMap<String, String> timezone;
    private String timezone_current;
    private boolean timezone_dst;
    private HashMap<String, String> fullview;
    private String fullview_current;
    private HashMap<String, String> style;
    private String style_current;
    private HashMap<String, String> stylesheet;
    private String stylesheet_current;
    private HashMap<String, String> scales_enabled;
    private String scales_enabled_current;
    private String paypal_email;
    private HashMap<String, String> display_mode;
    private String display_mode_current;
    private HashMap<String, String> scales_message_enabled;
    private String scales_message_enabled_current;
    private String scales_name;
    private String scales_plural_name;
    private String scales_cost;
    private HashMap<String, String> account_disabled;
    private String account_disabled_current;

    public controlsSettings(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public static String getPagePath() {
        return pagePath;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements inputs = doc.select("input");
        Elements selects = doc.select("select");

        if (inputs != null) {
            for (Element input : inputs) {
                switch (input.attr("name")) {
                    case "fa_useremail":
                        fa_useremail = input.attr("value");
                        break;
                    case "timezone_dst":
                        timezone_dst = (!input.attr("value").equals("0"));
                        break;
                    case "paypal_email":
                        paypal_email = input.attr("value");
                        break;
                    case "scales_name":
                        scales_name = input.attr("value");
                        break;
                    case "scales_plural_name":
                        scales_plural_name = input.attr("value");
                        break;
                    case "scales_cost":
                        scales_cost = input.attr("value");
                        break;
                }
            }
        }

        if (selects != null) {
            for (Element select : selects) {
                Element selected = select.selectFirst("option[selected]");
                Elements options = select.select("option");
                HashMap<String, String> newOptionList = new HashMap<>();

                for (Element option : options) {
                    if (option.hasAttr("value")) {
                        newOptionList.put(option.attr("value"), option.text());
                    }
                }

                if (selected != null) {
                    switch (select.attr("name")) {
                        case "ssl_enable":
                            ssl_enable = newOptionList;
                            ssl_enable_current = selected.attr("value");
                            break;
                        case "bdaymonth":
                            bdaymonth = newOptionList;
                            bdaymonth_current = selected.attr("value");
                            break;
                        case "bdayday":
                            bdayday = newOptionList;
                            bdayday_current = selected.attr("value");
                            break;
                        case "bdayyear":
                            bdayyear = newOptionList;
                            bdayyear_current = selected.attr("value");
                            break;
                        case "viewmature":
                            viewmature = newOptionList;
                            viewmature_current = selected.attr("value");
                            break;
                        case "timezone":
                            timezone = newOptionList;
                            timezone_current = selected.attr("value");
                            break;
                        case "fullview":
                            fullview = newOptionList;
                            fullview_current = selected.attr("value");
                            break;
                        case "style":
                            style = newOptionList;
                            style_current = selected.attr("value");
                            break;
                        case "stylesheet":
                            stylesheet = newOptionList;
                            stylesheet_current = selected.attr("value");
                            break;
                        case "scales_enabled":
                            scales_enabled = newOptionList;
                            scales_enabled_current = selected.attr("value");
                            break;
                        case "display_mode":
                            display_mode = newOptionList;
                            display_mode_current = selected.attr("value");
                            break;
                        case "scales_message_enabled":
                            scales_message_enabled = newOptionList;
                            scales_message_enabled_current = selected.attr("value");
                            break;
                        case "account_disabled":
                            account_disabled = newOptionList;
                            account_disabled_current = selected.attr("value");
                            break;
                    }
                }
            }
        }

        //not really a great way of doing it but sure
        return inputs != null && inputs.size() > 0 && selects != null && selects.size() > 0;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getFaUserEmail() {
        return fa_useremail;
    }

    public HashMap<String, String> getSslEnable() {
        return ssl_enable;
    }

    public HashMap<String, String> getBDayMonth() {
        return bdaymonth;
    }

    public HashMap<String, String> getBDayDay() {
        return bdayday;
    }

    public HashMap<String, String> getBDayYear() {
        return bdayyear;
    }

    public HashMap<String, String> getViewMature() {
        return viewmature;
    }

    public HashMap<String, String> getTimezone() {
        return timezone;
    }

    public boolean getTimezoneDST() {
        return timezone_dst;
    }

    public HashMap<String, String> getFullView() {
        return fullview;
    }

    public HashMap<String, String> getStyle() {
        return style;
    }

    public HashMap<String, String> getStylesheet() {
        return stylesheet;
    }

    public HashMap<String, String> getScalesEnabled() {
        return scales_enabled;
    }

    public String getPayPalEmail() {
        return paypal_email;
    }

    public HashMap<String, String> getDisplayMode() {
        return display_mode;
    }

    public HashMap<String, String> getScalesMessageEnabled() {
        return scales_message_enabled;
    }

    public String getScalesName() {
        return scales_name;
    }

    public String getScalesPluralName() {
        return scales_plural_name;
    }

    public String getScalesCost() {
        return scales_cost;
    }

    public HashMap<String, String> getAccountDisabled() {
        return account_disabled;
    }

    public String getSslEnableCurrent() {
        return ssl_enable_current;
    }

    public String getBDayMonthCurrent() {
        return bdaymonth_current;
    }

    public String getBDayDayCurrent() {
        return bdayday_current;
    }

    public String getBDayYearCurrent() {
        return bdayyear_current;
    }

    public String getViewMatureCurrent() {
        return viewmature_current;
    }

    public String getTimezoneCurrent() {
        return timezone_current;
    }

    public String getFullViewCurrent() {
        return fullview_current;
    }

    public String getStyleCurrent() {
        return style_current;
    }

    public String getStylesheetCurrent() {
        return stylesheet_current;
    }

    public String getScalesEnabledCurrent() {
        return scales_enabled_current;
    }

    public String getDisplayModeCurrent() {
        return display_mode_current;
    }

    public String getScalesMessageEnabledCurrent() {
        return scales_message_enabled_current;
    }

    public String getAccountDisabledCurrent() {
        return account_disabled_current;
    }
}
