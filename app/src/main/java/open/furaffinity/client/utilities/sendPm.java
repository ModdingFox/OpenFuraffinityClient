package open.furaffinity.client.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.dialogs.msgPmsDialog;
import open.furaffinity.client.dialogs.recaptchaV2Dialog;

public class sendPm {
    private static String TAG = sendPm.class.getName();

    private static void sendMessage(Context context, String postKey, String user, String subject, String body, String gRecaptchaResponse){
        HashMap<String, String> params = new HashMap<>();
        params.put("key", postKey);
        params.put("to", user);
        params.put("subject", subject);
        params.put("message", body);
        params.put("g-recaptcha-response", gRecaptchaResponse);

        try {
            new AsyncTask<webClient, Void, Void>() {
                @Override
                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + open.furaffinity.client.pages.msgPms.getSendPath(), params);
                    return null;
                }
            }.execute(new webClient(context)).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not reply to message: ", e);
        }
    }

    public static void sendPM(Context context, FragmentManager fragmentManager, String userIn) {
        open.furaffinity.client.pages.msgPms msgPms = new open.furaffinity.client.pages.msgPms();

        try {
            msgPms.execute(new webClient(context)).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }

        msgPmsDialog msgPmsDialog = new msgPmsDialog();

        if(userIn != null) {
            msgPmsDialog.setUser(userIn);
        }

        msgPmsDialog.setListener((user, subject, body) -> {
            if(msgPms.isRecaptchaRequired()) {
                recaptchaV2Dialog recaptchaV2Dialog = new recaptchaV2Dialog();
                recaptchaV2Dialog.setPagePath(open.furaffinity.client.utilities.webClient.getBaseUrl() + msgPms.getPagePath());
                recaptchaV2Dialog.setListener(new recaptchaV2Dialog.recaptchaV2DialogListener() {
                    @Override
                    public void gRecaptchaResponseFound(String gRecaptchaResponse) {
                        sendMessage(context, msgPms.getPostKey(), user, subject, body, gRecaptchaResponse);
                    }
                });
                recaptchaV2Dialog.show(fragmentManager, "recaptchaV2");
            } else {
                sendMessage(context, msgPms.getPostKey(), user, subject, body, "");
            }
        });

        msgPmsDialog.show(fragmentManager, "msgPmsDialog");
    }
}
