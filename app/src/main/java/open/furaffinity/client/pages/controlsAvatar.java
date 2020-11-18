package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class controlsAvatar extends abstractPage {
    private static String pagePath = "/controls/avatar/";

    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public controlsAvatar(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public controlsAvatar(controlsAvatar controlsAvatar) {
        super(controlsAvatar);
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element avatarListDiv = doc.selectFirst("div.avatar-list");

        if (avatarListDiv != null) {
            Elements avatarsTd = avatarListDiv.select("td");

            if (avatarsTd.size() > 0) {
                for (Element avatarTd : avatarsTd) {
                    HashMap<String, String> currentAvatar = new HashMap<>();

                    Elements a = avatarTd.select("a");

                    if (a.size() > 0) {
                        Element img = a.get(0).selectFirst("img");
                        open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(img);

                        currentAvatar.put("setUrl", a.attr("href"));
                        currentAvatar.put("imgUrl", img.attr("src"));
                    }

                    if (a.size() > 1) {
                        String onClickUrl = a.get(1).attr("onclick");
                        onClickUrl = onClickUrl.split(",")[1];
                        onClickUrl = onClickUrl.substring(1, onClickUrl.length() - 3);

                        currentAvatar.put("deleteUrl", onClickUrl);
                    }

                    if (currentAvatar.keySet().size() > 0) {
                        pageResults.add(currentAvatar);
                    }
                }
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

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
