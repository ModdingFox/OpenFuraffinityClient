package open.furaffinity.client.pages;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.abstractClasses.abstractPage;

public class controlsProfile extends abstractPage {
    private static final String TAG = controlsProfile.class.getName();

    private static final String pagePath = "/controls/profile/";
    private final List<inputItem> pageResults = new ArrayList<>();
    private String key;
    public controlsProfile(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public controlsProfile(controlsProfile controlsProfile) {
        super(controlsProfile);
    }

    public static String getPagePath() {
        return pagePath;
    }

    protected Boolean processPageData(String html) {
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

        return controlPanelItemContainerDiv != null && controlPanelItemContainerDiv.size() > 0 && msgFormForm != null;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public List<inputItem> getPageResults() {
        return pageResults;
    }

    public String getKey() {
        return key;
    }

    public static class inputItem {
        String name;
        String header;
        String value;
        int maxLength;
        HashMap<String, String> options;

        public inputItem(String name, String header, String value) {
            initInputItem(name, header, value, Integer.MAX_VALUE, null);
        }

        public inputItem(String name, String header, String value, int maxLength) {
            initInputItem(name, header, value, maxLength, null);
        }

        public inputItem(String name, String header, String value, HashMap<String, String> options) {
            initInputItem(name, header, value, 0, options);
        }

        private void initInputItem(String name, String header, String value, int maxLength, HashMap<String, String> options) {
            this.name = name;
            this.header = header;
            this.value = value;
            this.maxLength = maxLength;
            this.options = options;
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
            return (options != null);
        }
    }
}
