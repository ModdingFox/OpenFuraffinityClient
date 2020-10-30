package open.furaffinity.client.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import open.furaffinity.client.R;
import open.furaffinity.client.workers.notificationWorker;
import open.furaffinity.client.workers.searchNotificationWorker;

public class onBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //When testing in adb shell use "am broadcast -a android.intent.action.ACTION_BOOT_COMPLETED open.furaffinity.client"
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.ACTION_BOOT_COMPLETED")) {
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.settingsFile), Context.MODE_PRIVATE);

            WorkManager.getInstance(context).cancelAllWork();

            if(sharedPref.getBoolean(context.getString(R.string.notificationsEnabledSetting), false)) {
                PeriodicWorkRequest workRequest = new androidx.work.PeriodicWorkRequest.Builder(notificationWorker.class, sharedPref.getInt(context.getString(R.string.notificationsIntervalSetting), 15), TimeUnit.MINUTES).build();
                WorkManager.getInstance(context).enqueueUniquePeriodicWork(context.getString(R.string.OFACNotification), ExistingPeriodicWorkPolicy.KEEP, workRequest);
            }

            if(sharedPref.getBoolean(context.getString(R.string.searchNotificationsEnabledSetting), false)) {
                PeriodicWorkRequest workRequest = new androidx.work.PeriodicWorkRequest.Builder(searchNotificationWorker.class, sharedPref.getInt(context.getString(R.string.searchNotificationsIntervalSetting), 15), TimeUnit.MINUTES).build();
                WorkManager.getInstance(context).enqueueUniquePeriodicWork(context.getString(R.string.OFACSearchNotification), ExistingPeriodicWorkPolicy.KEEP, workRequest);
            }
        }
    }

}
