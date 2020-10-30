package open.furaffinity.client.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.imageListAdapter;
import open.furaffinity.client.adapter.savedSearchListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.sqlite.searchContract.searchItemEntry;
import open.furaffinity.client.sqlite.searchDBHelper;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.notificationItem;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class search extends Fragment {
    private static final String TAG = search.class.getName();

    private LinearLayoutManager layoutManager;
    private LinearLayoutManager saveLayoutManager;

    private ScrollView searchOptionsScrollView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private RecyclerView savedSearchRecyclerView;
    private RecyclerView.Adapter savedMAdapter;
    private EndlessRecyclerViewScrollListener savedEndlessRecyclerViewScrollListener;

    private EditText searchEditText;
    private Spinner searchOrderBySpinner;
    private Spinner searchOrderDirectionSpinner;
    private RadioButton searchDayRadioButton;
    private RadioButton search3DaysRadioButton;
    private RadioButton searchWeekRadioButton;
    private RadioButton searchMonthRadioButton;
    private RadioButton searchAllRadioButtonRange;
    private Switch searchRatingGeneralSwitch;
    private Switch searchRatingMatureSwitch;
    private Switch searchRatingAdultSwitch;
    private Switch searchTypeArtSwitch;
    private Switch searchTypeMusicSwitch;
    private Switch searchTypeFlashSwitch;
    private Switch searchTypeStorySwitch;
    private Switch searchTypePhotoSwitch;
    private Switch searchTypePoetrySwitch;
    private RadioButton searchAllRadioButtonKeywords;
    private RadioButton searchAnyRadioButton;
    private RadioButton searchExtendedRadioButton;
    private EditText saveSearchEditText;
    private Button saveSearchButton;

    private FloatingActionButton fab;

    private webClient webClient;
    private loginTest loginTest;
    private open.furaffinity.client.pages.search page;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private boolean loadedMainActivitySearchQuery = false;

    private List<notificationItem> savedMDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());
        saveLayoutManager = new LinearLayoutManager(getActivity());

        searchOptionsScrollView = rootView.findViewById(R.id.searchOptionsScrollView);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        savedSearchRecyclerView = rootView.findViewById(R.id.savedSearchRecyclerView);

        searchEditText = rootView.findViewById(R.id.searcheditText);
        searchOrderBySpinner = rootView.findViewById(R.id.searchOrderBySpinner);
        searchOrderDirectionSpinner = rootView.findViewById(R.id.searchOrderDirectionSpinner);
        searchDayRadioButton = rootView.findViewById(R.id.searchDayRadioButton);
        search3DaysRadioButton = rootView.findViewById(R.id.search3DaysRadioButton);
        searchWeekRadioButton = rootView.findViewById(R.id.searchWeekRadioButton);
        searchMonthRadioButton = rootView.findViewById(R.id.searchMonthRadioButton);
        searchAllRadioButtonRange = rootView.findViewById(R.id.searchAllRadioButtonRange);
        searchRatingGeneralSwitch = rootView.findViewById(R.id.searchRatingGeneralSwitch);
        searchRatingMatureSwitch = rootView.findViewById(R.id.searchRatingMatureSwitch);
        searchRatingAdultSwitch = rootView.findViewById(R.id.searchRatingAdultSwitch);
        searchTypeArtSwitch = rootView.findViewById(R.id.searchTypeArtSwitch);
        searchTypeMusicSwitch = rootView.findViewById(R.id.searchTypeMusicSwitch);
        searchTypeFlashSwitch = rootView.findViewById(R.id.searchTypeFlashSwitch);
        searchTypeStorySwitch = rootView.findViewById(R.id.searchTypeStorySwitch);
        searchTypePhotoSwitch = rootView.findViewById(R.id.searchTypePhotoSwitch);
        searchTypePoetrySwitch = rootView.findViewById(R.id.searchTypePoetrySwitch);
        searchAllRadioButtonKeywords = rootView.findViewById(R.id.searchAllRadioButtonKeywords);
        searchAnyRadioButton = rootView.findViewById(R.id.searchAnyRadioButton);
        searchExtendedRadioButton = rootView.findViewById(R.id.searchExtendedRadioButton);
        saveSearchEditText = rootView.findViewById(R.id.saveSearchEditText);
        saveSearchButton = rootView.findViewById(R.id.saveSearchButton);

        fab = rootView.findViewById(R.id.fab);
    }

    private void initClientAndPage() {
        webClient = new webClient(this.getActivity());
        loginTest = new loginTest();
        page = new open.furaffinity.client.pages.search();
    }

    private void fetchPageData() {
        loginTest = new loginTest();
        page = new open.furaffinity.client.pages.search(page);
        try {
            loginTest.execute(webClient).get();
            page.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }

        List<HashMap<String, String>> pageResults = page.getPageResults();

        //Deduplicate results
        List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
        List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
        newPostPaths.removeAll(oldPostPaths);
        pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("postPath"))).collect(Collectors.toList());
        mDataSet.addAll(pageResults);

        if (page.getPageResults() != null && page.getPageResults().size() > 0 && page.getCurrentPage().equals("1")) {
            //Find any saved searches that meet the current search criteria and apply the most recent link to them
            searchDBHelper dbHelper = new searchDBHelper(getActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(searchItemEntry.COLUMN_NAME_MOSTRECENTITEM, page.getPageResults().get(0).get("postPath"));

            String selection = "";
            selection += searchItemEntry.COLUMN_NAME_Q + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_ORDERBY + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_ORDERDIRECTION + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_RANGE + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_RATINGGENERAL + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_RATINGMATURE + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_RATINGADULT + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_TYPEART + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_TYPEMUSIC + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_TYPEFLASH + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_TYPESTORY + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_TYPEPHOTO + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_TYPEPOETRY + " = ? AND ";
            selection += searchItemEntry.COLUMN_NAME_MODE + " = ? ";

            String[] selectionArgs = {
                    page.getCurrentQuery(),
                    page.getCurrentOrderBy(),
                    page.getCurrentOrderDirection(),
                    page.getCurrentRange(),
                    ((page.getCurrentRatingGeneral().equals("")) ? ("0") : ("1")),
                    ((page.getCurrentRatingMature().equals("")) ? ("0") : ("1")),
                    ((page.getCurrentRatingAdult().equals("")) ? ("0") : ("1")),
                    ((page.getCurrentTypeArt().equals("")) ? ("0") : ("1")),
                    ((page.getCurrentTypeMusic().equals("")) ? ("0") : ("1")),
                    ((page.getCurrentTypeFlash().equals("")) ? ("0") : ("1")),
                    ((page.getCurrentTypeStory().equals("")) ? ("0") : ("1")),
                    ((page.getCurrentTypePhoto().equals("")) ? ("0") : ("1")),
                    ((page.getCurrentTypePoetry().equals("")) ? ("0") : ("1")),
                    page.getCurrentMode()
            };

            db.update(searchItemEntry.TABLE_NAME, values, selection, selectionArgs);
            db.close();
        }
    }

    private void initCurrentSettings() {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getString(R.string.saveSearchState), open.furaffinity.client.fragments.settings.saveSearchStateDefault)) {
            fetchPageData();
            mDataSet.clear();
            page = new open.furaffinity.client.pages.search(page);

            page.setOrderBy(sharedPref.getString(getString(R.string.searchOrderBySetting), ""));
            page.setOrderDirection(sharedPref.getString(getString(R.string.searchOrderDirectionSetting), ""));
            page.setRange(sharedPref.getString(getString(R.string.searchRangeSetting), ""));
            page.setRatingGeneral(sharedPref.getBoolean(getString(R.string.searchRatingGeneralSetting), true));
            page.setRatingMature(sharedPref.getBoolean(getString(R.string.searchRatingMatureSetting), false));
            page.setRatingAdult(sharedPref.getBoolean(getString(R.string.searchRatingAdultSetting), false));
            page.setTypeArt(sharedPref.getBoolean(getString(R.string.searchTypeArtSetting), true));
            page.setTypeMusic(sharedPref.getBoolean(getString(R.string.searchTypeMusicSetting), false));
            page.setTypeFlash(sharedPref.getBoolean(getString(R.string.searchTypeFlashSetting), false));
            page.setTypeStory(sharedPref.getBoolean(getString(R.string.searchTypeStorySetting), false));
            page.setTypePhoto(sharedPref.getBoolean(getString(R.string.searchTypePhotoSetting), false));
            page.setTypePoetry(sharedPref.getBoolean(getString(R.string.searchTypePoetrySetting), false));
            page.setMode(sharedPref.getString(getString(R.string.searchModeSetting), ""));
        }
    }

    private void loadSavedSearches() {
        savedMDataSet = new ArrayList<>();

        searchDBHelper dbHelper = new searchDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                searchItemEntry.COLUMN_NAME_NAME,
                searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE,
                "rowid"
        };

        String sortOrder = "rowid DESC";

        Cursor cursor = db.query(
                searchItemEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_NAME));
            boolean itemNotificationState = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE)) > 0) ? (true) : (false));
            int rowId = cursor.getInt(cursor.getColumnIndexOrThrow("rowid"));
            savedMDataSet.add(new notificationItem(itemName, itemNotificationState, rowId));
        }

        db.close();
    }

    private void saveCurrentSearch() {
        searchDBHelper dbHelper = new searchDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String name = ((saveSearchEditText.getText().toString().length() > 0) ? (saveSearchEditText.getText().toString()) : ("No Name Set"));
        String selectedQueryValue = searchEditText.getText().toString();
        String selectedOrderByValue = ((kvPair) searchOrderBySpinner.getSelectedItem()).getKey();
        String selectedOrderDirectionValue = ((kvPair) searchOrderDirectionSpinner.getSelectedItem()).getKey();
        String selectedRangeValue = (searchDayRadioButton.isChecked()) ? ("day") : ("all");
        selectedRangeValue = (search3DaysRadioButton.isChecked()) ? ("3days") : (selectedRangeValue);
        selectedRangeValue = (searchWeekRadioButton.isChecked()) ? ("week") : (selectedRangeValue);
        selectedRangeValue = (searchMonthRadioButton.isChecked()) ? ("month") : (selectedRangeValue);
        selectedRangeValue = (searchAllRadioButtonRange.isChecked()) ? ("all") : (selectedRangeValue);
        int selectedRatingGeneralValue = (searchRatingGeneralSwitch.isChecked() ? (1) : (0));
        int selectedRatingMatureValue = (searchRatingMatureSwitch.isChecked() ? (1) : (0));
        int selectedRatingAdultValue = (searchRatingAdultSwitch.isChecked() ? (1) : (0));
        int selectedTypeArtValue = (searchTypeArtSwitch.isChecked() ? (1) : (0));
        int selectedTypeMusicValue = (searchTypeMusicSwitch.isChecked() ? (1) : (0));
        int selectedTypeFlashValue = (searchTypeFlashSwitch.isChecked() ? (1) : (0));
        int selectedTypeStoryValue = (searchTypeStorySwitch.isChecked() ? (1) : (0));
        int selectedTypePhotoValue = (searchTypePhotoSwitch.isChecked() ? (1) : (0));
        int selectedTypePoetryValue = (searchTypePoetrySwitch.isChecked() ? (1) : (0));
        String selectedModeValue = (searchAllRadioButtonKeywords.isChecked()) ? ("all") : ("all");
        selectedModeValue = (searchAnyRadioButton.isChecked()) ? ("any") : (selectedModeValue);
        selectedModeValue = (searchExtendedRadioButton.isChecked()) ? ("extended") : (selectedModeValue);

        ContentValues values = new ContentValues();
        values.put(searchItemEntry.COLUMN_NAME_NAME, name);
        values.put(searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE, 0);
        values.put(searchItemEntry.COLUMN_NAME_MOSTRECENTITEM, "");
        values.put(searchItemEntry.COLUMN_NAME_Q, selectedQueryValue);
        values.put(searchItemEntry.COLUMN_NAME_ORDERBY, selectedOrderByValue);
        values.put(searchItemEntry.COLUMN_NAME_ORDERDIRECTION, selectedOrderDirectionValue);
        values.put(searchItemEntry.COLUMN_NAME_RANGE, selectedRangeValue);
        values.put(searchItemEntry.COLUMN_NAME_RATINGGENERAL, selectedRatingGeneralValue);
        values.put(searchItemEntry.COLUMN_NAME_RATINGMATURE, selectedRatingMatureValue);
        values.put(searchItemEntry.COLUMN_NAME_RATINGADULT, selectedRatingAdultValue);
        values.put(searchItemEntry.COLUMN_NAME_TYPEART, selectedTypeArtValue);
        values.put(searchItemEntry.COLUMN_NAME_TYPEMUSIC, selectedTypeMusicValue);
        values.put(searchItemEntry.COLUMN_NAME_TYPEFLASH, selectedTypeFlashValue);
        values.put(searchItemEntry.COLUMN_NAME_TYPESTORY, selectedTypeStoryValue);
        values.put(searchItemEntry.COLUMN_NAME_TYPEPHOTO, selectedTypePhotoValue);
        values.put(searchItemEntry.COLUMN_NAME_TYPEPOETRY, selectedTypePoetryValue);
        values.put(searchItemEntry.COLUMN_NAME_MODE, selectedModeValue);

        db.insert(searchItemEntry.TABLE_NAME, null, values);
        db.close();
    }

    private void loadCurrentSettings() {
        String selectedSearch = ((mainActivity) getActivity()).getSearchSelected();
        if (selectedSearch != null) {
            searchDBHelper dbHelper = new searchDBHelper(getActivity());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] projection = {
                    searchItemEntry.COLUMN_NAME_Q,
                    searchItemEntry.COLUMN_NAME_ORDERBY,
                    searchItemEntry.COLUMN_NAME_ORDERDIRECTION,
                    searchItemEntry.COLUMN_NAME_RANGE,
                    searchItemEntry.COLUMN_NAME_RATINGGENERAL,
                    searchItemEntry.COLUMN_NAME_RATINGMATURE,
                    searchItemEntry.COLUMN_NAME_RATINGADULT,
                    searchItemEntry.COLUMN_NAME_TYPEART,
                    searchItemEntry.COLUMN_NAME_TYPEMUSIC,
                    searchItemEntry.COLUMN_NAME_TYPEFLASH,
                    searchItemEntry.COLUMN_NAME_TYPESTORY,
                    searchItemEntry.COLUMN_NAME_TYPEPHOTO,
                    searchItemEntry.COLUMN_NAME_TYPEPOETRY,
                    searchItemEntry.COLUMN_NAME_MODE
            };

            String selection = "rowid = ?";
            String[] selectionArgs = {selectedSearch};

            String sortOrder = "rowid DESC";

            Cursor cursor = db.query(
                    searchItemEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            while (cursor.moveToNext()) {
                String COLUMN_Q = cursor.getString(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_Q));
                String COLUMN_ORDERBY = cursor.getString(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_ORDERBY));
                String COLUMN_ORDERDIRECTION = cursor.getString(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_ORDERDIRECTION));
                String COLUMN_RANGE = cursor.getString(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_RANGE));
                boolean COLUMN_RATINGGENERAL = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_RATINGGENERAL)) > 0) ? (true) : (false));
                boolean COLUMN_RATINGMATURE = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_RATINGMATURE)) > 0) ? (true) : (false));
                boolean COLUMN_RATINGADULT = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_RATINGADULT)) > 0) ? (true) : (false));
                boolean COLUMN_TYPEART = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEART)) > 0) ? (true) : (false));
                boolean COLUMN_TYPEMUSIC = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEMUSIC)) > 0) ? (true) : (false));
                boolean COLUMN_TYPEFLASH = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEFLASH)) > 0) ? (true) : (false));
                boolean COLUMN_TYPESTORY = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPESTORY)) > 0) ? (true) : (false));
                boolean COLUMN_TYPEPHOTO = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEPHOTO)) > 0) ? (true) : (false));
                boolean COLUMN_TYPEPOETRY = ((cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEPOETRY)) > 0) ? (true) : (false));
                String COLUMN_MODE = cursor.getString(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_MODE));

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
            }

            db.close();

            fetchPageData();

            loadedMainActivitySearchQuery = true;

            searchOptionsScrollView.setVisibility(View.GONE);
            savedSearchRecyclerView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (!loadedMainActivitySearchQuery) {
            String mainActivitySearchQuery = ((mainActivity) getActivity()).getSearchQuery();
            if (mainActivitySearchQuery != null) {
                searchEditText.setText(mainActivitySearchQuery);
            } else {
                searchEditText.setText(page.getCurrentQuery());
            }
            loadedMainActivitySearchQuery = true;
        } else {
            searchEditText.setText(page.getCurrentQuery());
        }

        uiControls.spinnerSetAdapter(requireContext(), searchOrderBySpinner, page.getOrderBy(), page.getCurrentOrderBy(), false, false);
        uiControls.spinnerSetAdapter(requireContext(), searchOrderDirectionSpinner, page.getOrderDirection(), page.getCurrentOrderDirection(), false, false);

        switch (page.getCurrentRange()) {
            case "day":
                searchDayRadioButton.setChecked(true);
                break;
            case "3days":
                search3DaysRadioButton.setChecked(true);
                break;
            case "week":
                searchWeekRadioButton.setChecked(true);
                break;
            case "month":
                searchMonthRadioButton.setChecked(true);
                break;
            case "all":
            default:
                searchAllRadioButtonRange.setChecked(true);
                break;

        }

        searchRatingGeneralSwitch.setChecked((!page.getCurrentRatingGeneral().equals("")));

        if (loginTest.getIsLoggedIn()) {
            searchRatingMatureSwitch.setChecked((!page.getCurrentRatingMature().equals("")));
            searchRatingAdultSwitch.setChecked((!page.getCurrentRatingAdult().equals("")));
        } else {
            searchRatingMatureSwitch.setChecked(false);
            searchRatingAdultSwitch.setChecked(false);
        }

        searchTypeArtSwitch.setChecked((!page.getCurrentTypeArt().equals("")));
        searchTypeMusicSwitch.setChecked((!page.getCurrentTypeMusic().equals("")));
        searchTypeFlashSwitch.setChecked((!page.getCurrentTypeFlash().equals("")));
        searchTypeStorySwitch.setChecked((!page.getCurrentTypeStory().equals("")));
        searchTypePhotoSwitch.setChecked((!page.getCurrentTypePhoto().equals("")));
        searchTypePoetrySwitch.setChecked((!page.getCurrentTypePoetry().equals("")));

        switch (page.getCurrentMode()) {
            case "all":
                searchAllRadioButtonKeywords.setChecked(true);
                break;
            case "any":
                searchAnyRadioButton.setChecked(true);
                break;
            case "extended":
            default:
                searchExtendedRadioButton.setChecked(true);
                break;
        }

    }

    private void saveCurrentSettings() {
        boolean valueChanged = false;

        String selectedQueryValue = searchEditText.getText().toString();
        if (!page.getCurrentQuery().equals(selectedQueryValue)) {
            page.setQuery(selectedQueryValue);
            valueChanged = true;
        }

        String selectedOrderByValue = ((kvPair) searchOrderBySpinner.getSelectedItem()).getKey();
        if (!page.getCurrentOrderBy().equals(selectedOrderByValue)) {
            page.setOrderBy(selectedOrderByValue);
            valueChanged = true;
        }

        String selectedOrderDirectionValue = ((kvPair) searchOrderDirectionSpinner.getSelectedItem()).getKey();
        if (!page.getCurrentOrderDirection().equals(selectedOrderDirectionValue)) {
            page.setOrderDirection(selectedOrderDirectionValue);
            valueChanged = true;
        }

        String selectedRangeValue = (searchDayRadioButton.isChecked()) ? ("day") : ("all");
        selectedRangeValue = (search3DaysRadioButton.isChecked()) ? ("3days") : (selectedRangeValue);
        selectedRangeValue = (searchWeekRadioButton.isChecked()) ? ("week") : (selectedRangeValue);
        selectedRangeValue = (searchMonthRadioButton.isChecked()) ? ("month") : (selectedRangeValue);
        selectedRangeValue = (searchAllRadioButtonRange.isChecked()) ? ("all") : (selectedRangeValue);
        if (!page.getCurrentRange().equals(selectedRangeValue)) {
            page.setRange(selectedRangeValue);
            valueChanged = true;
        }

        String selectedRatingGeneralValue = (searchRatingGeneralSwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentRatingGeneral().equals(selectedRatingGeneralValue)) {
            page.setRatingGeneral(searchRatingGeneralSwitch.isChecked());
            valueChanged = true;
        }

        String selectedRatingMatureValue = (searchRatingMatureSwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentRatingMature().equals(selectedRatingMatureValue)) {
            page.setRatingMature(searchRatingMatureSwitch.isChecked());
            valueChanged = true;
        }

        String selectedRatingAdultValue = (searchRatingAdultSwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentRatingAdult().equals(selectedRatingAdultValue)) {
            page.setRatingAdult(searchRatingAdultSwitch.isChecked());
            valueChanged = true;
        }

        String selectedTypeArtValue = (searchTypeArtSwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentTypeArt().equals(selectedTypeArtValue)) {
            page.setTypeArt(searchTypeArtSwitch.isChecked());
            valueChanged = true;
        }

        String selectedTypeMusicValue = (searchTypeMusicSwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentTypeMusic().equals(selectedTypeMusicValue)) {
            page.setTypeMusic(searchTypeMusicSwitch.isChecked());
            valueChanged = true;
        }

        String selectedTypeFlashValue = (searchTypeFlashSwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentTypeFlash().equals(selectedTypeFlashValue)) {
            page.setTypeFlash(searchTypeFlashSwitch.isChecked());
            valueChanged = true;
        }

        String selectedTypeStoryValue = (searchTypeStorySwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentTypeStory().equals(selectedTypeStoryValue)) {
            page.setTypeStory(searchTypeStorySwitch.isChecked());
            valueChanged = true;
        }

        String selectedTypePhotoValue = (searchTypePhotoSwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentTypePhoto().equals(selectedTypePhotoValue)) {
            page.setTypePhoto(searchTypePhotoSwitch.isChecked());
            valueChanged = true;
        }

        String selectedTypePoetryValue = (searchTypePoetrySwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentTypePoetry().equals(selectedTypePoetryValue)) {
            page.setTypePoetry(searchTypePoetrySwitch.isChecked());
            valueChanged = true;
        }

        String selectedModeValue = (searchAllRadioButtonKeywords.isChecked()) ? ("all") : ("all");
        selectedModeValue = (searchAnyRadioButton.isChecked()) ? ("any") : (selectedModeValue);
        selectedModeValue = (searchExtendedRadioButton.isChecked()) ? ("extended") : (selectedModeValue);
        if (!page.getCurrentMode().equals(selectedModeValue)) {
            page.setMode(selectedModeValue);
            valueChanged = true;
        }

        if (valueChanged) {
            Context context = getActivity();
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

            if (sharedPref.getBoolean(getString(R.string.saveSearchState), settings.saveSearchStateDefault)) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.searchOrderBySetting), selectedOrderByValue);
                editor.putString(getString(R.string.searchOrderDirectionSetting), selectedOrderDirectionValue);
                editor.putString(getString(R.string.searchRangeSetting), selectedRangeValue);
                editor.putBoolean(getString(R.string.searchRatingGeneralSetting), ((selectedRatingGeneralValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.searchRatingMatureSetting), ((selectedRatingMatureValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.searchRatingAdultSetting), ((selectedRatingAdultValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.searchTypeArtSetting), ((selectedTypeArtValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.searchTypeMusicSetting), ((selectedTypeMusicValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.searchTypeFlashSetting), ((selectedTypeFlashValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.searchTypeStorySetting), ((selectedTypeStoryValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.searchTypePhotoSetting), ((selectedTypePhotoValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.searchTypePoetrySetting), ((selectedTypePoetryValue.equals("on")) ? (true) : (false)));
                editor.putString(getString(R.string.searchModeSetting), selectedModeValue);
                editor.apply();
                editor.commit();
            }

            recyclerView.scrollTo(0, 0);
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();
            page = new open.furaffinity.client.pages.search(page);
            fetchPageData();
        }
    }

    private void updateUIElements() {
        if (loginTest.getIsLoggedIn() && loginTest.getIsNSFWAllowed()) {
            searchRatingMatureSwitch.setVisibility(View.VISIBLE);
            searchRatingAdultSwitch.setVisibility(View.VISIBLE);
        } else {
            searchRatingMatureSwitch.setVisibility(View.GONE);
            searchRatingAdultSwitch.setVisibility(View.GONE);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new imageListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        savedSearchRecyclerView.setHasFixedSize(true);
        savedSearchRecyclerView.setLayoutManager(saveLayoutManager);
        savedMAdapter = new savedSearchListAdapter(savedMDataSet, getActivity());
        savedSearchRecyclerView.setAdapter(savedMAdapter);
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page.setPage(Integer.toString(1));

                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                mAdapter.notifyDataSetChanged();
                endlessRecyclerViewScrollListener.resetState();
                page = new open.furaffinity.client.pages.search(page);
                fetchPageData();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setPage(Integer.toString(page.getPage() + 1));
                int curSize = mAdapter.getItemCount();
                fetchPageData();
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        savedEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {

            }
        };

        //noinspection deprecation
        savedSearchRecyclerView.setOnScrollListener(savedEndlessRecyclerViewScrollListener);

        saveSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentSearch();
                loadSavedSearches();
                saveCurrentSettings();
                updateUIElements();
            }
        });

        fab.setOnClickListener(view ->
        {
            if (savedSearchRecyclerView.getVisibility() == View.VISIBLE) {
                savedSearchRecyclerView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                searchOptionsScrollView.setVisibility(View.VISIBLE);
            } else if (searchOptionsScrollView.getVisibility() == View.VISIBLE) {
                searchOptionsScrollView.setVisibility(View.GONE);
                savedSearchRecyclerView.setVisibility(View.GONE);
                saveCurrentSettings();
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                swipeRefreshLayout.setVisibility(View.GONE);
                savedSearchRecyclerView.setVisibility(View.GONE);
                loadCurrentSettings();
                updateUIElements();
                searchOptionsScrollView.setVisibility(View.VISIBLE);
            }
        });

        fab.setOnLongClickListener(view ->
        {
            if (savedSearchRecyclerView.getVisibility() != View.VISIBLE) {
                searchOptionsScrollView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                loadSavedSearches();
                savedSearchRecyclerView.setVisibility(View.VISIBLE);
            }
            return true;
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        getElements(rootView);
        initClientAndPage();
        initCurrentSettings();
        fetchPageData();
        loadSavedSearches();
        loadCurrentSettings();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
