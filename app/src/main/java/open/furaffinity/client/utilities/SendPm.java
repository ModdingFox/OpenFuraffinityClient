package open.furaffinity.client.utilities;

import android.content.Context;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.dialogs.MsgPmsDialog;
import open.furaffinity.client.dialogs.RecaptchaV2Dialog;
import open.furaffinity.client.pages.MsgPms;
import open.furaffinity.client.submitPages.SubmitPm;

public class SendPm {
    private static void sendMessage(Context context, String postKey, String user, String subject,
                                    String body, String gRecaptchaResponse) {
        new SubmitPm(context, new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                Toast.makeText(context, "Successfully sent note", Toast.LENGTH_SHORT).show();
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                Toast.makeText(context, "Failed to send note", Toast.LENGTH_SHORT).show();
            }
        }, postKey, user, subject, body, gRecaptchaResponse).execute();
    }

    public static void sendPM(Context context, FragmentManager fragmentManager, String userIn) {
        MsgPms msgPms = new MsgPms(context, new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                MsgPmsDialog msgPmsDialog = new MsgPmsDialog();

                if (userIn != null) {
                    msgPmsDialog.setUser(userIn);
                }

                msgPmsDialog.setListener((user, subject, body) -> {
                    if (((MsgPms) abstractPage).isRecaptchaRequired()) {
                        RecaptchaV2Dialog recaptchaV2Dialog = new RecaptchaV2Dialog();
                        recaptchaV2Dialog.setPagePath(
                            WebClient.getBaseUrl() +
                                ((MsgPms) abstractPage).getPagePath());

                        recaptchaV2Dialog.setListener(gRecaptchaResponse -> sendMessage(context,
                            ((MsgPms) abstractPage).getPostKey(), user, subject, body,
                            gRecaptchaResponse));
                        recaptchaV2Dialog.show(fragmentManager, "recaptchaV2");
                    }
                    else {
                        sendMessage(context, ((MsgPms) abstractPage).getPostKey(), user, subject,
                            body, "");
                    }

                });

                msgPmsDialog.show(fragmentManager, "msgPmsDialog");
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                Toast.makeText(context, "Failed to load data needed to send note",
                    Toast.LENGTH_SHORT).show();
            }
        });

        msgPms.execute();
    }
}
