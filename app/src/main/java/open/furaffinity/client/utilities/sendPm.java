package open.furaffinity.client.utilities;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import open.furaffinity.client.abstractClasses.BasePage;
import open.furaffinity.client.dialogs.msgPmsDialog;
import open.furaffinity.client.dialogs.recaptchaV2Dialog;
import open.furaffinity.client.pages.msgPms;

public class sendPm {
    private static void sendMessage(Context context, String postKey, String user, String subject, String body, String gRecaptchaResponse) {
        new open.furaffinity.client.submitPages.submitPm(context, new BasePage.pageListener() {
            @Override
            public void requestSucceeded(BasePage BasePage) {
                Toast.makeText(context, "Successfully sent note", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestFailed(BasePage BasePage) {
                Toast.makeText(context, "Failed to send note", Toast.LENGTH_SHORT).show();
            }
        }, postKey, user, subject, body, gRecaptchaResponse).execute();
    }

    public static void sendPM(Context context, FragmentManager fragmentManager, String userIn) {
        msgPms msgPms = new msgPms(context, new BasePage.pageListener() {
            @Override
            public void requestSucceeded(BasePage abstractPage) {
                msgPmsDialog msgPmsDialog = new msgPmsDialog();

                if (userIn != null) {
                    msgPmsDialog.setUser(userIn);
                }

                msgPmsDialog.setListener((user, subject, body) -> {
                    if (((msgPms) abstractPage).isRecaptchaRequired()) {
                        recaptchaV2Dialog recaptchaV2Dialog = new recaptchaV2Dialog();
                        recaptchaV2Dialog.setPagePath(open.furaffinity.client.utilities.webClient.getBaseUrl() + ((msgPms) abstractPage).getPagePath());

                        recaptchaV2Dialog.setListener(gRecaptchaResponse -> sendMessage(context, ((msgPms) abstractPage).getPostKey(), user, subject, body, gRecaptchaResponse));
                        recaptchaV2Dialog.show(fragmentManager, "recaptchaV2");
                    } else {
                        sendMessage(context, ((msgPms) abstractPage).getPostKey(), user, subject, body, "");
                    }

                });

                msgPmsDialog.show(fragmentManager, "msgPmsDialog");
            }

            @Override
            public void requestFailed(BasePage abstractPage) {
                Toast.makeText(context, "Failed to load data needed to send note", Toast.LENGTH_SHORT).show();
            }
        });

        msgPms.execute();
    }
}
