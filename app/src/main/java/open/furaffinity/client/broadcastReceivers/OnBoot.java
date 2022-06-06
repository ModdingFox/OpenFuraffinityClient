package open.furaffinity.client.broadcastReceivers;

import java.util.concurrent.TimeUnit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import open.furaffinity.client.R;
import open.furaffinity.client.fragmentDrawers.Settings;
import open.furaffinity.client.workers.NotificationWorker;
import open.furaffinity.client.workers.SearchNotificationWorker;

public class OnBoot extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {

        // When testing in adb shell use "am broadcast -a android.intent.action
        // .ACTION_BOOT_COMPLETED open.furaffinity.client"
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
            || intent.getAction().equals("android.intent.action.ACTION_BOOT_COMPLETED")) {
            final SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);

            WorkManager.getInstance(context).cancelAllWork();

            if (sharedPref.getBoolean(context.getString(R.string.notificationsEnabledSetting),
                Settings.notificationsEnabledDefault)) {
                final PeriodicWorkRequest workRequest =
                    new androidx.work.PeriodicWorkRequest.Builder(NotificationWorker.class,
                        sharedPref.getInt(context.getString(R.string.notificationsIntervalSetting),
                            Settings.notificationsIntervalDefault), TimeUnit.MINUTES).build();
                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(context.getString(R.string.OFACNotification),
                        ExistingPeriodicWorkPolicy.KEEP, workRequest);
            }

            if (sharedPref.getBoolean(context.getString(R.string.searchNotificationsEnabledSetting),
                Settings.searchNotificationsEnabledDefault)) {
                final PeriodicWorkRequest workRequest =
                    new androidx.work.PeriodicWorkRequest.Builder(SearchNotificationWorker.class,
                        sharedPref.getInt(
                            context.getString(R.string.searchNotificationsIntervalSetting),
                            Settings.searchNotificationsIntervalDefault), TimeUnit.MINUTES).build();
                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(context.getString(R.string.OFACSearchNotification),
                        ExistingPeriodicWorkPolicy.KEEP, workRequest);
            }
        }
    }
}
