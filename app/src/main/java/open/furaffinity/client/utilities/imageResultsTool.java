package open.furaffinity.client.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class imageResultsTool {
    public static HashMap<String, String> getDropDownOptions(String name, String html) {
        HashMap<String, String> result = new HashMap<>();

        Document doc = Jsoup.parse(html);

        if (doc != null) {
            Element rootElement = doc.selectFirst(".listbox[name=" + name + "]");

            if (rootElement != null) {
                Elements optionElements = rootElement.select("option");

                for (Element optionElement : optionElements) {
                    result.put(optionElement.attr("value"), optionElement.html());
                }

                return result;
            }
        }

        return null;
    }

    public static List<HashMap<String, String>> getResultsData(String html) {
        List<HashMap<String, String>> result = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements rootElements = doc.select("figure");

        for (Element rootElement : rootElements) {
            HashMap<String, String> currentPostData = new HashMap<>();

            Set<String> classes = rootElement.classNames();
            String ratingCode = "U";

            if (classes.contains("r-general")) {
                ratingCode = "G";
            } else if (classes.contains("r-mature")) {
                ratingCode = "M";
            } else if (classes.contains("r-adult")) {
                ratingCode = "A";
            }

            Element img = rootElement.selectFirst("img");

            Element figcaption = rootElement.selectFirst("figcaption");
            Element checkbox = figcaption.selectFirst("input");
            Element post = figcaption.select("a").get(0);

            if (figcaption.select("a").size() > 1) {
                Element user = figcaption.select("a").get(1);

                currentPostData.put("postUserPath", user.attr("href"));
                currentPostData.put("postUserName", user.html());
            }

            currentPostData.put("imgUrl", img.attr("src"));

            if (checkbox != null) {
                currentPostData.put("postId", checkbox.attr("value"));
            }

            currentPostData.put("postPath", post.attr("href"));
            currentPostData.put("postTitle", post.html());
            currentPostData.put("postRatingCode", ratingCode);

            result.add(currentPostData);
        }

        return result;
    }

}
