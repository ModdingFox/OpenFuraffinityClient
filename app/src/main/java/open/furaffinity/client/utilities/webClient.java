package open.furaffinity.client.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import open.furaffinity.client.R;

public class webClient {
    private static final String TAG = webClient.class.getName();
    private final List<Cookie> lastPageResponceCookies = new ArrayList<>();
    private final String cookieA;
    private final String cookieB;
    private boolean lastPageLoaded = false;
    private boolean hasLoginCookie = false;
    private boolean followRedirects = true;

    public Context context;

    public webClient(Context context) {
        this.context = context;

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.settingsFile), Context.MODE_PRIVATE);
        cookieA = sharedPref.getString(context.getString(R.string.webClientCookieA), null);
        cookieB = sharedPref.getString(context.getString(R.string.webClientCookieB), null);

        if (cookieA != null && cookieB != null) {
            hasLoginCookie = true;
        }
    }

    public static HashMap<String, String> nameValueToHashMap(String name, String value) {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("value", value);
        return result;
    }

    public static String getBaseUrl() {
        return "https://www.furaffinity.net";
    }

    private void checkPageForErrors(String html) {
        Document doc = Jsoup.parse(html);
        Element head = doc.selectFirst("head");
        Element title = head.selectFirst("title");

        Element body = doc.selectFirst("body");
        Element sectionHeader = body.selectFirst("div.section-header");

        //Some pages doesnt have a section header but luckily they also dont
        if (!title.text().equals("System Error") && (sectionHeader == null || !sectionHeader.text().equals("System Error"))) {
            lastPageLoaded = true;
        }
    }

    private String cookieSetup(HashMap<String, String> cookies) {
        if (cookies == null) {
            cookies = new HashMap<>();
        }

        String additionalCookies = "";

        if (hasLoginCookie) {
            cookies.put("a", cookieA);
            cookies.put("b", cookieB);
        }

        for (String key : cookies.keySet()) {
            additionalCookies += key;
            additionalCookies += "=";
            additionalCookies += cookies.get(key);
            additionalCookies += "; ";
        }

        return additionalCookies;
    }

    private String getPageResponse(int responseCode, HttpURLConnection httpURLConnection) throws IOException {
        String result = null;

        Log.i(TAG, "Http Request: Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK || (!followRedirects && responseCode == HttpURLConnection.HTTP_MOVED_TEMP)) {
            List<String> cookies = httpURLConnection.getHeaderFields().get("set-cookie");
            if (cookies != null) {
                for (String currentCookie : cookies) {
                    try {
                        Cookie cookie = Cookie.parse(HttpUrl.get(httpURLConnection.getURL().toURI()), currentCookie);

                        if (cookie != null) {
                            lastPageResponceCookies.add(cookie);
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder html = new StringBuilder();
            String line;

            boolean foundDOCTYPE = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("!DOCTYPE")) { foundDOCTYPE = true; }
                if(foundDOCTYPE) { html.append(line); }
            }
            result = html.toString();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                checkPageForErrors(result);
            } else if (!followRedirects && responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                lastPageLoaded = true;
            }

            Log.i(TAG, "Http Request: " + result);
        } else {
            Log.e(TAG, "Http Request failed");
        }

        return result;
    }

    public String sendGetRequest(String urlIn, HashMap<String, String> cookies) {
        lastPageLoaded = false;
        String result = null;

        try {
            URL url = new URL(urlIn);
            HttpURLConnection httpURLConnection;
            int responseCode;

            int retry = 3;
            do {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Cookie", cookieSetup(cookies));

                httpURLConnection.setInstanceFollowRedirects(followRedirects);

                responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK || (!followRedirects && responseCode == HttpURLConnection.HTTP_MOVED_TEMP)) {
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

            result = getPageResponse(responseCode, httpURLConnection);
        } catch (IOException e) {
            Log.e(TAG, "sendGetRequest: ", e);
        }

        return result;
    }

    public String sendGetRequest(String urlIn) {
        return sendGetRequest(urlIn, null);
    }

    public String sendPostRequest(String urlIn, HashMap<String, String> paramsIn, HashMap<String, String> cookies) {
        lastPageLoaded = false;
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
                httpURLConnection.setRequestProperty("Cookie", cookieSetup(cookies));

                httpURLConnection.setInstanceFollowRedirects(followRedirects);

                DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                outputStream.write(params);

                responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK || (!followRedirects && responseCode == HttpURLConnection.HTTP_MOVED_TEMP)) {
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

            result = getPageResponse(responseCode, httpURLConnection);
        } catch (IOException e) {
            Log.e(TAG, "sendGetRequest: ", e);
        }

        return result;
    }

    public String sendPostRequest(String urlIn, HashMap<String, String> paramsIn) {
        return sendPostRequest(urlIn, paramsIn, null);
    }

    public String sendPostRequest(String urlIn, List<HashMap<String, String>> paramsIn, HashMap<String, String> cookies, boolean includeBountryAtFoot) {
        lastPageLoaded = false;
        String result = null;

        String boundry = "---------------------------" + UUID.randomUUID().toString().replace("-", "");
        String LINE_FEED = "\r\n";

        try {
            URL url = new URL(urlIn);
            HttpURLConnection httpURLConnection;
            int responseCode;

            int retry = 3;
            do {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setChunkedStreamingMode(1024);
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundry);
                httpURLConnection.setRequestProperty("charset", "utf-8");
                httpURLConnection.setRequestProperty("Cookie", cookieSetup(cookies));

                httpURLConnection.setInstanceFollowRedirects(followRedirects);

                DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());

                //Testing data stuff
                //ByteArrayOutputStream testByteArrayOutputStream = new ByteArrayOutputStream();
                //DataOutputStream outputStream = new DataOutputStream(testByteArrayOutputStream);

                for (HashMap<String, String> currentParams : paramsIn) {
                    if (currentParams.containsKey("name") && (currentParams.containsKey("value") || currentParams.containsKey("filePath"))) {
                        outputStream.writeBytes("--" + boundry + LINE_FEED);
                        outputStream.writeBytes("Content-Disposition: form-data; " + "name=\"" + currentParams.get("name") + "\"");

                        if (currentParams.containsKey("filePath")) {
                            Uri uri = Uri.parse(currentParams.get("filePath"));

                            try {
                                Cursor sourceFileCursor = context.getContentResolver().query(uri, null, null, null);

                                if(sourceFileCursor.moveToFirst()) {
                                    int displayNameColumnIndex = sourceFileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    String fileName = sourceFileCursor.getString(displayNameColumnIndex);

                                    String contentType = URLConnection.guessContentTypeFromName(fileName);
                                    outputStream.writeBytes("; filename=\"" + fileName + "\"" + LINE_FEED);
                                    outputStream.writeBytes("Content-Type: " + ((contentType != null) ? (contentType) : ("application/octet-stream")) + LINE_FEED + LINE_FEED);

                                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                                    byte[] buffer = new byte[1024];
                                    int bytesRead;
                                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, bytesRead);
                                    }
                                    outputStream.flush();
                                    inputStream.close();
                                } else {
                                    throw new FileNotFoundException("Could not find filename");
                                }
                            } catch (FileNotFoundException | NullPointerException e) {
                                //Just dump and empty file for now. I dont like this but in theory it should work for this apps purposes
                                outputStream.writeBytes("; filename=\"\"" + LINE_FEED);
                                outputStream.writeBytes("Content-Type: application/octet-stream" + LINE_FEED + LINE_FEED);
                            }
                        } else {
                            outputStream.writeBytes(LINE_FEED + LINE_FEED);
                            outputStream.writeBytes(currentParams.get("value"));
                        }

                        outputStream.writeBytes(LINE_FEED);
                    }
                }

                if (includeBountryAtFoot) {
                    outputStream.writeBytes(boundry + "--" + LINE_FEED);
                }

                outputStream.flush();
                outputStream.close();

                //Testing data stuff dump to console
                //Log.i(TAG, testByteArrayOutputStream.toString());

                responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK || (!followRedirects && responseCode == HttpURLConnection.HTTP_MOVED_TEMP)) {
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

            result = getPageResponse(responseCode, httpURLConnection);
        } catch (IOException e) {
            Log.e(TAG, "sendPostRequest: ", e);
        }

        return result;
    }

    public String sendPostRequest(String urlIn, List<HashMap<String, String>> paramsIn) {
        return sendPostRequest(urlIn, paramsIn, null, false);
    }

    public String sendPostRequest(String urlIn, List<HashMap<String, String>> paramsIn, boolean includeBountryAtFoot) {
        return sendPostRequest(urlIn, paramsIn, null, includeBountryAtFoot);
    }

    public boolean getLastPageLoaded() {
        return lastPageLoaded;
    }

    public List<Cookie> getLastPageResponceCookies() {
        return lastPageResponceCookies;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }
}