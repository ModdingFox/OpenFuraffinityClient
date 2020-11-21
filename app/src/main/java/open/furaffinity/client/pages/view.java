package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import open.furaffinity.client.abstractClasses.abstractPage;

public class view extends abstractPage {
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

    public view(Context context, pageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
    }

    public view(view view) {
        super(view);
        this.pagePath = view.pagePath;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element favoriteNav = doc.selectFirst("div.favorite-nav");

        if (favoriteNav != null) {
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
                        note = currentElement.attr("href").replace(msgPms.getNotePathPrefix(), "");
                        note = note.substring(0, note.length() - 1);
                        break;
                    case "Next":
                        next = currentElement.attr("href");
                        break;
                }
            }
        }

        Element submissionImgImg = doc.selectFirst("img[id=submissionImg]");

        if (submissionImgImg != null) {
            open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(submissionImgImg);
            submissionImgLink = submissionImgImg.attr("data-fullview-src");
        }

        Element submissionIdContainer = doc.selectFirst("div.submission-id-container");

        if (submissionIdContainer != null) {
            Element submissionIdAvatar = submissionIdContainer.selectFirst("div.submission-id-avatar");

            if (submissionIdAvatar != null) {
                Element submissionIdAvatarUserPage = submissionIdAvatar.selectFirst("a");
                if (submissionIdAvatarUserPage != null) {
                    submissionUserPage = submissionIdAvatarUserPage.attr("href");

                    Element submissionIdAvatarUserIcon = submissionIdAvatarUserPage.selectFirst("img");
                    if (submissionIdAvatarUserIcon != null) {
                        submissionUserIcon = "https:" + submissionIdAvatarUserIcon.attr("src");
                    }
                }
            }

            Element submissionTitleDiv = submissionIdContainer.selectFirst("div.submission-title");
            if (submissionTitleDiv != null) {
                Element submissionTitleP = submissionTitleDiv.selectFirst("p");
                if (submissionTitleP != null) {
                    submissionTitle = submissionTitleP.text();
                }

                //Using the submissionTitleDiv as a ref to find the user name. Yeah its not great but works for now
                Element submissionUserA = submissionTitleDiv.nextElementSibling().selectFirst("a");
                if (submissionUserA != null) {
                    Element submissionUserStrong = submissionUserA.selectFirst("strong");
                    if (submissionUserStrong != null) {
                        submissionUser = submissionUserStrong.text();
                    }
                }
            }

            Element submissionDateSpan = submissionIdContainer.selectFirst("span.popup_date");
            if (submissionDateSpan != null) {
                submissionDate = submissionDateSpan.text();
            }
        }

        Element submissionDescriptionDiv = doc.selectFirst("div.submission-description");
        if (submissionDescriptionDiv != null) {
            Elements submissionDescriptionDivA = submissionDescriptionDiv.select("a");
            if (submissionDescriptionDivA != null) {
                open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(submissionDescriptionDivA);
                submissionDescription = submissionDescriptionDiv.html();
            }
        }

        Element submissionStatsContainer = doc.selectFirst("section.stats-container");
        if(submissionStatsContainer != null) {
            Element submissionCommentsDiv = submissionStatsContainer.selectFirst("div.comments");
            if (submissionCommentsDiv != null) {
                Element submissionCommentsSpan = submissionCommentsDiv.selectFirst("span");
                if (submissionCommentsSpan != null) {
                    submissionCommentCount = submissionCommentsSpan.text();
                }
            }

            Element submissionViewsDiv = submissionStatsContainer.selectFirst("div.views");
            if (submissionViewsDiv != null) {
                Element submissionViewsSpan = submissionViewsDiv.selectFirst("span");
                if (submissionViewsSpan != null) {
                    submissionViews = submissionViewsSpan.text();
                }
            }

            Element submissionFavoritesDiv = submissionStatsContainer.selectFirst("div.favorites");
            if (submissionFavoritesDiv != null) {
                Element submissionFavoritesSpan = submissionFavoritesDiv.selectFirst("span");
                if (submissionFavoritesSpan != null) {
                    submissionFavorites = submissionFavoritesSpan.text();
                }
            }

            Element submissionRatingDiv = submissionStatsContainer.selectFirst("div.rating");
            if (submissionRatingDiv != null) {
                Element submissionRatingSpan = submissionRatingDiv.selectFirst("span");
                if (submissionRatingSpan != null) {
                    submissionRating = submissionRatingSpan.text().trim();
                }
            }
        }

        Element submissionInfoSection = doc.selectFirst("section.info");
        if(submissionInfoSection != null) {
            Element submissionCategoryDiv = submissionInfoSection.selectFirst("div");
            if(submissionCategoryDiv != null) {
                Element submissionCategoryNameSpan = submissionCategoryDiv.selectFirst("span.category-name");
                if(submissionCategoryNameSpan != null) {
                    Element submissionCategoryNameTypeSpan = submissionCategoryDiv.selectFirst("span.type-name");
                    if(submissionCategoryNameTypeSpan != null) {
                        submissionCategory = submissionCategoryNameSpan.text() + " / " + submissionCategoryNameTypeSpan.text();
                    }
                }
            }

            Element submissionSpeciesDiv = submissionCategoryDiv.nextElementSibling();
            if(submissionSpeciesDiv != null) {
                submissionSpecies = submissionSpeciesDiv.text();
            }

            Element submissionGenderDiv = submissionSpeciesDiv.nextElementSibling();
            if(submissionGenderDiv != null) {
                submissionGender = submissionGenderDiv.text();
            }

            Element submissionSizeDiv = submissionGenderDiv.nextElementSibling();
            if(submissionSizeDiv != null) {
                submissionSize = submissionSizeDiv.text();
            }
        }

        Element submissionTagsSection = doc.selectFirst("section.tags-row");
        if(submissionTagsSection != null) {
            Elements submissionTagsSpans = submissionTagsSection.select("span.tags");
            for (Element currentElement : submissionTagsSpans) {
                submissionTags.add(currentElement.text());
            }
        }

        Element submissionCommentsList = doc.selectFirst("div.comments-list");
        if(submissionCommentsList != null) {
            submissionComments = submissionCommentsList.html();
        }

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

        if(submissionIdContainer != null ) {
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

    public String getPagePath() {
        return pagePath;
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
