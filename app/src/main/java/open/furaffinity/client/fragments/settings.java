package open.furaffinity.client.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.sqlite.historyContract;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.workers.notificationWorker;
import open.furaffinity.client.workers.searchNotificationWorker;

public class settings extends Fragment {
    private Switch notificationsSwitch;
    private EditText notificationsInterval;
    private Switch searchNotificationsSwitch;
    private EditText searchNotificationsInterval;
    private EditText recyclerVisibleThresholdCount;
    private EditText imageListColumnsCount;
    private Switch imageListOrientation;
    private Switch enablePostLabels;
    private Switch saveBrowseStateSwitch;
    private Switch saveSearchStateSwitch;
    private Switch trackHistory;
    private Button clearHistory;

    public static boolean notificationsEnabledDefault = false;
    public static int notificationsIntervalDefault = 15;
    public static boolean searchNotificationsEnabledDefault = false;
    public static int searchNotificationsIntervalDefault = 15;
    public static int recyclerVisibleThresholdDefault = 16;
    public static int imageListColumnsDefault = 1;
    public static boolean imageListInfoDefault = true;
    public static int imageListOrientationDefault = StaggeredGridLayoutManager.VERTICAL;//Likely not gunna expose this as some views break with it as HORIZONTAL though the option would be nice
    public static boolean trackHistoryDefault = false;
    public static boolean saveBrowseStateDefault = true;
    public static boolean saveSearchStateDefault = true;

    private void getElements(View rootView) {
        notificationsSwitch = rootView.findViewById(R.id.notificationsSwitch);
        notificationsInterval = rootView.findViewById(R.id.notificationsInterval);
        searchNotificationsSwitch = rootView.findViewById(R.id.searchNotificationsSwitch);
        searchNotificationsInterval = rootView.findViewById(R.id.searchNotificationsInterval);
        recyclerVisibleThresholdCount = rootView.findViewById(R.id.recyclerVisibleThresholdCount);
        imageListColumnsCount = rootView.findViewById(R.id.imageListColumnsCount);
        imageListOrientation = rootView.findViewById(R.id.imageListOrientation);
        enablePostLabels = rootView.findViewById(R.id.enablePostLabels);
        saveBrowseStateSwitch = rootView.findViewById(R.id.saveBrowseStateSwitch);
        saveSearchStateSwitch = rootView.findViewById(R.id.saveSearchStateSwitch);
        trackHistory = rootView.findViewById(R.id.trackHistory);
        clearHistory = rootView.findViewById(R.id.clearHistory);
    }

    private void updateUIElements() {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        notificationsSwitch.setChecked(sharedPref.getBoolean(context.getString(R.string.notificationsEnabledSetting), notificationsEnabledDefault));
        notificationsInterval.setText(Integer.toString(sharedPref.getInt(context.getString(R.string.notificationsIntervalSetting), notificationsIntervalDefault)));
        searchNotificationsSwitch.setChecked(sharedPref.getBoolean(context.getString(R.string.searchNotificationsEnabledSetting), searchNotificationsEnabledDefault));
        searchNotificationsInterval.setText(Integer.toString(sharedPref.getInt(context.getString(R.string.searchNotificationsIntervalSetting), searchNotificationsIntervalDefault)));
        recyclerVisibleThresholdCount.setText(Integer.toString(sharedPref.getInt(context.getString(R.string.recyclerVisibleThreshold), recyclerVisibleThresholdDefault)));
        imageListColumnsCount.setText(Integer.toString(sharedPref.getInt(context.getString(R.string.imageListColumns), imageListColumnsDefault)));
        imageListOrientation.setChecked(sharedPref.getInt(context.getString(R.string.imageListOrientation), imageListOrientationDefault) == StaggeredGridLayoutManager.VERTICAL);
        enablePostLabels.setChecked(sharedPref.getBoolean(context.getString(R.string.imageListInfo), imageListInfoDefault));
        saveBrowseStateSwitch.setChecked(sharedPref.getBoolean(context.getString(R.string.saveBrowseState), saveBrowseStateDefault));
        saveSearchStateSwitch.setChecked(sharedPref.getBoolean(context.getString(R.string.saveSearchState), saveSearchStateDefault));
        trackHistory.setChecked(sharedPref.getBoolean(context.getString(R.string.trackHistorySetting), trackHistoryDefault));
    }

    private void updateUIElementListeners(View rootView) {
        notificationsSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getActivity();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(context.getString(R.string.notificationsEnabledSetting), isChecked);
                editor.apply();
                editor.commit();

                WorkManager.getInstance(context).cancelUniqueWork(context.getString(R.string.OFACNotification));

                if (isChecked) {
                    PeriodicWorkRequest workRequest = new androidx.work.PeriodicWorkRequest.Builder(notificationWorker.class, sharedPref.getInt(context.getString(R.string.notificationsIntervalSetting), notificationsIntervalDefault), TimeUnit.MINUTES).build();
                    WorkManager.getInstance(context).enqueueUniquePeriodicWork(context.getString(R.string.OFACNotification), ExistingPeriodicWorkPolicy.KEEP, workRequest);
                }
            }
        });

        notificationsInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(notificationsInterval.getText().toString());

                    if (value > 0) {
                        Context context = getActivity();
                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(context.getString(R.string.notificationsIntervalSetting), value);
                        editor.apply();
                        editor.commit();

                        WorkManager.getInstance(context).cancelUniqueWork(context.getString(R.string.OFACNotification));

                        if (sharedPref.getBoolean(context.getString(R.string.notificationsEnabledSetting), notificationsEnabledDefault)) {
                            PeriodicWorkRequest workRequest = new androidx.work.PeriodicWorkRequest.Builder(notificationWorker.class, sharedPref.getInt(context.getString(R.string.notificationsIntervalSetting), notificationsIntervalDefault), TimeUnit.MINUTES).build();
                            WorkManager.getInstance(context).enqueueUniquePeriodicWork(context.getString(R.string.OFACNotification), ExistingPeriodicWorkPolicy.KEEP, workRequest);
                        }
                    }
                } catch (NumberFormatException e) {

                }
            }
        });

        searchNotificationsSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getActivity();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(context.getString(R.string.searchNotificationsEnabledSetting), isChecked);
                editor.apply();
                editor.commit();

                WorkManager.getInstance(context).cancelUniqueWork(context.getString(R.string.OFACSearchNotification));

                if (isChecked) {
                    PeriodicWorkRequest workRequest = new androidx.work.PeriodicWorkRequest.Builder(searchNotificationWorker.class, sharedPref.getInt(context.getString(R.string.searchNotificationsIntervalSetting), searchNotificationsIntervalDefault), TimeUnit.MINUTES).build();
                    WorkManager.getInstance(context).enqueueUniquePeriodicWork(context.getString(R.string.OFACSearchNotification), ExistingPeriodicWorkPolicy.KEEP, workRequest);
                }
            }
        });

        searchNotificationsInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(searchNotificationsInterval.getText().toString());

                    if (value > 0) {
                        Context context = getActivity();
                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(context.getString(R.string.searchNotificationsIntervalSetting), value);
                        editor.apply();
                        editor.commit();

                        WorkManager.getInstance(context).cancelUniqueWork(context.getString(R.string.OFACSearchNotification));

                        if (sharedPref.getBoolean(context.getString(R.string.searchNotificationsEnabledSetting), searchNotificationsEnabledDefault)) {
                            PeriodicWorkRequest workRequest = new androidx.work.PeriodicWorkRequest.Builder(searchNotificationWorker.class, sharedPref.getInt(context.getString(R.string.searchNotificationsIntervalSetting), searchNotificationsIntervalDefault), TimeUnit.MINUTES).build();
                            WorkManager.getInstance(context).enqueueUniquePeriodicWork(context.getString(R.string.OFACSearchNotification), ExistingPeriodicWorkPolicy.KEEP, workRequest);
                        }
                    }
                } catch (NumberFormatException e) {

                }
            }
        });

        recyclerVisibleThresholdCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(recyclerVisibleThresholdCount.getText().toString());

                    if (value > 0) {
                        Context context = getActivity();
                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(context.getString(R.string.recyclerVisibleThreshold), value);
                        editor.apply();
                        editor.commit();
                    }
                } catch (NumberFormatException e) {

                }
            }
        });

        imageListColumnsCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(imageListColumnsCount.getText().toString());

                    if (value > 0) {
                        Context context = getActivity();
                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(context.getString(R.string.imageListColumns), value);
                        editor.apply();
                        editor.commit();
                    }
                } catch (NumberFormatException e) {

                }
            }
        });

        imageListOrientation.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getActivity();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(context.getString(R.string.imageListOrientation), ((isChecked) ? (StaggeredGridLayoutManager.VERTICAL) : (StaggeredGridLayoutManager.HORIZONTAL)));
                editor.apply();
                editor.commit();
            }
        });

        enablePostLabels.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getActivity();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(context.getString(R.string.imageListInfo), isChecked);
                editor.apply();
                editor.commit();
            }
        });

        saveBrowseStateSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getActivity();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(context.getString(R.string.saveBrowseState), isChecked);
                editor.apply();
                editor.commit();
            }
        });

        saveSearchStateSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getActivity();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(context.getString(R.string.saveSearchState), isChecked);
                editor.apply();
                editor.commit();
            }
        });

        trackHistory.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getActivity();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(context.getString(R.string.trackHistorySetting), isChecked);
                editor.apply();
                editor.commit();

                ((mainActivity) getActivity()).updateUIElements();
            }
        });

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDBHelper dbHelper = new historyDBHelper(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                //Delete previous versions from history
                db.delete(historyContract.historyItemEntry.TABLE_NAME_JOURNAL, null, null);
                db.delete(historyContract.historyItemEntry.TABLE_NAME_USER, null, null);
                db.delete(historyContract.historyItemEntry.TABLE_NAME_VIEW, null, null);

                db.close();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        getElements(rootView);
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
