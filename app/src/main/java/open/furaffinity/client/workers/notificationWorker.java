package open.furaffinity.client.workers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
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
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.pages.msgPms;
import open.furaffinity.client.utilities.webClient;

public class notificationWorker extends Worker {
    private static final String TAG = notificationWorker.class.getName();

    private Context context;

    private open.furaffinity.client.utilities.webClient webClient;

    private open.furaffinity.client.pages.msgOthers msgOthers;
    private open.furaffinity.client.pages.msgPms msgPms;
    private open.furaffinity.client.pages.msgSubmission msgSubmission;

    private List<HashMap<String, String>> msgPmsData = new ArrayList<>();
    private List<HashMap<String, String>> msgSubmissionData = new ArrayList<>();

    private void initClientAndPage() {
        webClient = new webClient(context);
        msgOthers = new open.furaffinity.client.pages.msgOthers();
        msgPms = new open.furaffinity.client.pages.msgPms();
        msgPms.setSelectedFolder(open.furaffinity.client.pages.msgPms.mailFolders.unread);
        msgSubmission = new open.furaffinity.client.pages.msgSubmission(true);
    }

    private void fetchPageData() {
        try {
            msgOthers.execute(webClient).get();

            do {
                msgPms = new open.furaffinity.client.pages.msgPms(msgPms);
                msgPms.execute(webClient).get();
                msgPms.setPage(msgPms.getPage() + 1);

                if(msgPms.getMessages() != null) {
                    msgPms.getMessages().removeAll(msgPmsData);
                    msgPmsData.addAll(msgPms.getMessages());
                }
            } while (msgPms.getMessages() != null && msgPms.getMessages().size() > 0);

            do {
                msgSubmission = new open.furaffinity.client.pages.msgSubmission(msgSubmission);
                msgSubmission.execute(webClient).get();
                msgSubmission.setNextPage();

                if(msgSubmission.getPageResults() != null) {
                    msgSubmission.getPageResults().removeAll(msgSubmissionData);
                    msgSubmissionData.addAll(msgSubmission.getPageResults());
                }
            } while (msgPms.getMessages() != null && msgPms.getMessages().size() > 0);

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

        List<HashMap<String, String>> watches = open.furaffinity.client.pages.msgOthers.processWatchNotifications(msgOthers.getWatches(), "");
        List<HashMap<String, String>> comments = open.furaffinity.client.utilities.html.commentsToListHash(msgOthers.getSubmissionComments());
        List<HashMap<String, String>> shouts = open.furaffinity.client.pages.msgOthers.processShoutNotifications(msgOthers.getShouts(), "");
        List<HashMap<String, String>> favorites = open.furaffinity.client.pages.msgOthers.processLineNotifications(msgOthers.getFavorites(), "");
        List<HashMap<String, String>> journals = open.furaffinity.client.pages.msgOthers.processLineNotifications(msgOthers.getJournals(), "");

        HashMap<String, Integer> newNotifications = new HashMap<>();

        if(watches.size() > 0) {
            newNotifications.put("watch" + ((watches.size() > 1)?("es"):("")), watches.size());
        }

        if(comments.size() > 0) {
            newNotifications.put("comment" + ((comments.size() > 1)?("s"):("")), comments.size());
        }

        if(shouts.size() > 0) {
            newNotifications.put("shout" + ((shouts.size() > 1)?("s"):("")), shouts.size());
        }

        if(favorites.size() > 0) {
            newNotifications.put("favorite" + ((favorites.size() > 1)?("s"):("")), favorites.size());
        }

        if(journals.size() > 0) {
            newNotifications.put("journal" + ((journals.size() > 1)?("s"):("")), journals.size());
        }

        if(msgPmsData.size() > 0) {
            newNotifications.put("note" + ((msgPmsData.size() > 1)?("s"):("")), msgPmsData.size());
        }

        if(msgSubmissionData.size() > 0) {
            newNotifications.put("submission" + ((msgSubmissionData.size() > 1)?("s"):("")), msgSubmissionData.size());
        }

        String contentText = "";
        for (String key : newNotifications.keySet().stream().sorted().collect(Collectors.toList())) {
            if(contentText.length() > 0) {
                contentText += "\n";
            }

            contentText += newNotifications.get(key) + " new " + key;
        }

        if(contentText.length() > 0) {
            Intent intent = new Intent(context, mainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, context.getString(R.string.app_name));
            mBuilder.setSmallIcon(R.drawable.ic_menu_gallery);
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

        return Result.success();
    }
}
