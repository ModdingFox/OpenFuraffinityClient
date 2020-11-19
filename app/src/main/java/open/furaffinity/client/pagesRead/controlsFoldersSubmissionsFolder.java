package open.furaffinity.client.pagesRead;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class controlsFoldersSubmissionsFolder extends abstractPage {
    String pagePath;
    String folderId;

    HashMap<String, String> existingGroups;
    String selectedGroup;
    String folderName;
    String description;

    String key;

    public controlsFoldersSubmissionsFolder(Context context, pageListener pageListener, String pagePath, String folderId) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.folderId = folderId;
        this.existingGroups = new HashMap<>();
        this.selectedGroup = "";
        this.folderName = "";
        this.description = "";
    }

    public controlsFoldersSubmissionsFolder(controlsFoldersSubmissionsFolder controlsFoldersSubmissionsFolder) {
        super(controlsFoldersSubmissionsFolder);
        this.pagePath = controlsFoldersSubmissionsFolder.pagePath;
        this.folderId = controlsFoldersSubmissionsFolder.folderId;
        this.existingGroups = controlsFoldersSubmissionsFolder.existingGroups;
        this.selectedGroup = controlsFoldersSubmissionsFolder.selectedGroup;
        this.folderName = controlsFoldersSubmissionsFolder.folderName;
        this.description = controlsFoldersSubmissionsFolder.description;
        this.key = controlsFoldersSubmissionsFolder.key;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element groupId = doc.selectFirst("select[name=group_id]");
        Element folderName = doc.selectFirst("input[name=folder_name]");
        Element folderDescription = doc.selectFirst("textarea[name=folder_description]");
        Element keyButton = doc.selectFirst("button[name=key]");

        if (groupId != null) {
            Element selectedGroup = groupId.selectFirst("option[selected]");
            Elements options = groupId.select("option");

            for (Element option : options) {
                if (option.hasAttr("value")) {
                    existingGroups.put(option.attr("value"), option.text());
                }
            }

            if (selectedGroup != null && selectedGroup.hasAttr("value")) {
                this.selectedGroup = selectedGroup.attr("value");
            }
        }

        if (folderName != null) {
            this.folderName = folderName.attr("value");
        }

        if (folderDescription != null) {
            this.description = folderDescription.html();
        }

        if (keyButton != null) {
            this.key = keyButton.attr("value");
        }

        if(groupId != null && folderName != null && folderDescription != null && keyButton != null) {
            return true;
        }

        return false;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        HashMap<String, String> params = new HashMap<>();
        if (folderId != null) {
            params.put("folder_id", folderId);
        }

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
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
