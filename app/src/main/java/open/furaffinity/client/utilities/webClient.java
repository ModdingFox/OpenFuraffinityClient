package open.furaffinity.client.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

import open.furaffinity.client.R;

public class webClient {
    private static final String TAG = webClient.class.getName();

    private boolean lastPageLoaded = false;
    private String cookieA = null;
    private String cookieB = null;
    private boolean hasLoginCookie = false;

    public webClient(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.settingsFile), Context.MODE_PRIVATE);
        cookieA = sharedPref.getString(context.getString(R.string.webClientCookieA), null);
        cookieB = sharedPref.getString(context.getString(R.string.webClientCookieB), null);

        if (cookieA != null && cookieB != null) {
            hasLoginCookie = true;
        }
    }

    public String sendGetRequest(String urlIn, HashMap<String, String> cookies) {
        if (cookies == null) {
            cookies = new HashMap<>();
        }

        String result = null;
        String additionalCookies = "";

        if (hasLoginCookie) {
            cookies.put("a", cookieA);
            cookies.put("b", cookieB);
        }

        if (cookies != null) {
            for (String key : cookies.keySet()) {
                additionalCookies += key;
                additionalCookies += "=";
                additionalCookies += cookies.get(key);
                additionalCookies += "; ";
            }
        }

        try {
            URL url = new URL(urlIn);
            HttpURLConnection httpURLConnection;
            int responseCode;

            int retry = 3;
            do {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Cookie", additionalCookies);
                responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    break;
                } else {
                    retry--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "sendGetRequest could not sleep: ", e);
                    }
                }
            } while (retry > 0);


            Log.i(TAG, "sendGetRequest: GET Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder html = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    html.append(line);
                }
                result = html.toString();

                Document doc = Jsoup.parse(result);
                Element head = doc.selectFirst("head");
                Element title = head.selectFirst("title");

                Element body = doc.selectFirst("body");
                Element sectionHeader = body.selectFirst("div.section-header");

                //Some pages doesnt have a section header but luckily they also dont
                if (!title.text().equals("System Error") && (sectionHeader == null || !sectionHeader.text().equals("System Error"))) {
                    lastPageLoaded = true;
                }

                Log.i(TAG, "sendGetRequest: " + result);
            } else {
                Log.e(TAG, "sendGetRequest: GET request failed");
            }
        } catch (IOException e) {
            Log.e(TAG, "sendGetRequest: ", e);
        }

        return result;
    }

    public String sendGetRequest(String urlIn) {
        return sendGetRequest(urlIn, null);
    }

    public String sendPostRequest(String urlIn, HashMap<String, String> paramsIn, HashMap<String, String> cookies) {
        if (cookies == null) {
            cookies = new HashMap<>();
        }

        String result = null;
        byte[] params = paramsIn.entrySet().stream().map(pair ->
        {
            try {
                return URLEncoder.encode(pair.getKey(), StandardCharsets.UTF_8.toString()) + "=" + URLEncoder.encode(pair.getValue(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "sendPostRequest: ", e);
                return "";
            }
        }).collect(Collectors.joining("&")).getBytes();

        String additionalCookies = "";

        if (hasLoginCookie) {
            cookies.put("a", cookieA);
            cookies.put("b", cookieB);
        }

        if (cookies != null) {
            for (String key : cookies.keySet()) {
                additionalCookies += key;
                additionalCookies += "=";
                additionalCookies += cookies.get(key);
                additionalCookies += "; ";
            }
        }

        try {
            URL url = new URL(urlIn);
            HttpURLConnection httpURLConnection;
            int responseCode;

            int retry = 3;
            do {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("charset", "utf-8");
                httpURLConnection.setRequestProperty("Content-Length", Integer.toString(params.length));
                httpURLConnection.setRequestProperty("Cookie", additionalCookies);

                DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                outputStream.write(params);

                responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    break;
                } else {
                    retry--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "sendPostRequest could not sleep: ", e);
                    }
                }
            } while (retry > 0);

            Log.i(TAG, "sendPostRequest: POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder html = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    html.append(line);
                }
                result = html.toString();

                Document doc = Jsoup.parse(result);
                Element head = doc.selectFirst("head");
                Element title = head.selectFirst("title");

                Element body = doc.selectFirst("body");
                Element sectionHeader = body.selectFirst("div.section-header");

                if (!title.text().equals("System Error") && !sectionHeader.text().equals("System Error")) {
                    lastPageLoaded = true;
                }

                Log.i(TAG, result);
            } else {
                Log.e(TAG, "sendGetRequest: GET request failed");
            }
        } catch (IOException e) {
            Log.e(TAG, "sendGetRequest: ", e);
        }

        return result;
    }

    public String sendPostRequest(String urlIn, HashMap<String, String> paramsIn) {
        return sendPostRequest(urlIn, paramsIn, null);
    }

    public boolean getLastPageLoaded() {
        return lastPageLoaded;
    }

    public static String getBaseUrl() {
        return "https://www.furaffinity.net";
    }

}