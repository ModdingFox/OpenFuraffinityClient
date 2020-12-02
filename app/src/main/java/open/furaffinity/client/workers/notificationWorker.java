package open.furaffinity.client.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.fragmentDrawers.settings;
import open.furaffinity.client.pages.msgOthers;
import open.furaffinity.client.pages.msgPms;
import open.furaffinity.client.pages.msgSubmission;
import open.furaffinity.client.utilities.webClient;

public class notificationWorker extends Worker {
    private static final String TAG = notificationWorker.class.getName();

    private Context context;

    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.msgOthers msgOthers;
    private open.furaffinity.client.pages.msgPms msgPms;
    private open.furaffinity.client.pages.msgSubmission msgSubmission;

    private List<HashMap<String, String>> msgPmsData = new ArrayList<>();
    private List<HashMap<String, String>> msgSubmissionData = new ArrayList<>();

    private void initClientAndPage() {
        loginCheck = new open.furaffinity.client.pages.loginCheck(context, new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {

            }

            @Override
            public void requestFailed(abstractPage abstractPage) {

            }
        });

        msgOthers = new msgOthers(context, new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {

            }

            @Override
            public void requestFailed(abstractPage abstractPage) {

            }
        });

        msgPms = new msgPms(context, new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {

            }

            @Override
            public void requestFailed(abstractPage abstractPage) {

            }
        });

        msgPms.setSelectedFolder(open.furaffinity.client.pages.msgPms.mailFolders.unread);
        msgSubmission = new msgSubmission(context, new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {

            }

            @Override
            public void requestFailed(abstractPage abstractPage) {

            }
        }, true);
    }

    private void fetchPageData() {
        try {
            loginCheck.execute().get();
            if (loginCheck.getIsLoggedIn()) {
                msgOthers.execute().get();

                do {
                    msgPms = new msgPms(msgPms);
                    msgPms.execute().get();
                    msgPms.setPage(msgPms.getPage() + 1);

                    if (msgPms.getMessages() != null) {
                        msgPms.getMessages().removeAll(msgPmsData);
                        msgPmsData.addAll(msgPms.getMessages());
                    }
                } while (msgPms.getMessages() != null && msgPms.getMessages().size() > 0);

                do {
                    msgSubmission = new msgSubmission(msgSubmission);
                    msgSubmission.execute().get();
                    msgSubmission.setNextPage();

                    if (msgSubmission.getPageResults() != null) {
                        msgSubmission.getPageResults().removeAll(msgSubmissionData);
                        msgSubmissionData.addAll(msgSubmission.getPageResults());
                    }
                } while (msgPms.getMessages() != null && msgPms.getMessages().size() > 0);
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }
    }

    public notificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override
    public Result doWork() {
        initClientAndPage();
        fetchPageData();

        if (loginCheck.getIsLoggedIn()) {
            List<HashMap<String, String>> watches = open.furaffinity.client.pages.msgOthers.processWatchNotifications(msgOthers.getWatches(), "");
            List<HashMap<String, String>> submissionComments = open.furaffinity.client.pages.msgOthers.processLineNotifications(msgOthers.getSubmissionComments(), "");
            List<HashMap<String, String>> journalComments = open.furaffinity.client.pages.msgOthers.processJournalLineNotifications(msgOthers.getJournalComments(), "");
            List<HashMap<String, String>> shouts = open.furaffinity.client.pages.msgOthers.processShoutNotifications(msgOthers.getShouts(), "");
            List<HashMap<String, String>> favorites = open.furaffinity.client.pages.msgOthers.processLineNotifications(msgOthers.getFavorites(), "");
            List<HashMap<String, String>> journals = open.furaffinity.client.pages.msgOthers.processLineNotifications(msgOthers.getJournals(), "");

            HashMap<String, Integer> newNotifications = new HashMap<>();

            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.settingsFile), Context.MODE_PRIVATE);

            if (watches.size() > 0 && sharedPref.getBoolean(context.getString(R.string.watchNotificationsEnabledSetting), settings.standardNotificationsDefault)) {
                newNotifications.put("watch" + ((watches.size() > 1) ? ("es") : ("")), watches.size());
            }

            if (submissionComments.size() > 0 && sharedPref.getBoolean(context.getString(R.string.submissionCommentNotificationsEnabledSetting), settings.standardNotificationsDefault)) {
                newNotifications.put("submission comment" + ((submissionComments.size() > 1) ? ("s") : ("")), submissionComments.size());
            }

            if (journalComments.size() > 0 && sharedPref.getBoolean(context.getString(R.string.journalCommentNotificationsEnabledSetting), settings.standardNotificationsDefault)) {
                newNotifications.put("journal comment" + ((journalComments.size() > 1) ? ("s") : ("")), journalComments.size());
            }

            if (shouts.size() > 0 && sharedPref.getBoolean(context.getString(R.string.shoutNotificationsEnabledSetting), settings.standardNotificationsDefault)) {
                newNotifications.put("shout" + ((shouts.size() > 1) ? ("s") : ("")), shouts.size());
            }

            if (favorites.size() > 0 && sharedPref.getBoolean(context.getString(R.string.favoriteNotificationsEnabledSetting), settings.standardNotificationsDefault)) {
                newNotifications.put("favorite" + ((favorites.size() > 1) ? ("s") : ("")), favorites.size());
            }

            if (journals.size() > 0 && sharedPref.getBoolean(context.getString(R.string.journalNotificationsEnabledSetting), settings.standardNotificationsDefault)) {
                newNotifications.put("journal" + ((journals.size() > 1) ? ("s") : ("")), journals.size());
            }

            if (msgPmsData.size() > 0 && sharedPref.getBoolean(context.getString(R.string.noteNotificationsEnabledSetting), settings.standardNotificationsDefault)) {
                newNotifications.put("note" + ((msgPmsData.size() > 1) ? ("s") : ("")), msgPmsData.size());
            }

            if (msgSubmissionData.size() > 0 && sharedPref.getBoolean(context.getString(R.string.submissionNotificationsEnabledSetting), settings.standardNotificationsDefault)) {
                newNotifications.put("submission" + ((msgSubmissionData.size() > 1) ? ("s") : ("")), msgSubmissionData.size());
            }

            String contentText = "";
            for (String key : newNotifications.keySet().stream().sorted().collect(Collectors.toList())) {
                if (contentText.length() > 0) {
                    contentText += "\n";
                }

                contentText += newNotifications.get(key) + " new " + key;
            }

            if (contentText.length() > 0) {
                Intent intent = new Intent(context, mainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(intent);
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, context.getString(R.string.app_name));
                mBuilder.setSmallIcon(R.drawable.ic_menu_notifications);
                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(contentText));
                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setAutoCancel(true);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelId = context.getString(R.string.app_name);
                    NotificationChannel channel = new NotificationChannel(channelId, context.getString(R.string.app_name) + "_notificationService_channel", NotificationManager.IMPORTANCE_DEFAULT);
                    mNotificationManager.createNotificationChannel(channel);
                    mBuilder.setChannelId(channelId);
                }

                mNotificationManager.notify(0, mBuilder.build());
            }
        }
        return Result.success();
    }
}
