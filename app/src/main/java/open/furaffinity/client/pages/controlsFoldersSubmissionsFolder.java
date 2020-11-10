package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.utilities.webClient;

public class controlsFoldersSubmissionsFolder extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = controlsFoldersSubmissionsFolder.class.getName();

    String pagePath;
    String folderId;

    HashMap<String, String> existingGroups;
    String selectedGroup;
    String folderName;
    String description;

    String key;

    public controlsFoldersSubmissionsFolder(String pagePath, String folderId) {
        this.pagePath = pagePath;
        this.folderId = folderId;
        this.existingGroups = new HashMap<>();
        this.selectedGroup = "";
        this.folderName = "";
        this.description = "";
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element groupId = doc.selectFirst("select[name=group_id]");
        Element folderName = doc.selectFirst("input[name=folder_name]");
        Element folderDescription = doc.selectFirst("textarea[name=folder_description]");
        Element keyButton = doc.selectFirst("button[name=key]");

        if(groupId != null) {
            Element selectedGroup = groupId.selectFirst("option[selected]");
            Elements options = groupId.select("option");

            for(Element option : options) {
                if(option.hasAttr("value")) {
                    existingGroups.put(option.attr("value"), option.text());
                }
            }

            if(selectedGroup != null && selectedGroup.hasAttr("value")) {
                this.selectedGroup = selectedGroup.attr("value");
            }
        }

        if(folderName != null) {
            this.folderName = folderName.attr("value");
        }

        if(folderDescription != null) {
            this.description = folderDescription.html();
        }

        if(keyButton != null) {
            this.key = keyButton.attr("value");
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        HashMap<String, String> params = new HashMap<>();
        if(folderId != null) {
            params.put("folder_id", folderId);
        }

        html = webClient[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        processPageData(html);
        return null;
    }

    public String getPagePath() {
        return pagePath;
    }

    public HashMap<String, String> getExistingGroups() {
        return existingGroups;
    }

    public String getSelectedGroup() {
        return selectedGroup;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }
}
