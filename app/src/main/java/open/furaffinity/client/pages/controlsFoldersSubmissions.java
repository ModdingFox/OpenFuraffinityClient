package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

public class controlsFoldersSubmissions extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = controlsFoldersSubmissions.class.getName();

    private static String pagePath = "/controls/folders/submissions/";
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    private String createGroupKey = "";
    private String renameGroupKey = "";
    HashMap<String, String> existingGroups = new HashMap<>();
    String selectedGroup;

    public controlsFoldersSubmissions() {
    }

    private HashMap<String, String> extractFormelements(Element form, String name) {
        HashMap<String, String> result = new HashMap<>();

        if(form != null) {
            if(form.hasAttr("action")) {
                result.put(name + "action", form.attr("action"));
            }

            Elements inputs = form.select("input");

            if(inputs != null) {
                for(Element input : inputs) {
                    if(input.hasAttr("name") && input.hasAttr("value")) {
                        result.put(name + input.attr("name"), input.attr("value"));
                    }
                }
            }

            Element button = form.selectFirst("button[name=key]");

            if(button != null) {
                if(button.hasAttr("value")) {
                    result.put(name + "key", button.attr("value"));
                }
            }
        }

        return result;
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements groupsAndFolders = doc.select("tr.group-row,tr.folder-row");
        Element addGroupForm = doc.selectFirst("form[id=add-group]");
        Element renameGroupForm = doc.selectFirst("form[id=edit-group]");

        if(groupsAndFolders != null && groupsAndFolders.size() > 0) {
            for(Element groupOrFolder : groupsAndFolders) {
                HashMap<String, String> newItem = new HashMap<>();

                if(groupOrFolder.is("tr.group-row")){
                    newItem.put("type", "group");
                } else {
                    newItem.put("type", "folder");
                }

                Element td = groupOrFolder.selectFirst("td");
                if(td != null) {
                    Elements tdChildren = td.children();
                    if(tdChildren.size() >= 2) {
                        if(tdChildren.get(0).is("form")) {
                            newItem.putAll(extractFormelements(tdChildren.get(0), "up"));
                        }

                        if(tdChildren.get(1).is("form")) {
                            newItem.putAll(extractFormelements(tdChildren.get(1), "down"));
                        }
                    }
                }
                td = td.nextElementSibling();
                Element img = td.selectFirst("img");

                if(img != null && img.hasAttr("src")) {
                    newItem.put("iconLink", open.furaffinity.client.utilities.webClient.getBaseUrl() + img.attr("src"));
                }

                td = td.nextElementSibling();

                Element name = td.selectFirst("h2");

                if(name != null) {
                    newItem.put("name", name.text());
                } else {
                    name = td.selectFirst("h3");
                    if(name != null) {
                        newItem.put("name", name.text());
                    }
                }

                td = td.nextElementSibling();

                Elements tdChildren = td.children();
                if(tdChildren.size() >= 3) {
                    if(tdChildren.get(0).is("form")) {
                        newItem.putAll(extractFormelements(tdChildren.get(0), "edit"));
                    }

                    if(tdChildren.get(1).is("form")) {
                        newItem.putAll(extractFormelements(tdChildren.get(1), "delete"));
                    }

                    if(tdChildren.get(2).is("form")) {
                        newItem.putAll(extractFormelements(tdChildren.get(2), "add"));
                    }
                }

                pageResults.add(newItem);
            }
        }

        if(addGroupForm != null) {
            Element keyButton = addGroupForm.selectFirst("button[name=key]");
            if(keyButton != null) {
                createGroupKey = keyButton.attr("value");
            }
        }

        if(renameGroupForm != null) {
            Element keyButton = renameGroupForm.selectFirst("button[name=key]");
            if(keyButton != null) {
                renameGroupKey = keyButton.attr("value");
            }

            Element groupId = renameGroupForm.selectFirst("select[name=group_id]");

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
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public static String getPagePath() {
        return pagePath;
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }

    public String getCreateGroupKey() {
        return createGroupKey;
    }

    public String getRenameGroupKey() {
        return renameGroupKey;
    }

    public HashMap<String, String> getExistingGroups() {
        return existingGroups;
    }

    public String getSelectedGroup() {
        return selectedGroup;
    }
}
