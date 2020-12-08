package open.furaffinity.client.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.dialogs.msgPmsDialog;
import open.furaffinity.client.dialogs.recaptchaV2Dialog;
import open.furaffinity.client.pages.msgPms;

public class sendPm {
    private static final String TAG = sendPm.class.getName();

    private static void sendMessage(Context context, String postKey, String user, String subject, String body, String gRecaptchaResponse) {
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
                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + msgPms.getSendPath(), params);
                    return null;
                }
            }.execute(new webClient(context)).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not reply to message: ", e);
        }
    }

    public static void sendPM(Context context, FragmentManager fragmentManager, String userIn) {
        msgPms msgPms = new msgPms(context, new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                msgPmsDialog msgPmsDialog = new msgPmsDialog();

                if (userIn != null) {
                    msgPmsDialog.setUser(userIn);
                }

                msgPmsDialog.setListener((user, subject, body) -> {
                    if (((msgPms)abstractPage).isRecaptchaRequired()) {
                        recaptchaV2Dialog recaptchaV2Dialog = new recaptchaV2Dialog();
                        recaptchaV2Dialog.setPagePath(open.furaffinity.client.utilities.webClient.getBaseUrl() + ((msgPms)abstractPage).getPagePath());

                        recaptchaV2Dialog.setListener(gRecaptchaResponse -> sendMessage(context, ((msgPms)abstractPage).getPostKey(), user, subject, body, gRecaptchaResponse));
                        recaptchaV2Dialog.show(fragmentManager, "recaptchaV2");
                    } else {
                        sendMessage(context, ((msgPms)abstractPage).getPostKey(), user, subject, body, "");
                    }

                });

                msgPmsDialog.show(fragmentManager, "msgPmsDialog");
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(context, "Failed to load data needed to send note", Toast.LENGTH_SHORT).show();
            }
        });

        msgPms.execute();
    }
}
