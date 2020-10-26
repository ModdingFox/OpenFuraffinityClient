package open.furaffinity.client.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import open.furaffinity.client.workers.notificationWorker;
import open.furaffinity.client.workers.searchNotificationWorker;

public class onBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //When testing in adb shell use "am broadcast -a android.intent.action.ACTION_BOOT_COMPLETED open.furaffinity.client"
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") /* || intent.getAction().equals("android.intent.action.ACTION_BOOT_COMPLETED")*/) {
            PeriodicWorkRequest workRequest = new androidx.work.PeriodicWorkRequest.Builder(notificationWorker.class, 15, TimeUnit.MINUTES).build();
            WorkManager.getInstance(context).enqueue(workRequest);

            workRequest = new androidx.work.PeriodicWorkRequest.Builder(searchNotificationWorker.class, 15, TimeUnit.MINUTES).build();
            WorkManager.getInstance(context).enqueue(workRequest);
        }
    }

}
