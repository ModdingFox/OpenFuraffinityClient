package open.furaffinity.client.pagesOld;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

public class controlsProfile extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = controlsProfile.class.getName();

    private static String pagePath = "/controls/profile/";

    public class inputItem {
        String name;
        String header;
        String value;
        int maxLength;
        HashMap<String, String> options;

        private void initInputItem(String name, String header, String value, int maxLength, HashMap<String, String> options) {
            this.name = name;
            this.header = header;
            this.value = value;
            this.maxLength = maxLength;
            this.options = options;
        }

        public inputItem(String name, String header, String value) {
            initInputItem(name, header, value, Integer.MAX_VALUE, null);
        }

        public inputItem(String name, String header, String value, int maxLength) {
            initInputItem(name, header, value, maxLength, null);
        }

        public inputItem(String name, String header, HashMap<String, String> options) {
            initInputItem(name, header, "", 0, options);
        }

        public inputItem(String name, String header, String value, HashMap<String, String> options) {
            initInputItem(name, header, value, 0, options);
        }

        public String getName() {
            return name;
        }

        public String getHeader() {
            return header;
        }

        public String getValue() {
            return value;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public HashMap<String, String> getOptions() {
            return options;
        }

        public boolean isSelect() {
            return ((options == null) ? (false) : (true));
        }
    }

    private List<inputItem> pageResults = new ArrayList<>();
    private String key;

    public controlsProfile() {
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements controlPanelItemContainerDiv = doc.select("div.control-panel-item-container");
        Element msgFormForm = doc.selectFirst("form[name=MsgForm]");

        if (controlPanelItemContainerDiv != null) {
            for (Element currentControlPanelItemContainerDiv : controlPanelItemContainerDiv) {
                Elements flexItemsDiv = currentControlPanelItemContainerDiv.select("div.flex-item");

                if (flexItemsDiv != null && flexItemsDiv.size() > 0) {
                    for (Element flexItemDiv : flexItemsDiv) {
                        Element h4 = flexItemDiv.selectFirst("h4");
                        Element input = flexItemDiv.selectFirst("input");
                        Element select = flexItemDiv.selectFirst("select");

                        if (h4 != null) {
                            if (input != null) {
                                if (input.hasAttr("maxLength")) {
                                    try {
                                        int maxLength = Integer.parseInt(input.attr("maxLength"));
                                        pageResults.add(new inputItem(input.attr("name"), h4.text(), input.attr("value"), maxLength));
                                    } catch (NumberFormatException e) {
                                        Log.e(TAG, "processPageData: ", e);
                                    }
                                } else {
                                    pageResults.add(new inputItem(input.attr("name"), h4.text(), input.attr("value")));
                                }
                            } else if (select != null) {
                                String selected = null;
                                Elements selectOptions = select.select("option");
                                HashMap<String, String> options = new HashMap<>();

                                for (Element selectOption : selectOptions) {
                                    options.put(selectOption.attr("value"), selectOption.text());

                                    if (selectOption.hasAttr("selected")) {
                                        selected = selectOption.attr("value");
                                    }
                                }

                                pageResults.add(new inputItem(select.attr("name"), h4.text(), selected, options));
                            }
                        }
                    }
                } else {
                    Element h4 = currentControlPanelItemContainerDiv.selectFirst("h4");
                    Element textarea = currentControlPanelItemContainerDiv.selectFirst("textarea");
                    Element select = currentControlPanelItemContainerDiv.selectFirst("select");

                    if (h4 != null) {
                        if (textarea != null) {
                            pageResults.add(new inputItem(textarea.attr("name"), h4.text(), textarea.text()));
                        } else if (select != null) {
                            String selected = null;
                            Elements selectOptions = select.select("option");
                            HashMap<String, String> options = new HashMap<>();

                            for (Element selectOption : selectOptions) {
                                options.put(selectOption.attr("value"), selectOption.text());

                                if (selectOption.hasAttr("selected")) {
                                    selected = selectOption.attr("value");
                                }
                            }

                            pageResults.add(new inputItem(select.attr("name"), h4.text(), selected, options));
                        }
                    }
                }
            }
        }

        if (msgFormForm != null) {
            Element keyInput = msgFormForm.selectFirst("input[name=key]");
            if (keyInput != null) {
                key = keyInput.attr("value");
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

    public List<inputItem> getPageResults() {
        return pageResults;
    }

    public String getKey() {
        return key;
    }
}
