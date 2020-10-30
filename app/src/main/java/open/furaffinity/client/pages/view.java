package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

public class view extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = view.class.getName();

    private boolean isLoaded = false;

    private String pagePath;

    private String prev;
    private boolean isFav;
    private String favUnFav;
    private String mainGallery;
    private String download;
    private String note;
    private String next;

    private String submissionImgLink;

    private String submissionUserIcon;
    private String submissionUser;
    private String submissionUserPage;
    private String submissionTitle;
    private String submissionDate;
    private String submissionDescription;

    private String submissionCommentCount;
    private String submissionViews;
    private String submissionFavorites;
    private String submissionRating;

    private String submissionCategory;
    private String submissionSpecies;
    private String submissionGender;
    private String submissionSize;

    private List<String> submissionTags = new ArrayList<>();
    private String submissionComments;

    private List<String> folderList = new ArrayList<>();

    public view(String pagePath) {
        this.pagePath = pagePath;
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element favoriteNav = doc.selectFirst("div.favorite-nav");

        if(favoriteNav != null) {
            Elements favoriteNavA = favoriteNav.select("a.button.standard");

            for (Element currentElement : favoriteNavA) {
                switch (currentElement.text()) {
                    case "Prev":
                        prev = currentElement.attr("href");
                        break;
                    case "+Fav":
                        isFav = false;
                        favUnFav = currentElement.attr("href");
                        break;
                    case "-Fav":
                        isFav = true;
                        favUnFav = currentElement.attr("href");
                        break;
                    case "Main Gallery":
                        mainGallery = currentElement.attr("href");
                        break;
                    case "Download":
                        download = "https:" + currentElement.attr("href");
                        break;
                    case "Note":
                        note = currentElement.attr("href");
                        break;
                    case "Next":
                        next = currentElement.attr("href");
                        break;
                }
            }
        }

        Element submissionImgImg = doc.selectFirst("img[id=submissionImg]");

        if(submissionImgImg != null) {
            open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(submissionImgImg);
            submissionImgLink = submissionImgImg.attr("data-fullview-src");
        }

        Element submissionIdContainer = doc.selectFirst("div.submission-id-container");

        Element submissionIdAvatar = submissionIdContainer.selectFirst("div.submission-id-avatar");

        Element submissionIdAvatarUserPage = submissionIdAvatar.selectFirst("a");
        submissionUserPage = submissionIdAvatarUserPage.attr("href");

        Element submissionIdAvatarUserIcon = submissionIdAvatarUserPage.selectFirst("img");
        submissionUserIcon = "https:" + submissionIdAvatarUserIcon.attr("src");

        Element submissionTitleDiv = submissionIdContainer.selectFirst("div.submission-title");
        Element submissionTitleP = submissionTitleDiv.selectFirst("p");
        submissionTitle = submissionTitleP.text();

        //Using the submissionTitleDiv as a ref to find the user name. Yeah its not great but works for now
        Element submissionUserA = submissionTitleDiv.nextElementSibling().selectFirst("a");
        Element submissionUserStrong = submissionUserA.selectFirst("strong");
        submissionUser = submissionUserStrong.text();

        Element submissionDateSpan = submissionIdContainer.selectFirst("span.popup_date");
        submissionDate = submissionDateSpan.text();

        Element submissionDescriptionDiv = doc.selectFirst("div.submission-description");
        Elements submissionDescriptionDivA = submissionDescriptionDiv.select("a");
        open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(submissionDescriptionDivA);
        submissionDescription = submissionDescriptionDiv.html();

        Element submissionStatsContainer = doc.selectFirst("section.stats-container");

        Element submissionCommentsDiv = submissionStatsContainer.selectFirst("div.comments");
        Element submissionCommentsSpan = submissionCommentsDiv.selectFirst("span");
        submissionCommentCount = submissionCommentsSpan.text();

        Element submissionViewsDiv = submissionStatsContainer.selectFirst("div.views");
        Element submissionViewsSpan = submissionViewsDiv.selectFirst("span");
        submissionViews = submissionViewsSpan.text();

        Element submissionFavoritesDiv = submissionStatsContainer.selectFirst("div.favorites");
        Element submissionFavoritesSpan = submissionFavoritesDiv.selectFirst("span");
        submissionFavorites = submissionFavoritesSpan.text();

        Element submissionRatingDiv = submissionStatsContainer.selectFirst("div.rating");
        Element submissionRatingSpan = submissionRatingDiv.selectFirst("span");
        submissionRating = submissionRatingSpan.text().trim();

        Element submissionInfoSection = doc.selectFirst("section.info");
        Element submissionCategoryDiv = submissionInfoSection.selectFirst("div");
        Element submissionCategoryNameSpan = submissionCategoryDiv.selectFirst("span.category-name");
        Element submissionCategoryNameTypeSpan = submissionCategoryDiv.selectFirst("span.type-name");
        submissionCategory = submissionCategoryNameSpan.text() + " / " + submissionCategoryNameTypeSpan.text();

        Element submissionSpeciesDiv = submissionCategoryDiv.nextElementSibling();
        submissionSpecies = submissionSpeciesDiv.text();

        Element submissionGenderDiv = submissionSpeciesDiv.nextElementSibling();
        submissionGender = submissionGenderDiv.text();

        Element submissionSizeDiv = submissionGenderDiv.nextElementSibling();
        submissionSize = submissionSizeDiv.text();

        Element submissionTagsSection = doc.selectFirst("section.tags-row");
        Elements submissionTagsSpans = submissionTagsSection.select("span.tags");
        for (Element currentElement : submissionTagsSpans) {
            submissionTags.add(currentElement.text());
        }

        Element submissionCommentsList = doc.selectFirst("div.comments-list");
        submissionComments = submissionCommentsList.html();

        Element folderListContainerSection = doc.selectFirst("section.folder-list-container");
        if (folderListContainerSection != null) {
            Elements folderListContainerSectionA = folderListContainerSection.select("a");

            for (Element currentElement : folderListContainerSectionA) {
                Element currentElementSpan = currentElement.selectFirst("span");

                if (currentElementSpan != null) {
                    folderList.add(currentElementSpan.text() + "\n" + currentElement.attr("href"));
                }
            }
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        isLoaded = webClient[0].getLastPageLoaded();
        if (isLoaded) {
            processPageData(html);
        }
        return null;
    }

    public String getPagePath() { return pagePath; }

    public boolean getIsLoaded() {
        return isLoaded;
    }

    public String getPrev() {
        return prev;
    }

    public boolean getIsFav() {
        return isFav;
    }

    public String getFavUnFav() {
        return favUnFav;
    }

    public String getMainGallery() {
        return mainGallery;
    }

    public String getDownload() {
        return download;
    }

    public String getNote() {
        return note;
    }

    public String getNext() {
        return next;
    }

    public String getSubmissionImgLink() {
        return submissionImgLink;
    }

    public String getSubmissionUserIcon() {
        return submissionUserIcon;
    }

    public String getSubmissionUser() {
        return submissionUser;
    }

    public String getSubmissionUserPage() {
        return submissionUserPage;
    }

    public String getSubmissionTitle() {
        return submissionTitle;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public String getSubmissionDescription() {
        return submissionDescription;
    }

    public String getSubmissionViews() {
        return submissionViews;
    }

    public String getSubmissionCommentCount() {
        return submissionCommentCount;
    }

    public String getSubmissionFavorites() {
        return submissionFavorites;
    }

    public String getSubmissionRating() {
        return submissionRating;
    }

    public String getSubmissionCategory() {
        return submissionCategory;
    }

    public String getSubmissionSpecies() {
        return submissionSpecies;
    }

    public String getSubmissionGender() {
        return submissionGender;
    }

    public String getSubmissionSize() {
        return submissionSize;
    }

    public List<String> getSubmissionTags() {
        return submissionTags;
    }

    public String getSubmissionComments() {
        return submissionComments;
    }

    public List<String> getFolderList() {
        return folderList;
    }
}
