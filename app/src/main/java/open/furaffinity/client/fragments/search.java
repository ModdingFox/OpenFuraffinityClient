package open.furaffinity.client.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.page;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.imageListAdapter;
import open.furaffinity.client.adapter.savedSearchListAdapter;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.fragmentsOld.settings;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.loginCheck;
import open.furaffinity.client.sqlite.searchContract.searchItemEntry;
import open.furaffinity.client.sqlite.searchDBHelper;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.notificationItem;
import open.furaffinity.client.utilities.uiControls;

public class search extends Fragment {
    private static final String TAG = search.class.getName();

    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private LinearLayoutManager saveLayoutManager;

    private ScrollView searchOptionsScrollView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private RecyclerView savedSearchRecyclerView;
    private RecyclerView.Adapter savedMAdapter;

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

    private fabCircular fab;
    private FloatingActionButton search;
    private FloatingActionButton savedSearches;
    private FloatingActionButton searchSettings;
    private FloatingActionButton saveSearch;

    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.search page;

    private boolean isInitialized = false;
    private boolean isLoading = false;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private boolean loadedMainActivitySearchQuery = false;

    private List<notificationItem> savedMDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(sharedPref.getInt(getString(R.string.imageListColumns), settings.imageListColumnsDefault), sharedPref.getInt(getString(R.string.imageListOrientation), settings.imageListOrientationDefault));
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

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        search = new FloatingActionButton(getContext());
        savedSearches = new FloatingActionButton(getContext());
        searchSettings = new FloatingActionButton(getContext());
        saveSearch = new FloatingActionButton(getContext());

        search.setImageResource(R.drawable.ic_menu_search);
        savedSearches.setImageResource(R.drawable.ic_menu_galleryfolder);
        searchSettings.setImageResource(R.drawable.ic_menu_settings);
        saveSearch.setImageResource(R.drawable.ic_menu_save);

        search.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        savedSearches.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        searchSettings.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        saveSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        search.setVisibility(View.GONE);
        savedSearches.setVisibility(View.GONE);
        searchSettings.setVisibility(View.GONE);
        saveSearch.setVisibility(View.GONE);

        constraintLayout.addView(search);
        constraintLayout.addView(savedSearches);
        constraintLayout.addView(searchSettings);
        constraintLayout.addView(saveSearch);

        fab.addButton(search, 1.5f, 270);
        fab.addButton(savedSearches, 1.5f, 225);
        fab.addButton(saveSearch, 1.5f, 180);
    }

    private void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new open.furaffinity.client.pages.search(page);
            page.execute();
        }
    }

    private void resetRecycler() {
        page.setPage(Integer.toString(1));
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    private void initCurrentSettings() {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getString(R.string.saveSearchState), open.furaffinity.client.fragmentsOld.settings.saveSearchStateDefault)) {
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

        isInitialized = true;
    }

    private void setFabSearchMode() {
        fab.removeButton(search);
        fab.removeButton(savedSearches);
        fab.removeButton(searchSettings);
        fab.removeButton(saveSearch);

        fab.addButton(searchSettings, 1.5f, 270);
        fab.addButton(savedSearches, 1.5f, 225);
        fab.addButton(saveSearch, 1.5f, 180);
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

            loadedMainActivitySearchQuery = true;
            setFabSearchMode();

            searchOptionsScrollView.setVisibility(View.GONE);
            savedSearchRecyclerView.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
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

        //fa will ignore these in sfw mode anyways
        searchRatingMatureSwitch.setChecked((!page.getCurrentRatingMature().equals("")));
        searchRatingAdultSwitch.setChecked((!page.getCurrentRatingAdult().equals("")));

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

    private void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new imageListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        loginCheck = new loginCheck(getActivity(), new page.pageListener() {
            private void updateUIElements() {
                if (loginCheck.getIsLoggedIn() && loginCheck.getIsNSFWAllowed()) {
                    searchRatingMatureSwitch.setVisibility(View.VISIBLE);
                    searchRatingAdultSwitch.setVisibility(View.VISIBLE);
                } else {
                    searchRatingMatureSwitch.setVisibility(View.GONE);
                    searchRatingAdultSwitch.setVisibility(View.GONE);
                }
            }

            @Override
            public void requestSucceeded() {
                updateUIElements();
            }

            @Override
            public void requestFailed() {
                updateUIElements();
                Toast.makeText(getActivity(), "Failed to detect login", Toast.LENGTH_SHORT).show();
            }
        });

        page = new open.furaffinity.client.pages.search(getActivity(), new page.pageListener() {
            @Override
            public void requestSucceeded() {
                if (!isInitialized) {
                    isLoading = false;
                    initCurrentSettings();
                    loadCurrentSettings();
                    fab.setVisibility(View.VISIBLE);
                    resetRecycler();
                } else {
                    List<HashMap<String, String>> pageResults = page.getPageResults();

                    int curSize = mAdapter.getItemCount();

                    //Deduplicate results
                    List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                    List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                    newPostPaths.removeAll(oldPostPaths);
                    pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("postPath"))).collect(Collectors.toList());
                    mDataSet.addAll(pageResults);
                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);

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

                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                }
            }

            @Override
            public void requestFailed() {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data from search page", Toast.LENGTH_SHORT).show();
            }
        });
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
        textDialog textDialog = new textDialog();
        textDialog.setTitleText("Enter name for search:");
        textDialog.setListener(new textDialog.dialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                searchDBHelper dbHelper = new searchDBHelper(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String name = ((textDialog) dialog).getText();
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

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });
        textDialog.show(getChildFragmentManager(), "getSearchName");
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

            resetRecycler();
        }
    }

    private void updateUIElements() {
        savedSearchRecyclerView.setLayoutManager(saveLayoutManager);
        savedMAdapter = new savedSearchListAdapter(savedMDataSet, getActivity());
        savedSearchRecyclerView.setAdapter(savedMAdapter);
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecycler();
            }
        });

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setPage(Integer.toString(page.getPage() + 1));
                fetchPageData();
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        savedSearchRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                savedMAdapter.notifyDataSetChanged();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOptionsScrollView.setVisibility(View.GONE);
                savedSearchRecyclerView.setVisibility(View.GONE);
                saveCurrentSettings();
                updateUIElements();
                updateUIElementListeners(rootView);
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                swipeRefreshLayout.setVisibility(View.VISIBLE);

                setFabSearchMode();
            }
        });

        savedSearches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOptionsScrollView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                loadSavedSearches();
                updateUIElements();
                updateUIElementListeners(rootView);
                savedSearchRecyclerView.setVisibility(View.VISIBLE);

                fab.removeButton(search);
                fab.removeButton(savedSearches);
                fab.removeButton(searchSettings);
                fab.removeButton(saveSearch);

                fab.addButton(searchSettings, 1.5f, 270);
                fab.addButton(search, 1.5f, 225);
            }
        });

        searchSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setVisibility(View.GONE);
                savedSearchRecyclerView.setVisibility(View.GONE);
                loadCurrentSettings();
                resetRecycler();
                updateUIElements();
                updateUIElementListeners(rootView);
                searchOptionsScrollView.setVisibility(View.VISIBLE);

                fab.removeButton(search);
                fab.removeButton(savedSearches);
                fab.removeButton(searchSettings);
                fab.removeButton(saveSearch);

                fab.addButton(search, 1.5f, 270);
                fab.addButton(savedSearches, 1.5f, 225);
                fab.addButton(saveSearch, 1.5f, 180);
            }
        });

        saveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentSearch();
                loadSavedSearches();
                saveCurrentSettings();
                updateUIElements();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        getElements(rootView);
        initPages();
        fetchPageData();
        loadSavedSearches();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
