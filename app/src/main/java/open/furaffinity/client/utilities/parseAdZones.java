package open.furaffinity.client.utilities;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import app.cash.quickjs.QuickJs;

public class parseAdZones {
    private static final String TAG = parseAdZones.class.getName();

    public static List<Integer> getAdZones(String html) {
        List<Integer> result = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements scripts = doc.select("script");
        for(Element currentScript : scripts) {
            String currentScriptBody = currentScript.data();
            if(currentScriptBody.contains("var adData")) {
                currentScriptBody = currentScriptBody.replace(";", ";\n");
                for(String line : currentScriptBody.split("\n")) {
                    if(line.contains("var adData")) {
                        line = line.trim();
                        QuickJs engine = QuickJs.create();
                        String adConfigData = (String)engine.evaluate(line + "\nJSON.stringify(adData);");
                        try {
                            JSONObject adConfigDataJson = new JSONObject(adConfigData);
                            if(adConfigDataJson.has("adConfig")){
                                JSONObject adConfig = adConfigDataJson.getJSONObject("adConfig");
                                if(adConfig.has("inhouse")){
                                    JSONObject inhouse = adConfig.getJSONObject("inhouse");
                                    for (Iterator<String> it = inhouse.keys(); it.hasNext(); ) {
                                        String inhouseKey = it.next();
                                        JSONObject inhouseElement = inhouse.getJSONObject(inhouseKey);
                                        if(inhouseElement.has("default")) {
                                            JSONObject inhouseElementDefault = inhouseElement.getJSONObject("default");
                                            if(inhouseElementDefault.has("tagId")) {
                                                result.add(inhouseElementDefault.getInt("tagId"));
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "processPageData: ", e);
                        }
                        result = result.stream().distinct().collect(Collectors.toList());
                        break;
                    }
                }
            }
        }
        return result;
    }
}
