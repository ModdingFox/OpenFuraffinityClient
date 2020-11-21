package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class controlsUserSettings extends abstractPage {
    private static String pagePath = "/controls/user-settings/";

    private boolean accept_trades;
    private boolean accept_commissions;
    private HashMap<String, String> featured_journal_id;
    private String featured_journal_id_current;

    private String key;

    public controlsUserSettings(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public controlsUserSettings(controlsUserSettings controlsUserSettings) {
        super(controlsUserSettings);
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements inputs = doc.select("input[checked=checked]");
        Elements selects = doc.select("select");
        Element msgFormForm = doc.selectFirst("form[method=post][action=" + pagePath + "]");

        if (inputs != null) {
            for (Element input : inputs) {
                switch (input.attr("name")) {
                    case "accept_trades":
                        accept_trades = ((input.attr("value").equals("1")) ? (true) : (false));
                        break;
                    case "accept_commissions":
                        accept_commissions = ((input.attr("value").equals("1")) ? (true) : (false));
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

                switch (select.attr("name")) {
                    case "featured_journal_id":
                        featured_journal_id = newOptionList;
                        featured_journal_id_current = ((selected != null) ? (selected.attr("value")) : ("0"));
                        break;
                }

            }
        }

        if (msgFormForm != null) {
            Element keyInput = msgFormForm.selectFirst("input[name=key]");
            if (keyInput != null) {
                key = keyInput.attr("value");
            }

            return true;
        }

        return false;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public static String getPagePath() {
        return pagePath;
    }

    public boolean getAcceptTrades() {
        return accept_trades;
    }

    public boolean getAcceptCommissions() {
        return accept_commissions;
    }

    public HashMap<String, String> getFeaturedJournalId() {
        return featured_journal_id;
    }

    public String getFeaturedJournalIdCurrent() {
        return featured_journal_id_current;
    }

    public String getKey() {
        return key;
    }
}
