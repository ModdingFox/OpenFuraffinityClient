package open.furaffinity.client.pages;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import open.furaffinity.client.utilities.webClient;

import static open.furaffinity.client.utilities.imageResultsTool.getResultsData;

public class gallery extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = gallery.class.getName();

    private String pagePath;
    private String nextPage;
    private List<HashMap<String, String>> pageResults = new ArrayList<>();
    private HashMap<String, String> folderResults = new HashMap<>();

    private HashMap<String, String> assignFolderId;
    private String assignFolderSubmit;
    private String createFolderSubmit;
    private String removeFromFoldersSubmit;
    private String moveFromScrapsSubmit;
    private String moveToScrapsSubmit;

    public gallery(String pagePath) {
        this.pagePath = pagePath;
    }

    public gallery(gallery gallery) {
        this.pagePath = gallery.pagePath;
    }

    private void processPageData(String html) {
        pageResults = getResultsData(html);

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

        if(buttonElements != null) {
            for(Element currentButton : buttonElements) {
                if(currentButton.text().startsWith("Next")) {
                    nextPage = currentButton.attr("href");
                    break;
                }
            }
        }

        if(nextPage == null){
            buttonElements = doc.select("button.button");

            if(buttonElements != null) {
                for(Element currentButton : buttonElements) {
                    if(currentButton.text().startsWith("Next")) {
                        nextPage = currentButton.parent().attr("action");
                        break;
                    }
                }
            }
        }

        assignFolderId = new HashMap<>();

        Element assignFolderIdElement = doc.selectFirst("select[name=assign_folder_id]");

        if(assignFolderIdElement != null) {
            Elements assignFolderIdOptionElements = assignFolderIdElement.select("option");

            if(assignFolderIdOptionElements != null) {
                for (Element assignFolderIdOptionElement : assignFolderIdOptionElements) {
                    String value = assignFolderIdOptionElement.attr("value");
                    if(!value.equals("0")) {
                        assignFolderId.put(value, assignFolderIdOptionElement.text());
                    }
                }
            }
        }

        Element assignFolderSubmitElement = doc.selectFirst("button[name=assign_folder_submit]");
        Element createFolderSubmitElement = doc.selectFirst("button[name=create_folder_submit]");
        Element removeFromFoldersSubmitElement = doc.selectFirst("button[name=remove_from_folders_submit]");
        Element moveFromScrapsSubmitElement = doc.selectFirst("button[name=move_from_scraps_submit]");
        Element moveToScrapsSubmitElement = doc.selectFirst("button[name=move_to_scraps_submit]");

        if(assignFolderSubmitElement != null) {
            assignFolderSubmit = assignFolderSubmitElement.attr("value");
        }

        if(createFolderSubmitElement != null) {
            createFolderSubmit = createFolderSubmitElement.attr("value");
        }

        if(removeFromFoldersSubmitElement != null) {
            removeFromFoldersSubmit = removeFromFoldersSubmitElement.attr("value");
        }

        if(moveFromScrapsSubmitElement != null) {
            moveFromScrapsSubmit = moveFromScrapsSubmitElement.attr("value");
        }

        if(moveToScrapsSubmitElement != null) {
            moveToScrapsSubmit = moveToScrapsSubmitElement.attr("value");
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setNextPage() {
        if(this.nextPage != null) {
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
