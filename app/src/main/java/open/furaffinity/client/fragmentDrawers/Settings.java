package open.furaffinity.client.fragmentDrawers;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.dialogs.ConfirmDialog;
import open.furaffinity.client.sqlite.BackContract;
import open.furaffinity.client.sqlite.BackDBHelper;
import open.furaffinity.client.sqlite.HistoryContract;
import open.furaffinity.client.sqlite.HistoryDBHelper;
import open.furaffinity.client.utilities.ImageResultsTool;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;
import open.furaffinity.client.workers.NotificationWorker;
import open.furaffinity.client.workers.SearchNotificationWorker;

public class Settings extends AbstractAppFragment {
    public static boolean advertisementsEnabledDefault = true;
    public static boolean notificationsEnabledDefault = false;
    public static int notificationsIntervalDefault = 15;
    public static boolean standardNotificationsDefault = true;
    public static boolean searchNotificationsEnabledDefault = false;
    public static int searchNotificationsIntervalDefault = 15;
    public static int imageResolutionDefault =
        ImageResultsTool.imageResolutions.Original.getValue();
    public static int recyclerVisibleThresholdDefault = 16;
    public static int imageListColumnsDefault = 1;
    public static boolean imageListInfoDefault = true;
    public static int imageListOrientationDefault = StaggeredGridLayoutManager.VERTICAL;
        // Likely not gunna expose this as some views break with it as HORIZONTAL though the
        // option would be nice
    public static boolean trackHistoryDefault = false;
    public static boolean trackBackHistoryDefault = true;
    public static boolean saveBrowseStateDefault = true;
    // Tied to saveBrowseStateDefault
    public static boolean cachedBrowseDefault = true;
    public static int InvalidateCachedBrowseTimeDefault = 5;
    public static int InvalidateCachedBrowseAfterDefault = 12;
    public static boolean saveSearchStateDefault = true;
    // Tied to saveSearchStateDefault
    public static boolean cachedSearchDefault = true;
    public static int InvalidateCachedSearchTimeDefault = 5;
    public static int InvalidateCachedSearchAfterDefault = 12;
    private Switch advertisementsSwitch;
    private Switch notificationsSwitch;
    private EditText notificationsInterval;
    private Switch watchNotificationsSwitch;
    private Switch submissionCommentNotificationsSwitch;
    private Switch journalCommentNotificationsSwitch;
    private Switch shoutNotificationsSwitch;
    private Switch favoriteNotificationsSwitch;
    private Switch journalNotificationsSwitch;
    private Switch noteNotificationsSwitch;
    private Switch submissionNotificationsSwitch;
    private Switch searchNotificationsSwitch;
    private EditText searchNotificationsInterval;
    private Spinner imageResolution;
    private EditText recyclerVisibleThresholdCount;
    private EditText imageListColumnsCount;
    private Switch imageListOrientation;
    private Switch enablePostLabels;
    private Switch saveBrowseStateSwitch;
    private Switch cachedBrowseStateSwitch;
    private EditText InvalidateCachedBrowseTimeEditText;
    private EditText InvalidateCachedBrowseAfterEditText;
    private Switch saveSearchStateSwitch;
    private Switch cachedSearchStateSwitch;
    private EditText InvalidateCachedSearchTimeEditText;
    private EditText InvalidateCachedSearchAfterEditText;
    private Switch trackHistory;
    private Button clearHistory;
    private Switch trackBackHistory;
    private Button clearBackHistory;

    @Override protected int getLayout() {
        return R.layout.fragment_settings;
    }

    protected void getElements(View rootView) {
        advertisementsSwitch = rootView.findViewById(R.id.advertisementsSwitch);
        notificationsSwitch = rootView.findViewById(R.id.notificationsSwitch);
        notificationsInterval = rootView.findViewById(R.id.notificationsInterval);
        watchNotificationsSwitch = rootView.findViewById(R.id.watchNotificationsSwitch);
        submissionCommentNotificationsSwitch =
            rootView.findViewById(R.id.submissionCommentNotificationsSwitch);
        journalCommentNotificationsSwitch =
            rootView.findViewById(R.id.journalCommentNotificationsSwitch);
        shoutNotificationsSwitch = rootView.findViewById(R.id.shoutNotificationsSwitch);
        favoriteNotificationsSwitch = rootView.findViewById(R.id.favoriteNotificationsSwitch);
        journalNotificationsSwitch = rootView.findViewById(R.id.journalNotificationsSwitch);
        noteNotificationsSwitch = rootView.findViewById(R.id.noteNotificationsSwitch);
        submissionNotificationsSwitch = rootView.findViewById(R.id.submissionNotificationsSwitch);
        searchNotificationsSwitch = rootView.findViewById(R.id.searchNotificationsSwitch);
        searchNotificationsInterval = rootView.findViewById(R.id.searchNotificationsInterval);
        imageResolution = rootView.findViewById(R.id.imageResolution);
        recyclerVisibleThresholdCount = rootView.findViewById(R.id.recyclerVisibleThresholdCount);
        imageListColumnsCount = rootView.findViewById(R.id.imageListColumnsCount);
        imageListOrientation = rootView.findViewById(R.id.imageListOrientation);
        enablePostLabels = rootView.findViewById(R.id.enablePostLabels);
        saveBrowseStateSwitch = rootView.findViewById(R.id.saveBrowseStateSwitch);
        cachedBrowseStateSwitch = rootView.findViewById(R.id.cachedBrowseStateSwitch);
        InvalidateCachedBrowseTimeEditText =
            rootView.findViewById(R.id.InvalidateCachedBrowseTimeEditText);
        InvalidateCachedBrowseAfterEditText =
            rootView.findViewById(R.id.InvalidateCachedBrowseAfterEditText);
        saveSearchStateSwitch = rootView.findViewById(R.id.saveSearchStateSwitch);
        cachedSearchStateSwitch = rootView.findViewById(R.id.cachedSearchStateSwitch);
        InvalidateCachedSearchTimeEditText =
            rootView.findViewById(R.id.InvalidateCachedSearchTimeEditText);
        InvalidateCachedSearchAfterEditText =
            rootView.findViewById(R.id.InvalidateCachedSearchAfterEditText);
        trackHistory = rootView.findViewById(R.id.trackHistory);
        clearHistory = rootView.findViewById(R.id.clearHistory);
        trackBackHistory = rootView.findViewById(R.id.trackBackHistory);
        clearBackHistory = rootView.findViewById(R.id.clearBackHistory);
    }

    @Override protected void initPages() {
        ((MainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(), "");
    }

    @Override protected void fetchPageData() {

    }

    protected void updateUiElements() {
        final Context context = requireActivity();
        final SharedPreferences sharedPref =
            context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        advertisementsSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.advertisementsEnabledSetting),
                advertisementsEnabledDefault));
        notificationsSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.notificationsEnabledSetting),
                notificationsEnabledDefault));
        notificationsInterval.setText(Integer.toString(
            sharedPref.getInt(context.getString(R.string.notificationsIntervalSetting),
                notificationsIntervalDefault)));
        watchNotificationsSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.watchNotificationsEnabledSetting),
                standardNotificationsDefault));
        submissionCommentNotificationsSwitch.setChecked(sharedPref.getBoolean(
            context.getString(R.string.submissionCommentNotificationsEnabledSetting),
            standardNotificationsDefault));
        journalCommentNotificationsSwitch.setChecked(sharedPref.getBoolean(
            context.getString(R.string.journalCommentNotificationsEnabledSetting),
            standardNotificationsDefault));
        shoutNotificationsSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.shoutNotificationsEnabledSetting),
                standardNotificationsDefault));
        favoriteNotificationsSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.favoriteNotificationsEnabledSetting),
                standardNotificationsDefault));
        journalNotificationsSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.journalNotificationsEnabledSetting),
                standardNotificationsDefault));
        noteNotificationsSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.noteNotificationsEnabledSetting),
                standardNotificationsDefault));
        submissionNotificationsSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.submissionNotificationsEnabledSetting),
                standardNotificationsDefault));
        searchNotificationsSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.searchNotificationsEnabledSetting),
                searchNotificationsEnabledDefault));
        searchNotificationsInterval.setText(Integer.toString(
            sharedPref.getInt(context.getString(R.string.searchNotificationsIntervalSetting),
                searchNotificationsIntervalDefault)));

        HashMap<String, String> imageResolutionOptions = new HashMap<String, String>();
        for (ImageResultsTool.imageResolutions currentImageResolution
            : ImageResultsTool.imageResolutions.values()) {
            imageResolutionOptions.put(currentImageResolution.toString(),
                currentImageResolution.getPrintableName());
        }

        UiControls.spinnerSetAdapter(requireContext(), imageResolution, imageResolutionOptions,
            Integer.toString(sharedPref.getInt(context.getString(R.string.imageResolutionSetting),
                Settings.imageResolutionDefault)), true, true);

        recyclerVisibleThresholdCount.setText(Integer.toString(
            sharedPref.getInt(context.getString(R.string.recyclerVisibleThreshold),
                recyclerVisibleThresholdDefault)));
        imageListColumnsCount.setText(Integer.toString(
            sharedPref.getInt(context.getString(R.string.imageListColumns),
                imageListColumnsDefault)));
        imageListOrientation.setChecked(
            sharedPref.getInt(context.getString(R.string.imageListOrientation),
                imageListOrientationDefault) == StaggeredGridLayoutManager.VERTICAL);
        enablePostLabels.setChecked(
            sharedPref.getBoolean(context.getString(R.string.imageListInfo), imageListInfoDefault));
        saveBrowseStateSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.saveBrowseState),
                saveBrowseStateDefault));
        cachedBrowseStateSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.cachedBrowseStateSetting),
                cachedBrowseDefault));
        InvalidateCachedBrowseTimeEditText.setText(Integer.toString(
            sharedPref.getInt(context.getString(R.string.InvalidateCachedBrowseTimeSetting),
                InvalidateCachedBrowseTimeDefault)));
        InvalidateCachedBrowseAfterEditText.setText(Integer.toString(
            sharedPref.getInt(context.getString(R.string.InvalidateCachedBrowseAfterSetting),
                InvalidateCachedBrowseAfterDefault)));
        saveSearchStateSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.saveSearchState),
                saveSearchStateDefault));
        cachedSearchStateSwitch.setChecked(
            sharedPref.getBoolean(context.getString(R.string.cachedSearchStateSetting),
                cachedSearchDefault));
        InvalidateCachedSearchTimeEditText.setText(Integer.toString(
            sharedPref.getInt(context.getString(R.string.InvalidateCachedSearchTimeSetting),
                InvalidateCachedSearchTimeDefault)));
        InvalidateCachedSearchAfterEditText.setText(Integer.toString(
            sharedPref.getInt(context.getString(R.string.InvalidateCachedSearchAfterSetting),
                InvalidateCachedSearchAfterDefault)));
        trackHistory.setChecked(
            sharedPref.getBoolean(context.getString(R.string.trackHistorySetting),
                trackHistoryDefault));
        trackBackHistory.setChecked(
            sharedPref.getBoolean(context.getString(R.string.trackBackHistorySetting),
                trackBackHistoryDefault));
    }

    protected void updateUiElementListeners(View rootView) {
        advertisementsSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Context context = requireActivity();
                final SharedPreferences sharedPref =
                    context.getSharedPreferences(getString(R.string.settingsFile),
                        Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPref.edit();

                if (!isChecked) {
                    final ConfirmDialog confirmDialog = new ConfirmDialog();
                    final String confirmDialogText = "Heewwwoooo just a quick thing.\n\n"
                        + "Although this app does not have server costs and is not distributed "
                        + "for-profit FurAffinity does have server costs and the advertisements "
                        + "they serve help pay for them to keep being awesome.\n"
                        + "As this app is opensource its pointless for it to force advertisements "
                        + "on anyone as someone could just pull the code and remove them anyways.\n"
                        + "So before you turn advertisements I ask you to reconsider the decision"
                        + ".\n"
                        + "Accept to turn advertisements off. Cancel to leave them on";
                    confirmDialog.setTitleText(confirmDialogText);
                    confirmDialog.setListener(new ConfirmDialog.DialogListener() {
                        @Override public void onDialogPositiveClick(DialogFragment dialog) {
                            editor.putBoolean(
                                context.getString(R.string.advertisementsEnabledSetting), false);
                            editor.apply();
                        }

                        @Override public void onDialogNegativeClick(DialogFragment dialog) {
                            advertisementsSwitch.setChecked(true);
                        }
                    });
                    confirmDialog.show(getChildFragmentManager(),
                        "getAdvertisementsDisableConfirm");
                }
                else {
                    editor.putBoolean(context.getString(R.string.advertisementsEnabledSetting),
                        isChecked);
                    editor.apply();
                }
            }
        });

        notificationsSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Context context = requireActivity();
                final SharedPreferences sharedPref =
                    context.getSharedPreferences(getString(R.string.settingsFile),
                        Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(context.getString(R.string.notificationsEnabledSetting),
                    isChecked);
                editor.apply();

                WorkManager.getInstance(context)
                    .cancelUniqueWork(context.getString(R.string.OFACNotification));

                if (isChecked) {
                    final PeriodicWorkRequest workRequest =
                        new androidx.work.PeriodicWorkRequest.Builder(NotificationWorker.class,
                            sharedPref.getInt(
                                context.getString(R.string.notificationsIntervalSetting),
                                notificationsIntervalDefault), TimeUnit.MINUTES).build();
                    WorkManager.getInstance(context)
                        .enqueueUniquePeriodicWork(context.getString(R.string.OFACNotification),
                            ExistingPeriodicWorkPolicy.KEEP, workRequest);
                }
            }
        });

        notificationsInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                try {
                    final int value = Integer.parseInt(notificationsInterval.getText().toString());

                    if (value > 0) {
                        final Context context = requireActivity();
                        final SharedPreferences sharedPref =
                            context.getSharedPreferences(getString(R.string.settingsFile),
                                Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(context.getString(R.string.notificationsIntervalSetting),
                            value);
                        editor.apply();

                        WorkManager.getInstance(context)
                            .cancelUniqueWork(context.getString(R.string.OFACNotification));

                        if (sharedPref.getBoolean(
                            context.getString(R.string.notificationsEnabledSetting),
                            notificationsEnabledDefault)) {
                            final PeriodicWorkRequest workRequest =
                                new androidx.work.PeriodicWorkRequest.Builder(
                                    NotificationWorker.class, sharedPref.getInt(
                                    context.getString(R.string.notificationsIntervalSetting),
                                    notificationsIntervalDefault), TimeUnit.MINUTES).build();
                            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                                context.getString(R.string.OFACNotification),
                                ExistingPeriodicWorkPolicy.KEEP, workRequest);
                        }
                    }
                }
                catch (NumberFormatException ignored) {}
            }
        });

        watchNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.watchNotificationsEnabledSetting),
                isChecked);
            editor.apply();
        });

        submissionCommentNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(
                context.getString(R.string.submissionCommentNotificationsEnabledSetting),
                isChecked);
            editor.apply();
        });

        journalCommentNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.journalCommentNotificationsEnabledSetting),
                isChecked);
            editor.apply();
        });

        shoutNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.shoutNotificationsEnabledSetting),
                isChecked);
            editor.apply();
        });

        favoriteNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.favoriteNotificationsEnabledSetting),
                isChecked);
            editor.apply();
        });

        journalNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.journalNotificationsEnabledSetting),
                isChecked);
            editor.apply();
        });

        noteNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.noteNotificationsEnabledSetting),
                isChecked);
            editor.apply();
        });

        submissionNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.submissionNotificationsEnabledSetting),
                isChecked);
            editor.apply();
        });

        searchNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.searchNotificationsEnabledSetting),
                isChecked);
            editor.apply();

            WorkManager.getInstance(context)
                .cancelUniqueWork(context.getString(R.string.OFACSearchNotification));

            if (isChecked) {
                final PeriodicWorkRequest workRequest =
                    new PeriodicWorkRequest.Builder(SearchNotificationWorker.class,
                        sharedPref.getInt(
                            context.getString(R.string.searchNotificationsIntervalSetting),
                            searchNotificationsIntervalDefault), TimeUnit.MINUTES).build();
                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(context.getString(R.string.OFACSearchNotification),
                        ExistingPeriodicWorkPolicy.KEEP, workRequest);
            }
        });

        searchNotificationsInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                try {
                    final int value = Integer.parseInt(searchNotificationsInterval.getText().toString());

                    if (value > 0) {
                        final Context context = requireActivity();
                        final SharedPreferences sharedPref =
                            context.getSharedPreferences(getString(R.string.settingsFile),
                                Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(
                            context.getString(R.string.searchNotificationsIntervalSetting), value);
                        editor.apply();

                        WorkManager.getInstance(context)
                            .cancelUniqueWork(context.getString(R.string.OFACSearchNotification));

                        if (sharedPref.getBoolean(
                            context.getString(R.string.searchNotificationsEnabledSetting),
                            searchNotificationsEnabledDefault)) {
                            final PeriodicWorkRequest workRequest =
                                new androidx.work.PeriodicWorkRequest.Builder(
                                    SearchNotificationWorker.class, sharedPref.getInt(
                                    context.getString(R.string.searchNotificationsIntervalSetting),
                                    searchNotificationsIntervalDefault), TimeUnit.MINUTES).build();
                            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                                context.getString(R.string.OFACSearchNotification),
                                ExistingPeriodicWorkPolicy.KEEP, workRequest);
                        }
                    }
                }
                catch (NumberFormatException ignored) {}
            }
        });

        imageResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int selectedValue =
                    Integer.parseInt(((KvPair) imageResolution.getSelectedItem()).getKey());
                final Context context = requireActivity();
                final SharedPreferences sharedPref =
                    context.getSharedPreferences(getString(R.string.settingsFile),
                        Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(context.getString(R.string.imageResolutionSetting), selectedValue);
                editor.apply();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerVisibleThresholdCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                try {
                    final int value =
                        Integer.parseInt(recyclerVisibleThresholdCount.getText().toString());

                    if (value > 0) {
                        final Context context = requireActivity();
                        final SharedPreferences sharedPref =
                            context.getSharedPreferences(getString(R.string.settingsFile),
                                Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(context.getString(R.string.recyclerVisibleThreshold), value);
                        editor.apply();
                    }
                }
                catch (NumberFormatException ignored) {}
            }
        });

        imageListColumnsCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                try {
                    final int value = Integer.parseInt(imageListColumnsCount.getText().toString());

                    if (value > 0) {
                        final Context context = requireActivity();
                        final SharedPreferences sharedPref =
                            context.getSharedPreferences(getString(R.string.settingsFile),
                                Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(context.getString(R.string.imageListColumns), value);
                        editor.apply();
                    }
                }
                catch (NumberFormatException ignored) {}
            }
        });

        imageListOrientation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(context.getString(R.string.imageListOrientation), isChecked
                ? StaggeredGridLayoutManager.VERTICAL : StaggeredGridLayoutManager.HORIZONTAL);
            editor.apply();
        });

        enablePostLabels.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.imageListInfo), isChecked);
            editor.apply();
        });

        saveBrowseStateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                cachedBrowseStateSwitch.setChecked(false);
            }

            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.saveBrowseState), isChecked);
            editor.apply();
        });

        cachedBrowseStateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBrowseStateSwitch.setChecked(true);
            }

            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.cachedBrowseStateSetting), isChecked);
            editor.apply();
        });

        InvalidateCachedBrowseTimeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                try {
                    final int value =
                        Integer.parseInt(InvalidateCachedBrowseTimeEditText.getText().toString());

                    if (value > 0 && value < Integer.MAX_VALUE) {
                        final Context context = requireActivity();
                        final SharedPreferences sharedPref =
                            context.getSharedPreferences(getString(R.string.settingsFile),
                                Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(context.getString(R.string.InvalidateCachedBrowseTimeSetting),
                            value);
                        editor.apply();
                    }
                }
                catch (NumberFormatException ignored) {}
            }
        });

        InvalidateCachedBrowseAfterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                try {
                    final int value =
                        Integer.parseInt(InvalidateCachedBrowseAfterEditText.getText().toString());

                    if (value > 0 && value <= 24) {
                        final Context context = requireActivity();
                        final SharedPreferences sharedPref =
                            context.getSharedPreferences(getString(R.string.settingsFile),
                                Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(
                            context.getString(R.string.InvalidateCachedBrowseAfterSetting), value);
                        editor.apply();
                    }
                }
                catch (NumberFormatException ignored) {}
            }
        });

        saveSearchStateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                cachedSearchStateSwitch.setChecked(false);
            }

            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.saveSearchState), isChecked);
            editor.apply();
        });

        cachedSearchStateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveSearchStateSwitch.setChecked(true);
            }

            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.cachedSearchStateSetting), isChecked);
            editor.apply();
        });

        InvalidateCachedSearchTimeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                try {
                    final int value =
                        Integer.parseInt(InvalidateCachedSearchTimeEditText.getText().toString());

                    if (value > 0 && value < Integer.MAX_VALUE) {
                        final Context context = requireActivity();
                        final SharedPreferences sharedPref =
                            context.getSharedPreferences(getString(R.string.settingsFile),
                                Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(context.getString(R.string.InvalidateCachedSearchTimeSetting),
                            value);
                        editor.apply();
                    }
                }
                catch (NumberFormatException ignored) {}
            }
        });

        InvalidateCachedSearchAfterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                try {
                    final int value =
                        Integer.parseInt(InvalidateCachedSearchAfterEditText.getText().toString());

                    if (value > 0 && value <= 24) {
                        final Context context = requireActivity();
                        final SharedPreferences sharedPref =
                            context.getSharedPreferences(getString(R.string.settingsFile),
                                Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(
                            context.getString(R.string.InvalidateCachedSearchAfterSetting), value);
                        editor.apply();
                    }
                }
                catch (NumberFormatException ignored) {}
            }
        });

        trackHistory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.trackHistorySetting), isChecked);
            editor.apply();

            ((MainActivity) context).updateUiElements();
        });

        clearHistory.setOnClickListener(v -> {
            final HistoryDBHelper dbHelper = new HistoryDBHelper(getActivity());
            final SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

            // Delete previous versions from history
            sqliteDatabase.delete(HistoryContract.historyItemEntry.TABLE_NAME_JOURNAL, null, null);
            sqliteDatabase.delete(HistoryContract.historyItemEntry.TABLE_NAME_USER, null, null);
            sqliteDatabase.delete(HistoryContract.historyItemEntry.TABLE_NAME_VIEW, null, null);

            sqliteDatabase.close();
        });

        trackBackHistory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getString(R.string.trackBackHistorySetting), isChecked);
            editor.apply();

            ((MainActivity) context).updateUiElements();
        });

        clearBackHistory.setOnClickListener(v -> {
            final BackDBHelper dbHelper = new BackDBHelper(getActivity());
            final SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

            // Delete previous versions from history
            sqliteDatabase.delete(BackContract.backItemEntry.TABLE_NAME_BACK_HISTORY, null, null);
            sqliteDatabase.close();
        });
    }
}
