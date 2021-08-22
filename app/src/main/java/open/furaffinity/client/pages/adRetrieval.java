package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.cash.quickjs.QuickJs;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;

public class adRetrieval extends abstractPage {
    private static final String pageBaseUrl = "https://rv.furaffinity.net";
    private static final String pagePath = "/live/www/delivery/spc.php";

    private static List<HashMap<String, String>> adData = new ArrayList<>();

    public adRetrieval(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public adRetrieval(adRetrieval adRetrieval) {
        super(adRetrieval.context, adRetrieval.pageListener);
    }

    @Override
    protected Boolean processPageData(String javascriptIn) {
        QuickJs engine = QuickJs.create();
        String html = "<html><body>" + (String)engine.evaluate(javascriptIn + "\n\nOA_output.join('');") + "</body></html>";
        Document doc = Jsoup.parse(html);

        Elements linkSelections = doc.select("a");
        Elements divSelections = doc.select("div");

        if(linkSelections != null && divSelections != null && linkSelections.size() == divSelections.size()) {
            for(int i = 0; i < linkSelections.size(); i++) {
                Element aImgSelection = linkSelections.get(i).selectFirst("img");
                Element divImgSelection = divSelections.get(i).selectFirst("img");

                if(aImgSelection != null && divImgSelection != null) {
                    String link = linkSelections.get(i).attr("href");
                    String image = aImgSelection.attr("src");
                    String beacon = divImgSelection.attr("src");

                    if(link != null && image != null && beacon != null) {
                        HashMap<String, String> newAd = new HashMap<>();
                        newAd.put("link", link);
                        newAd.put("image", image);
                        newAd.put("beacon", beacon);
                        newAd.put("type", "imagelist_imageonly_item");
                        adData.add(newAd);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("zones", "11|13|10|5|6|2|4");

        String javascriptReturn = webClient.sendPostRequest(pageBaseUrl + pagePath, params);
        if (javascriptReturn != null) {
            return processPageData(javascriptReturn);
        }
        return false;
    }

    public List<HashMap<String,String>> getAdData() {
        return adData;
    }
}
