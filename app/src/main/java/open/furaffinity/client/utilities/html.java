package open.furaffinity.client.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class html {
    public static void correctHtmlAHrefAndImgScr(Element rootElementIn) {
        if (rootElementIn != null) {
            Elements rootElementsA = rootElementIn.select("a");
            for (Element rootElementA : rootElementsA) {
                if (rootElementA.attr("href").startsWith("/")) {
                    rootElementA.attr("href", webClient.getBaseUrl() + rootElementA.attr("href"));
                }
            }
            Elements rootElementsImg = rootElementIn.select("img");
            for (Element rootElementImg : rootElementsImg) {
                if (rootElementImg.attr("src").startsWith("/")) {
                    rootElementImg.attr("src", "https:" + rootElementImg.attr("src"));
                }

                if (rootElementImg.hasAttr("data-fullview-src") && rootElementImg.attr("data-fullview-src").startsWith("/")) {
                    rootElementImg.attr("data-fullview-src", "https:" + rootElementImg.attr("data-fullview-src"));
                }
            }
        }
    }

    public static void correctHtmlAHrefAndImgScr(Elements rootElementIn) {
        for (Element currentElement : rootElementIn) {
            correctHtmlAHrefAndImgScr(currentElement);
        }
    }

    public static List<HashMap<String, String>> commentsToListHash(String html) {
        List<HashMap<String, String>> result = new ArrayList<>();

        if (html != null && html.length() > 0) {
            Document doc = Jsoup.parse(html);

            Elements submissionCommentsContainers = doc.select("div.comment_container");
            Elements submissionCommentsContainersCollapsedHeight = submissionCommentsContainers.select("div.collapsed_height");
            submissionCommentsContainers.removeAll(submissionCommentsContainersCollapsedHeight);

            for (Element currentElement : submissionCommentsContainers) {
                HashMap<String, String> currentCommentData = new HashMap<>();

                Element currentElementUserStrong = currentElement.selectFirst("strong.comment_username");

                Element currentElementAvatarDesktopDiv = currentElement.selectFirst("div.avatar-desktop");
                Element currentElementAvatarDesktopA = currentElementAvatarDesktopDiv.selectFirst("a");
                Element currentElementCommentUserAvatarImg = currentElementAvatarDesktopDiv.selectFirst("img.comment_useravatar");

                Element currentElementCommentDiv = currentElement.selectFirst("div.comment_text");
                open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(currentElementCommentDiv);

                Element currentElementReplyToLinkDiv = currentElement.selectFirst("a.replyto_link");
                Element currentElementParentCommentIdDiv = currentElement.selectFirst("a.comment-parent");
                Element currentElementCommentIdDiv = currentElement.selectFirst("a.comment_anchor");

                currentCommentData.put("userName", currentElementUserStrong.text());
                currentCommentData.put("userIcon", "https:" + currentElementCommentUserAvatarImg.attr("src"));
                currentCommentData.put("userLink", currentElementAvatarDesktopA.attr("href"));
                currentCommentData.put("commentDate", currentElement.attr("data-timestamp"));
                currentCommentData.put("comment", currentElementCommentDiv.html());

                if(currentElementReplyToLinkDiv != null) {
                    currentCommentData.put("replyToLink", currentElementReplyToLinkDiv.attr("href"));
                }

                if (currentElementParentCommentIdDiv != null) {
                    String ParentCommentId = currentElementParentCommentIdDiv.attr("href");
                    currentCommentData.put("parentCommentId", ParentCommentId.substring(1));
                }

                currentCommentData.put("commentId", currentElementCommentIdDiv.attr("id"));
                result.add(currentCommentData);
            }
        }

        return result;
    }

    public static HashMap<String, String> getDropDownOptions(Element selectIn) {
        HashMap<String, String> result = new HashMap<>();

        if(selectIn != null) {
            Elements options = selectIn.select("option");

            for(Element option : options) {
                if(option.hasAttr("value")) {
                    result.put(option.attr("value"), option.text());
                }
            }
        }

        return result;
    }
}
