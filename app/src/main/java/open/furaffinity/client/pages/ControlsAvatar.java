package open.furaffinity.client.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.Html;
import open.furaffinity.client.utilities.WebClient;

public class ControlsAvatar extends AbstractPage {
    private static final String pagePath = "/controls/avatar/";

    private final List<HashMap<String, String>> pageResults = new ArrayList<>();

    public ControlsAvatar(Context context, PageListener pageListener) {
        super(context, pageListener);
    }

    public ControlsAvatar(ControlsAvatar controlsAvatar) {
        super(controlsAvatar);
    }

    public static String getPagePath() {
        return pagePath;
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
                        Html.correctHtmlAHrefAndImgScr(img);

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

    @Override protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(
            WebClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
