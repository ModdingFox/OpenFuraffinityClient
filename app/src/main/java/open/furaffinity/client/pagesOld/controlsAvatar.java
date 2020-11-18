package open.furaffinity.client.pagesOld;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

public class controlsAvatar extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = controlsAvatar.class.getName();

    private static String pagePath = "/controls/avatar/";

    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    private void processPageData(String html) {
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
        }
    }

    @Override
    protected Void doInBackground(webClient... webClients) {
        String html;
        html = webClients[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public static String getPagePath() {
        return pagePath;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
