package open.furaffinity.client.fragmentDrawers;

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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.imageListAdapter;
import open.furaffinity.client.adapter.savedSearchListAdapter;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.loginCheck;
import open.furaffinity.client.sqlite.searchContract.searchItemEntry;
import open.furaffinity.client.sqlite.searchDBHelper;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.notificationItem;
import open.furaffinity.client.utilities.uiControls;

public class search extends appFragment {
    @SuppressWarnings("FieldCanBeLocal")
    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private LinearLayoutManager saveLayoutManager;

    private ScrollView searchOptionsScrollView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<imageListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private RecyclerView savedSearchRecyclerView;
    private RecyclerView.Adapter<savedSearchListAdapter.ViewHolder> savedMAdapter;

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
    private boolean isCacheInitialized = false;
    private boolean isLoading = false;
    private List<HashMap<String, String>> mDataSet;

    private String selectedSearch = null;
    private String searchParamaters = null;

    private List<notificationItem> savedMDataSet = new ArrayList<>();

    private int recyclerViewPosition = -1;
    private int pageNumber = -1;
    private int pageCacheCheckResultCount = 0;
    private String query = null;

    @Override
    protected int getLayout() {
        return R.layout.fragment_search;
    }

    protected void getElements(View rootView) {
        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        SharedPreferences sharedPref = requireContext().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

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

        search = new FloatingActionButton(requireContext());
        savedSearches = new FloatingActionButton(requireContext());
        searchSettings = new FloatingActionButton(requireContext());
        saveSearch = new FloatingActionButton(requireContext());

        search.setImageResource(R.drawable.ic_menu_search);
        savedSearches.setImageResource(R.drawable.ic_menu_galleryfolder);
        searchSettings.setImageResource(R.drawable.ic_menu_settings);
        saveSearch.setImageResource(R.drawable.ic_menu_save);

        //noinspection deprecation
        search.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        savedSearches.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        searchSettings.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
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

    protected void fetchPageData() {
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
        SharedPreferences sharedPref = requireContext().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getString(R.string.saveSearchState), settings.saveSearchStateDefault)) {
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
                boolean COLUMN_RATINGGENERAL = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_RATINGGENERAL)) > 0);
                boolean COLUMN_RATINGMATURE = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_RATINGMATURE)) > 0);
                boolean COLUMN_RATINGADULT = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_RATINGADULT)) > 0);
                boolean COLUMN_TYPEART = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEART)) > 0);
                boolean COLUMN_TYPEMUSIC = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEMUSIC)) > 0);
                boolean COLUMN_TYPEFLASH = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEFLASH)) > 0);
                boolean COLUMN_TYPESTORY = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPESTORY)) > 0);
                boolean COLUMN_TYPEPHOTO = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEPHOTO)) > 0);
                boolean COLUMN_TYPEPOETRY = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_TYPEPOETRY)) > 0);
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

            cursor.close();
            db.close();

            setFabSearchMode();

            searchOptionsScrollView.setVisibility(View.GONE);
            savedSearchRecyclerView.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);

            selectedSearch = null;
        } else if (searchParamaters != null) {
            try {
                JSONObject loadedSearchParamaters = new JSONObject(searchParamaters);

                for (Iterator<String> it = loadedSearchParamaters.keys(); it.hasNext(); ) {
                    String key = it.next();

                    switch(key) {
                        case searchItemEntry.COLUMN_NAME_Q:
                            page.setQuery(loadedSearchParamaters.getString(searchItemEntry.COLUMN_NAME_Q));
                            break;
                        case searchItemEntry.COLUMN_NAME_ORDERBY:
                            page.setOrderBy(loadedSearchParamaters.getString(searchItemEntry.COLUMN_NAME_ORDERBY));
                            break;
                        case searchItemEntry.COLUMN_NAME_ORDERDIRECTION:
                            page.setOrderDirection(loadedSearchParamaters.getString(searchItemEntry.COLUMN_NAME_ORDERDIRECTION));
                            break;
                        case searchItemEntry.COLUMN_NAME_RANGE:
                            page.setRange(loadedSearchParamaters.getString(searchItemEntry.COLUMN_NAME_RANGE));
                            break;
                        case searchItemEntry.COLUMN_NAME_RATINGGENERAL:
                            page.setRatingGeneral(loadedSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_RATINGGENERAL));
                            break;
                        case searchItemEntry.COLUMN_NAME_RATINGMATURE:
                            page.setRatingMature(loadedSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_RATINGMATURE));
                            break;
                        case searchItemEntry.COLUMN_NAME_RATINGADULT:
                            page.setRatingAdult(loadedSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_RATINGADULT));
                            break;
                        case searchItemEntry.COLUMN_NAME_TYPEART:
                            page.setTypeArt(loadedSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEART));
                            break;
                        case searchItemEntry.COLUMN_NAME_TYPEMUSIC:
                            page.setTypeMusic(loadedSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEMUSIC));
                            break;
                        case searchItemEntry.COLUMN_NAME_TYPEFLASH:
                            page.setTypeFlash(loadedSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEFLASH));
                            break;
                        case searchItemEntry.COLUMN_NAME_TYPESTORY:
                            page.setTypeStory(loadedSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPESTORY));
                            break;
                        case searchItemEntry.COLUMN_NAME_TYPEPHOTO:
                            page.setTypePhoto(loadedSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEPHOTO));
                            break;
                        case searchItemEntry.COLUMN_NAME_TYPEPOETRY:
                            page.setTypePoetry(loadedSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEPOETRY));
                            break;
                        case searchItemEntry.COLUMN_NAME_MODE:
                            page.setMode(loadedSearchParamaters.getString(searchItemEntry.COLUMN_NAME_MODE));
                            break;
                        default:
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            searchParamaters = null;
        }

        searchEditText.setText(page.getCurrentQuery());

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

    protected void initPages() {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new imageListAdapter(mDataSet, requireActivity(), requireActivity());
        recyclerView.setAdapter(mAdapter);

        if (recyclerViewPosition > -1) {
            staggeredGridLayoutManager.scrollToPosition(recyclerViewPosition);
        }

        loginCheck = new loginCheck(getActivity(), new abstractPage.pageListener() {
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
            public void requestSucceeded(abstractPage abstractPage) {
                updateUIElements();
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                updateUIElements();
                Toast.makeText(getActivity(), "Failed to detect login", Toast.LENGTH_SHORT).show();
            }
        });

        loginCheck.execute();

        page = new open.furaffinity.client.pages.search(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (!isInitialized) {
                    isLoading = false;
                    initCurrentSettings();
                    loadCurrentSettings();
                    fab.setVisibility(View.VISIBLE);

                    if (isCacheInitialized || mDataSet == null || mDataSet.size() == 0) {
                        isCacheInitialized = true;
                        resetRecycler();
                    } else {
                        page.setQuery(query);
                        fetchPageData();
                    }
                } else if (!isCacheInitialized) {
                    isLoading = false;
                    isCacheInitialized = true;

                    List<HashMap<String, String>> pageResults = page.getPageResults();
                    if (pageResults.size() > pageCacheCheckResultCount) {
                        pageResults = pageResults.subList(0, pageCacheCheckResultCount);
                    }

                    if (mDataSet.size() > 0 && pageResults.contains(mDataSet.get(0))) {
                        searchOptionsScrollView.setVisibility(View.GONE);
                        savedSearchRecyclerView.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        setFabSearchMode();

                        page.setPage(Integer.toString(pageNumber));
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        resetRecycler();
                    }
                } else {
                    List<HashMap<String, String>> pageResults = ((open.furaffinity.client.pages.search) abstractPage).getPageResults();

                    int curSize = mAdapter.getItemCount();

                    //Deduplicate results
                    List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                    List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                    newPostPaths.removeAll(oldPostPaths);
                    pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("postPath"))).collect(Collectors.toList());
                    mDataSet.addAll(pageResults);
                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());

                    if (((open.furaffinity.client.pages.search) abstractPage).getPageResults() != null && ((open.furaffinity.client.pages.search) abstractPage).getPageResults().size() > 0 && ((open.furaffinity.client.pages.search) abstractPage).getCurrentPage().equals("1")) {
                        //Find any saved searches that meet the current search criteria and apply the most recent link to them
                        searchDBHelper dbHelper = new searchDBHelper(getActivity());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put(searchItemEntry.COLUMN_NAME_MOSTRECENTITEM, ((open.furaffinity.client.pages.search) abstractPage).getPageResults().get(0).get("postPath"));

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
                                ((open.furaffinity.client.pages.search) abstractPage).getCurrentQuery(),
                                ((open.furaffinity.client.pages.search) abstractPage).getCurrentOrderBy(),
                                ((open.furaffinity.client.pages.search) abstractPage).getCurrentOrderDirection(),
                                ((open.furaffinity.client.pages.search) abstractPage).getCurrentRange(),
                                ((((open.furaffinity.client.pages.search) abstractPage).getCurrentRatingGeneral().equals("")) ? ("0") : ("1")),
                                ((((open.furaffinity.client.pages.search) abstractPage).getCurrentRatingMature().equals("")) ? ("0") : ("1")),
                                ((((open.furaffinity.client.pages.search) abstractPage).getCurrentRatingAdult().equals("")) ? ("0") : ("1")),
                                ((((open.furaffinity.client.pages.search) abstractPage).getCurrentTypeArt().equals("")) ? ("0") : ("1")),
                                ((((open.furaffinity.client.pages.search) abstractPage).getCurrentTypeMusic().equals("")) ? ("0") : ("1")),
                                ((((open.furaffinity.client.pages.search) abstractPage).getCurrentTypeFlash().equals("")) ? ("0") : ("1")),
                                ((((open.furaffinity.client.pages.search) abstractPage).getCurrentTypeStory().equals("")) ? ("0") : ("1")),
                                ((((open.furaffinity.client.pages.search) abstractPage).getCurrentTypePhoto().equals("")) ? ("0") : ("1")),
                                ((((open.furaffinity.client.pages.search) abstractPage).getCurrentTypePoetry().equals("")) ? ("0") : ("1")),
                                ((open.furaffinity.client.pages.search) abstractPage).getCurrentMode()
                        };

                        db.update(searchItemEntry.TABLE_NAME, values, selection, selectionArgs);
                        db.close();
                    }

                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data from search page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private JSONObject getSearchParamaterObject() {
        JSONObject result = new JSONObject();

        String selectedQueryValue = searchEditText.getText().toString();
        String selectedOrderByValue = ((kvPair) searchOrderBySpinner.getSelectedItem()).getKey();
        String selectedOrderDirectionValue = ((kvPair) searchOrderDirectionSpinner.getSelectedItem()).getKey();
        String selectedRangeValue = (searchDayRadioButton.isChecked()) ? ("day") : ("all");
        selectedRangeValue = (search3DaysRadioButton.isChecked()) ? ("3days") : (selectedRangeValue);
        selectedRangeValue = (searchWeekRadioButton.isChecked()) ? ("week") : (selectedRangeValue);
        selectedRangeValue = (searchMonthRadioButton.isChecked()) ? ("month") : (selectedRangeValue);
        selectedRangeValue = (searchAllRadioButtonRange.isChecked()) ? ("all") : (selectedRangeValue);
        boolean selectedRatingGeneralValue = searchRatingGeneralSwitch.isChecked();
        boolean selectedRatingMatureValue = searchRatingMatureSwitch.isChecked();
        boolean selectedRatingAdultValue = searchRatingAdultSwitch.isChecked();
        boolean selectedTypeArtValue = searchTypeArtSwitch.isChecked();
        boolean selectedTypeMusicValue = searchTypeMusicSwitch.isChecked();
        boolean selectedTypeFlashValue = searchTypeFlashSwitch.isChecked();
        boolean selectedTypeStoryValue = searchTypeStorySwitch.isChecked();
        boolean selectedTypePhotoValue = searchTypePhotoSwitch.isChecked();
        boolean selectedTypePoetryValue = searchTypePoetrySwitch.isChecked();
        String selectedModeValue = (searchAllRadioButtonKeywords.isChecked()) ? ("all") : ("all");
        selectedModeValue = (searchAnyRadioButton.isChecked()) ? ("any") : (selectedModeValue);
        selectedModeValue = (searchExtendedRadioButton.isChecked()) ? ("extended") : (selectedModeValue);

        try {
            result.put(searchItemEntry.COLUMN_NAME_Q, selectedQueryValue);
            result.put(searchItemEntry.COLUMN_NAME_ORDERBY, selectedOrderByValue);
            result.put(searchItemEntry.COLUMN_NAME_ORDERDIRECTION, selectedOrderDirectionValue);
            result.put(searchItemEntry.COLUMN_NAME_RANGE, selectedRangeValue);
            result.put(searchItemEntry.COLUMN_NAME_RATINGGENERAL, selectedRatingGeneralValue);
            result.put(searchItemEntry.COLUMN_NAME_RATINGMATURE, selectedRatingMatureValue);
            result.put(searchItemEntry.COLUMN_NAME_RATINGADULT, selectedRatingAdultValue);
            result.put(searchItemEntry.COLUMN_NAME_TYPEART, selectedTypeArtValue);
            result.put(searchItemEntry.COLUMN_NAME_TYPEMUSIC, selectedTypeMusicValue);
            result.put(searchItemEntry.COLUMN_NAME_TYPEFLASH, selectedTypeFlashValue);
            result.put(searchItemEntry.COLUMN_NAME_TYPESTORY, selectedTypeStoryValue);
            result.put(searchItemEntry.COLUMN_NAME_TYPEPHOTO, selectedTypePhotoValue);
            result.put(searchItemEntry.COLUMN_NAME_TYPEPOETRY, selectedTypePoetryValue);
            result.put(searchItemEntry.COLUMN_NAME_MODE, selectedModeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
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
            boolean itemNotificationState = (cursor.getInt(cursor.getColumnIndexOrThrow(searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE)) > 0);
            int rowId = cursor.getInt(cursor.getColumnIndexOrThrow("rowid"));
            savedMDataSet.add(new notificationItem(itemName, itemNotificationState, rowId));
        }

        cursor.close();
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
                JSONObject saveSearchParamaters = getSearchParamaterObject();

                ContentValues values = new ContentValues();
                values.put(searchItemEntry.COLUMN_NAME_NAME, name);
                values.put(searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE, 0);
                values.put(searchItemEntry.COLUMN_NAME_MOSTRECENTITEM, "");

                for (Iterator<String> it = saveSearchParamaters.keys(); it.hasNext(); ) {
                    String key = it.next();

                    try {
                        switch (key) {
                            case searchItemEntry.COLUMN_NAME_Q:
                                values.put(searchItemEntry.COLUMN_NAME_Q, saveSearchParamaters.getString(searchItemEntry.COLUMN_NAME_Q));
                                break;
                            case searchItemEntry.COLUMN_NAME_ORDERBY:
                                values.put(searchItemEntry.COLUMN_NAME_ORDERBY, saveSearchParamaters.getString(searchItemEntry.COLUMN_NAME_ORDERBY));
                                break;
                            case searchItemEntry.COLUMN_NAME_ORDERDIRECTION:
                                values.put(searchItemEntry.COLUMN_NAME_ORDERDIRECTION, saveSearchParamaters.getString(searchItemEntry.COLUMN_NAME_ORDERDIRECTION));
                                break;
                            case searchItemEntry.COLUMN_NAME_RANGE:
                                values.put(searchItemEntry.COLUMN_NAME_RANGE, saveSearchParamaters.getString(searchItemEntry.COLUMN_NAME_RANGE));
                                break;
                            case searchItemEntry.COLUMN_NAME_RATINGGENERAL:
                                values.put(searchItemEntry.COLUMN_NAME_RATINGGENERAL, saveSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_RATINGGENERAL));
                                break;
                            case searchItemEntry.COLUMN_NAME_RATINGMATURE:
                                values.put(searchItemEntry.COLUMN_NAME_RATINGMATURE, saveSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_RATINGMATURE));
                                break;
                            case searchItemEntry.COLUMN_NAME_RATINGADULT:
                                values.put(searchItemEntry.COLUMN_NAME_RATINGADULT, saveSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_RATINGADULT));
                                break;
                            case searchItemEntry.COLUMN_NAME_TYPEART:
                                values.put(searchItemEntry.COLUMN_NAME_TYPEART, saveSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEART));
                                break;
                            case searchItemEntry.COLUMN_NAME_TYPEMUSIC:
                                values.put(searchItemEntry.COLUMN_NAME_TYPEMUSIC, saveSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEMUSIC));
                                break;
                            case searchItemEntry.COLUMN_NAME_TYPEFLASH:
                                values.put(searchItemEntry.COLUMN_NAME_TYPEFLASH, saveSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEFLASH));
                                break;
                            case searchItemEntry.COLUMN_NAME_TYPESTORY:
                                values.put(searchItemEntry.COLUMN_NAME_TYPESTORY, saveSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPESTORY));
                                break;
                            case searchItemEntry.COLUMN_NAME_TYPEPHOTO:
                                values.put(searchItemEntry.COLUMN_NAME_TYPEPHOTO, saveSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEPHOTO));
                                break;
                            case searchItemEntry.COLUMN_NAME_TYPEPOETRY:
                                values.put(searchItemEntry.COLUMN_NAME_TYPEPOETRY, saveSearchParamaters.getBoolean(searchItemEntry.COLUMN_NAME_TYPEPOETRY));
                                break;
                            case searchItemEntry.COLUMN_NAME_MODE:
                                values.put(searchItemEntry.COLUMN_NAME_MODE, saveSearchParamaters.getString(searchItemEntry.COLUMN_NAME_MODE));
                                break;
                            default:
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

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
            SharedPreferences sharedPref = requireContext().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

            if (sharedPref.getBoolean(getString(R.string.saveSearchState), settings.saveSearchStateDefault)) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.searchOrderBySetting), selectedOrderByValue);
                editor.putString(getString(R.string.searchOrderDirectionSetting), selectedOrderDirectionValue);
                editor.putString(getString(R.string.searchRangeSetting), selectedRangeValue);
                editor.putBoolean(getString(R.string.searchRatingGeneralSetting), (selectedRatingGeneralValue.equals("on")));
                editor.putBoolean(getString(R.string.searchRatingMatureSetting), (selectedRatingMatureValue.equals("on")));
                editor.putBoolean(getString(R.string.searchRatingAdultSetting), (selectedRatingAdultValue.equals("on")));
                editor.putBoolean(getString(R.string.searchTypeArtSetting), (selectedTypeArtValue.equals("on")));
                editor.putBoolean(getString(R.string.searchTypeMusicSetting), (selectedTypeMusicValue.equals("on")));
                editor.putBoolean(getString(R.string.searchTypeFlashSetting), (selectedTypeFlashValue.equals("on")));
                editor.putBoolean(getString(R.string.searchTypeStorySetting), (selectedTypeStoryValue.equals("on")));
                editor.putBoolean(getString(R.string.searchTypePhotoSetting), (selectedTypePhotoValue.equals("on")));
                editor.putBoolean(getString(R.string.searchTypePoetrySetting), (selectedTypePoetryValue.equals("on")));
                editor.putString(getString(R.string.searchModeSetting), selectedModeValue);
                editor.apply();
                editor.commit();
            }

            resetRecycler();
        }
    }

    protected void updateUIElements() {
        savedSearchRecyclerView.setLayoutManager(saveLayoutManager);
        savedMAdapter = new savedSearchListAdapter(savedMDataSet, getActivity());
        savedSearchRecyclerView.setAdapter(savedMAdapter);
    }

    protected void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setPage(Integer.toString(page.getPage() + 1));
                fetchPageData();
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        savedSearchRecyclerView.post(() -> savedMAdapter.notifyDataSetChanged());

        search.setOnClickListener(v -> {
            searchOptionsScrollView.setVisibility(View.GONE);
            savedSearchRecyclerView.setVisibility(View.GONE);
            saveCurrentSettings();
            updateUIElements();
            updateUIElementListeners(rootView);
            ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            swipeRefreshLayout.setVisibility(View.VISIBLE);

            ((mainActivity)requireActivity()).drawerFragmentPush(this.getClass().getName(), getSearchParamaterObject().toString());

            setFabSearchMode();
        });

        fab.setOnLongClickListener(v -> {
            resetRecycler();
            return false;
        });

        savedSearches.setOnClickListener(v -> {
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
        });

        searchSettings.setOnClickListener(v -> {
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
        });

        saveSearch.setOnClickListener(v -> {
            saveCurrentSearch();
            loadSavedSearches();
            saveCurrentSettings();
            updateUIElements();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayout(), container, false);
        getElements(rootView);
        initPages();
        fetchPageData();
        loadSavedSearches();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }

    private int getRecyclerFirstItem() {
        int[] firstVisibleItems = null;
        firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
        if (firstVisibleItems != null && firstVisibleItems.length > 0) {
            return firstVisibleItems[0];
        }

        return -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedSearch = ((mainActivity) requireActivity()).getSearchSelected();
        searchParamaters = ((mainActivity) requireActivity()).getSearchParamaters();

        if (selectedSearch == null && searchParamaters == null) {
            Context context = requireActivity();
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

            if (sharedPref.getBoolean(getString(R.string.cachedSearchStateSetting), open.furaffinity.client.fragmentDrawers.settings.cachedSearchDefault)) {
                long sessionTimestamp = sharedPref.getLong(getString(R.string.searchSessionTimestamp), 0);
                long sessionInvalidateCachedTime = sharedPref.getInt(getString(R.string.InvalidateCachedSearchTimeSetting), settings.InvalidateCachedSearchTimeDefault);
                sessionInvalidateCachedTime = sessionInvalidateCachedTime * 60;//convert min to seconds

                long currentTimestamp = Instant.now().getEpochSecond();
                long minTimestamp = currentTimestamp - sessionInvalidateCachedTime;

                if (sessionTimestamp >= minTimestamp && sessionTimestamp <= currentTimestamp) {
                    pageCacheCheckResultCount = sharedPref.getInt(context.getString(R.string.InvalidateCachedSearchAfterSetting), settings.InvalidateCachedSearchAfterDefault);

                    String mDataSetString = sharedPref.getString(context.getString(R.string.searchSessionDataSet), null);
                    pageNumber = sharedPref.getInt(context.getString(R.string.searchSessionPage), -1);
                    recyclerViewPosition = sharedPref.getInt(context.getString(R.string.searchSessionRecyclerView), -1);
                    query = sharedPref.getString(context.getString(R.string.searchSessionQuery), null);

                    if (mDataSetString != null) {
                        mDataSet = (List<HashMap<String, String>>) open.furaffinity.client.utilities.serialization.deSearilizeFromString(mDataSetString);
                    } else {
                        isCacheInitialized = true;
                    }
                } else {
                    isCacheInitialized = true;
                }
            } else {
                isCacheInitialized = true;
            }
        } else {
            isCacheInitialized = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDataSet != null && page != null && recyclerView != null) {
            Context context = requireActivity();
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            if (sharedPref.getBoolean(getString(R.string.cachedSearchStateSetting), settings.cachedSearchDefault)) {
                editor.putLong(context.getString(R.string.searchSessionTimestamp), Instant.now().getEpochSecond());
                editor.putString(context.getString(R.string.searchSessionDataSet), open.furaffinity.client.utilities.serialization.searilizeToString((Serializable) mDataSet));
                editor.putInt(context.getString(R.string.searchSessionPage), page.getPage());
                editor.putInt(context.getString(R.string.searchSessionRecyclerView), getRecyclerFirstItem());
                editor.putString(context.getString(R.string.searchSessionQuery), page.getCurrentQuery());
            } else {
                editor.putLong(context.getString(R.string.searchSessionTimestamp), 0);
                editor.putString(context.getString(R.string.searchSessionDataSet), open.furaffinity.client.utilities.serialization.searilizeToString((Serializable) mDataSet));
                editor.putInt(context.getString(R.string.searchSessionPage), page.getPage());
                editor.putInt(context.getString(R.string.searchSessionRecyclerView), getRecyclerFirstItem());
                editor.putString(context.getString(R.string.searchSessionQuery), page.getCurrentQuery());
            }

            editor.apply();
        }
    }

}
