package open.furaffinity.client.pages;

import static open.furaffinity.client.utilities.imageResultsTool.getResultsData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.fragmentDrawers.settings;
import open.furaffinity.client.utilities.imageResultsTool;

public class gallery extends abstractPage {
    private final HashMap<String, String> folderResults = new HashMap<>();
    private final imageResultsTool.imageResolutions currentResolution;
    private String pagePath;
    private String nextPage;
    private List<HashMap<String, String>> pageResults = new ArrayList<>();
    private HashMap<String, String> assignFolderId;
    private String assignFolderSubmit;
    private String createFolderSubmit;
    private String removeFromFoldersSubmit;
    private String moveFromScrapsSubmit;
    private String moveToScrapsSubmit;

    public gallery(Context context, pageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
        currentResolution = imageResultsTool.getimageResolutionFromInt(
            sharedPref.getInt(context.getString(R.string.imageResolutionSetting),
                settings.imageResolutionDefault));
    }

    public gallery(gallery gallery) {
        super(gallery);
        this.pagePath = gallery.pagePath;
        this.currentResolution = gallery.currentResolution;
    }

    protected Boolean processPageData(String html) {
        pageResults = getResultsData(html, currentResolution);

        Document doc = Jsoup.parse(html);
        Element folderListDiv = doc.selectFirst("div.folder-list");

        if (folderListDiv != null) {
            Element currentFolder = folderListDiv.selectFirst("li.active");
            folderResults.put(pagePath, currentFolder.text());

            Elements folderLinks = folderListDiv.select("a");

            if (folderLinks != null) {
                for (Element currentElement : folderLinks) {
                    folderResults.put(currentElement.attr("href"), currentElement.text());
                }
            }
        }

        Elements buttonElements = doc.select("a.button");

        if (buttonElements != null) {
            for (Element currentButton : buttonElements) {
                if (currentButton.text().startsWith("Next")) {
                    nextPage = currentButton.attr("href");
                    break;
                }
            }
        }

        if (nextPage == null) {
            buttonElements = doc.select("button.button");

            if (buttonElements != null) {
                for (Element currentButton : buttonElements) {
                    if (currentButton.text().startsWith("Next")) {
                        nextPage = currentButton.parent().attr("action");
                        break;
                    }
                }
            }
        }

        assignFolderId = new HashMap<>();

        Element assignFolderIdElement = doc.selectFirst("select[name=assign_folder_id]");

        if (assignFolderIdElement != null) {
            Elements assignFolderIdOptionElements = assignFolderIdElement.select("option");

            if (assignFolderIdOptionElements != null) {
                for (Element assignFolderIdOptionElement : assignFolderIdOptionElements) {
                    String value = assignFolderIdOptionElement.attr("value");
                    if (!value.equals("0")) {
                        assignFolderId.put(value, assignFolderIdOptionElement.text());
                    }
                }
            }
        }

        Element assignFolderSubmitElement = doc.selectFirst("button[name=assign_folder_submit]");
        Element createFolderSubmitElement = doc.selectFirst("button[name=create_folder_submit]");
        Element removeFromFoldersSubmitElement =
            doc.selectFirst("button[name=remove_from_folders_submit]");
        Element moveFromScrapsSubmitElement =
            doc.selectFirst("button[name=move_from_scraps_submit]");
        Element moveToScrapsSubmitElement = doc.selectFirst("button[name=move_to_scraps_submit]");

        if (assignFolderSubmitElement != null) {
            assignFolderSubmit = assignFolderSubmitElement.attr("value");
        }

        if (createFolderSubmitElement != null) {
            createFolderSubmit = createFolderSubmitElement.attr("value");
        }

        if (removeFromFoldersSubmitElement != null) {
            removeFromFoldersSubmit = removeFromFoldersSubmitElement.attr("value");
        }

        if (moveFromScrapsSubmitElement != null) {
            moveFromScrapsSubmit = moveFromScrapsSubmitElement.attr("value");
        }

        if (moveToScrapsSubmitElement != null) {
            moveToScrapsSubmit = moveToScrapsSubmitElement.attr("value");
        }

        //currently not really a great way of checking success
        return true;
    }

    @Override protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(
            open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setNextPage() {
        if (this.nextPage != null) {
            this.pagePath = this.nextPage;
        }
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }

    public HashMap<String, String> getFolderResults() {
        return folderResults;
    }

    public HashMap<String, String> getAssignFolderId() {
        return assignFolderId;
    }

    public String getAssignFolderSubmit() {
        return assignFolderSubmit;
    }

    public String getCreateFolderSubmit() {
        return createFolderSubmit;
    }

    public String getRemoveFromFoldersSubmit() {
        return removeFromFoldersSubmit;
    }

    public String getMoveFromScrapsSubmit() {
        return moveFromScrapsSubmit;
    }

    public String getMoveToScrapsSubmit() {
        return moveToScrapsSubmit;
    }
}
