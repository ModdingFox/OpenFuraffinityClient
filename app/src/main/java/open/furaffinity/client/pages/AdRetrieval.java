package open.furaffinity.client.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.SharedPreferences;
import app.cash.quickjs.QuickJs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.fragmentDrawers.Settings;

public class AdRetrieval extends AbstractPage {
    private static final String pageBaseUrl = "https://rv.furaffinity.net";
    private static final String pagePath = "/live/www/delivery/spc.php";

    private final List<Integer> zones;
    private final List<HashMap<String, String>> adData = new ArrayList<>();

    public AdRetrieval(Context context, PageListener pageListener, List<Integer> zones) {
        super(context, pageListener);
        this.zones = zones;
    }

    @Override protected Boolean processPageData(String javascriptIn) {
        QuickJs engine = QuickJs.create();
        String html =
            "<html><body>" + (String) engine.evaluate(javascriptIn + "\n\nOA_output.join('');") +
                "</body></html>";
        Document doc = Jsoup.parse(html);

        Elements linkSelections = doc.select("a");
        Elements divSelections = doc.select("div");

        if (linkSelections != null && divSelections != null &&
            linkSelections.size() == divSelections.size()) {
            for (int i = 0; i < linkSelections.size(); i++) {
                Element aImgSelection = linkSelections.get(i).selectFirst("img");
                Element divImgSelection = divSelections.get(i).selectFirst("img");

                if (aImgSelection != null && divImgSelection != null) {
                    String link = linkSelections.get(i).attr("href");
                    String image = aImgSelection.attr("src");
                    String beacon = divImgSelection.attr("src");

                    if (link != null && image != null && beacon != null) {
                        HashMap<String, String> newAd = new HashMap<>();
                        newAd.put("link", link);
                        newAd.put("image", image);
                        newAd.put("beacon", beacon);
                        newAd.put("type", "imagelist_advertisement_item");
                        adData.add(newAd);
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override protected Boolean doInBackground(Void... voids) {
        SharedPreferences sharedPref =
            context.getSharedPreferences(context.getString(R.string.settingsFile),
                Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(context.getString(R.string.advertisementsEnabledSetting),
            Settings.advertisementsEnabledDefault) && zones.size() > 0) {
            HashMap<String, String> params = new HashMap<>();
            params.put("zones",
                zones.stream().map(v -> Integer.toString(v)).collect(Collectors.joining("|")));

            String javascriptReturn = webClient.sendPostRequest(pageBaseUrl + pagePath, params);
            if (javascriptReturn != null) {
                return processPageData(javascriptReturn);
            }
            return false;
        }
        else {
            return true;
        }
    }

    public List<HashMap<String, String>> getAdData() {
        return adData;
    }
}
