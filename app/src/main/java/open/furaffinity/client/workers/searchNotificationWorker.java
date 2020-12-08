package open.furaffinity.client.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.pages.search;
import open.furaffinity.client.sqlite.searchContract;
import open.furaffinity.client.sqlite.searchDBHelper;

public class searchNotificationWorker extends Worker {
    private static final String TAG = searchNotificationWorker.class.getName();
    private static final int maxPagesToCheck = 3;
    private final Context context;
    private final List<HashMap<String, String>> mDataset = new ArrayList<>();
    private search page;

    public searchNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    private void initClientAndPage() {
        page = new search(context, new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {

            }

            @Override
            public void requestFailed(abstractPage abstractPage) {

            }
        });
    }

    private void fetchPageData() {
        try {
            page.execute().get();
            page = new search(page);

            searchDBHelper dbHelper = new searchDBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] projection = {
                    searchContract.searchItemEntry.COLUMN_NAME_NAME,
                    searchContract.searchItemEntry.COLUMN_NAME_MOSTRECENTITEM,
                    searchContract.searchItemEntry.COLUMN_NAME_Q,
                    searchContract.searchItemEntry.COLUMN_NAME_ORDERBY,
                    searchContract.searchItemEntry.COLUMN_NAME_ORDERDIRECTION,
                    searchContract.searchItemEntry.COLUMN_NAME_RANGE,
                    searchContract.searchItemEntry.COLUMN_NAME_RATINGGENERAL,
                    searchContract.searchItemEntry.COLUMN_NAME_RATINGMATURE,
                    searchContract.searchItemEntry.COLUMN_NAME_RATINGADULT,
                    searchContract.searchItemEntry.COLUMN_NAME_TYPEART,
                    searchContract.searchItemEntry.COLUMN_NAME_TYPEMUSIC,
                    searchContract.searchItemEntry.COLUMN_NAME_TYPEFLASH,
                    searchContract.searchItemEntry.COLUMN_NAME_TYPESTORY,
                    searchContract.searchItemEntry.COLUMN_NAME_TYPEPHOTO,
                    searchContract.searchItemEntry.COLUMN_NAME_TYPEPOETRY,
                    searchContract.searchItemEntry.COLUMN_NAME_MODE
            };

            String selection = searchContract.searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE + " = ?";

            String[] selectionArgs = {"1"};

            String sortOrder = "rowid DESC";

            Cursor cursor = db.query(
                    searchContract.searchItemEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            while (cursor.moveToNext()) {
                String COLUMN_NAME = cursor.getString(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_NAME));
                String COLUMN_MOSTRECENTITEM = cursor.getString(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_MOSTRECENTITEM));

                String COLUMN_Q = cursor.getString(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_Q));
                String COLUMN_ORDERBY = cursor.getString(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_ORDERBY));
                String COLUMN_ORDERDIRECTION = cursor.getString(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_ORDERDIRECTION));
                String COLUMN_RANGE = cursor.getString(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_RANGE));
                boolean COLUMN_RATINGGENERAL = (cursor.getInt(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_RATINGGENERAL)) > 0);
                boolean COLUMN_RATINGMATURE = (cursor.getInt(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_RATINGMATURE)) > 0);
                boolean COLUMN_RATINGADULT = (cursor.getInt(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_RATINGADULT)) > 0);
                boolean COLUMN_TYPEART = (cursor.getInt(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_TYPEART)) > 0);
                boolean COLUMN_TYPEMUSIC = (cursor.getInt(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_TYPEMUSIC)) > 0);
                boolean COLUMN_TYPEFLASH = (cursor.getInt(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_TYPEFLASH)) > 0);
                boolean COLUMN_TYPESTORY = (cursor.getInt(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_TYPESTORY)) > 0);
                boolean COLUMN_TYPEPHOTO = (cursor.getInt(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_TYPEPHOTO)) > 0);
                boolean COLUMN_TYPEPOETRY = (cursor.getInt(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_TYPEPOETRY)) > 0);
                String COLUMN_MODE = cursor.getString(cursor.getColumnIndexOrThrow(searchContract.searchItemEntry.COLUMN_NAME_MODE));

                page.setQuery(COLUMN_Q);
                page.setOrderBy(COLUMN_ORDERBY);
                page.setOrderDirection(COLUMN_ORDERDIRECTION);
                page.setRange(COLUMN_RANGE);
                page.setRatingGeneral(COLUMN_RATINGGENERAL);
                page.setRatingMature(COLUMN_RATINGMATURE);
                page.setRatingAdult(COLUMN_RATINGADULT);
                page.setTypeArt(COLUMN_TYPEART);
                page.setTypeMusic(COLUMN_TYPEMUSIC);
                page.setTypeFlash(COLUMN_TYPEFLASH);
                page.setTypeStory(COLUMN_TYPESTORY);
                page.setTypePhoto(COLUMN_TYPEPHOTO);
                page.setTypePoetry(COLUMN_TYPEPOETRY);
                page.setMode(COLUMN_MODE);
                page.setPage("1");

                boolean foundLastPosition = false;
                int pageCount = 0;
                int postCount = 0;

                do {
                    page.execute().get();

                    List<HashMap<String, String>> currentPageResults = page.getPageResults();

                    if (currentPageResults == null || currentPageResults.size() == 0) {
                        foundLastPosition = true;
                    } else {
                        for (HashMap<String, String> currentResult : currentPageResults) {
                            if (currentResult.get("postPath").equals(COLUMN_MOSTRECENTITEM)) {
                                foundLastPosition = true;
                                break;
                            } else {
                                postCount++;
                            }
                        }
                        page.setPage(Integer.toString(Integer.parseInt(page.getCurrentPage()) + 1));
                        Thread.sleep(1000);
                    }
                    page = new search(page);
                    pageCount++;
                    if (pageCount > maxPagesToCheck) {
                        foundLastPosition = true;
                    }
                } while (!foundLastPosition);

                if (postCount > 0) {
                    HashMap<String, String> newItem = new HashMap<>();
                    newItem.put("name", COLUMN_NAME);
                    newItem.put("newPosts", Integer.toString(postCount));

                    if (pageCount > maxPagesToCheck) {
                        newItem.put("newPosts", newItem.get("newPosts") + "+");
                    }

                    mDataset.add(newItem);
                }
            }

            cursor.close();
            db.close();

        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }
    }

    @Override
    public Result doWork() {
        initClientAndPage();
        fetchPageData();

        String contentText = "";
        for (HashMap<String, String> currentElement : mDataset) {
            if (contentText.length() > 0) {
                contentText += "\n";
            }
            contentText += currentElement.get("name") + " has " + currentElement.get("newPosts") + " new results";
        }

        if (contentText.length() > 0) {
            Intent intent = new Intent(context, mainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, context.getString(R.string.app_name));
            mBuilder.setSmallIcon(R.drawable.ic_menu_search);
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

            mNotificationManager.notify(1, mBuilder.build());
        }

        return Result.success();
    }
}