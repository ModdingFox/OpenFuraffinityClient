package open.furaffinity.client.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class imageResultsTool {
    public static HashMap<String, String> getDropDownOptions(String name, String html) {
        HashMap<String, String> result = new HashMap<>();
        Document doc = Jsoup.parse(html);
        Element rootElement = doc.selectFirst(".listbox[name=" + name + "]");
        Elements optionElements = rootElement.select("option");

        for (Element optionElement : optionElements) {
            result.put(optionElement.attr("value"), optionElement.html());
        }

        return result;
    }

    public static List<HashMap<String, String>> getResultsData(String html) {
        Document doc = Jsoup.parse(html);
        Elements rootElements = doc.select("figure");

        List<HashMap<String, String>> result = new ArrayList<>();

        for (Element rootElement : rootElements) {
            HashMap<String, String> currentPostData = new HashMap<>();

            Element img = rootElement.selectFirst("img");

            Element figcaption = rootElement.selectFirst("figcaption");
            Element post = figcaption.select("a").get(0);
            Element user = figcaption.select("a").get(1);

            currentPostData.put("imgUrl", img.attr("src"));
            currentPostData.put("postPath", post.attr("href"));
            currentPostData.put("postTitle", post.html());
            currentPostData.put("postUserPath", user.attr("href"));
            currentPostData.put("postUserName", user.html());

            result.add(currentPostData);
        }

        return result;
    }

}
