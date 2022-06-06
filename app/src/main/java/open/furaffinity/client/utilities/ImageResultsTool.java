package open.furaffinity.client.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ImageResultsTool {
    public static imageResolutions getimageResolutionFromInt(int input) {
        List<imageResolutions> imageResolutionsList = Arrays.asList(imageResolutions.values());
        imageResolutionsList = imageResolutionsList.stream()
            .sorted((p1, p2) -> Integer.compare(p1.getValue(), p2.getValue()))
            .collect(Collectors.toList());

        for (imageResolutions currentImageResolution : imageResolutionsList) {
            if (input <= currentImageResolution.getValue()) {
                return currentImageResolution;
            }
        }

        return imageResolutions.Original;
    }

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

    public static List<HashMap<String, String>> getResultsData(String html,
                                                               imageResolutions imageResolution) {
        List<HashMap<String, String>> result = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements rootElements = doc.select("figure");

        for (Element rootElement : rootElements) {
            HashMap<String, String> currentPostData = new HashMap<>();

            Set<String> classes = rootElement.classNames();
            String ratingCode = "U";

            if (classes.contains("r-general")) {
                ratingCode = "G";
            }
            else if (classes.contains("r-mature")) {
                ratingCode = "M";
            }
            else if (classes.contains("r-adult")) {
                ratingCode = "A";
            }

            Element img = rootElement.selectFirst("img");
            if (img != null) {
                if (imageResolution == imageResolutions.Original) {
                    currentPostData.put("imgUrl", img.attr("src"));
                }
                else {
                    String changedUrl = img.attr("src")
                        .replaceFirst("@\\d+-", "@" + imageResolution.toString() + "-");
                    currentPostData.put("imgUrl", changedUrl);
                }
            }

            Element figcaption = rootElement.selectFirst("figcaption");
            Element checkbox = figcaption.selectFirst("input");

            Elements posts = figcaption.select("a");

            if (posts.size() > 0) {
                Element post = posts.get(0);

                currentPostData.put("postPath", post.attr("href"));
                currentPostData.put("postTitle", post.text());
            }


            if (figcaption.select("a").size() > 1) {
                Element user = figcaption.select("a").get(1);

                currentPostData.put("postUserPath", user.attr("href"));
                currentPostData.put("postUserName", user.text());
            }

            if (checkbox != null) {
                currentPostData.put("postId", checkbox.attr("value"));
            }

            currentPostData.put("postRatingCode", ratingCode);

            result.add(currentPostData);
        }

        return result;
    }

    public enum imageResolutions {
        UltraHigh(1600, "Ultra High - 1600"), VeryHigh(800, "Very High - 800"),
        High(600, "High - 600"), MediumHigh(400, "Medium High - 400"), Medium(300, "Medium - 300"),
        MediumLow(250, "Medium Low - 250"), Low(200, "Low - 200"), VeryLow(150, "Very Low - 150"),
        UltraLow(100, "Ultra Low - 100"), ExtremeLow(75, "Extreme Low - 75"),
        Minimal(50, "Minimal - 50"), Original(0, "Site Default");

        private final int value;
        private final String printableName;

        imageResolutions(int value, String printableName) {
            this.value = value;
            this.printableName = printableName;
        }

        @Override public String toString() {
            return Integer.toString(this.value);
        }

        public String getPrintableName() {
            return this.printableName;
        }

        public int getValue() {
            return this.value;
        }

    }

}
