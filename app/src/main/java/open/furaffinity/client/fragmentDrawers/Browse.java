package open.furaffinity.client.fragmentDrawers;

import static open.furaffinity.client.utilities.Serialization.deSearilizeFromString;
import static open.furaffinity.client.utilities.Serialization.searilizeToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.adapter.ImageListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.AdRetrieval;
import open.furaffinity.client.pages.LoginCheck;
import open.furaffinity.client.sqlite.BrowseContract;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class Browse extends AbstractAppFragment {
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private TableLayout settingsTableLayout;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ImageListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private Spinner browseCatSpinner;
    private Spinner browseAtypeSpinner;
    private Spinner browseSpeciesSpinner;
    private Spinner browseGenderSpinner;
    private Spinner browsePerpageSpinner;
    private EditText browsePageEditText;
    private Switch browseRatingGeneralSwitch;
    private Switch browseRatingMatureSwitch;
    private Switch browseRatingAdultSwitch;

    private FloatingActionButton fab;

    private LoginCheck loginCheck;
    private open.furaffinity.client.pages.Browse page;

    private boolean isInitialized;
    private boolean isCacheInitialized;
    private boolean isLoading;
    private List<HashMap<String, String>> mDataSet;

    private String browseParamaters;

    private int recyclerViewPosition = -1;
    private int pageNumber = -1;
    private int pageCacheCheckResultCount;

    @Override protected int getLayout() {
        return R.layout.fragment_browse;
    }

    protected void getElements(View rootView) {
        final SharedPreferences sharedPref = requireActivity()
            .getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(
            sharedPref.getInt(getString(R.string.imageListColumns),
                Settings.imageListColumnsDefault),
            sharedPref.getInt(getString(R.string.imageListOrientation),
                Settings.imageListOrientationDefault));
        settingsTableLayout = rootView.findViewById(R.id.settingsTableLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        browseCatSpinner = rootView.findViewById(R.id.browseCatSpinner);
        browseAtypeSpinner = rootView.findViewById(R.id.browseAtypeSpinner);
        browseSpeciesSpinner = rootView.findViewById(R.id.browseSpeciesSpinner);
        browseGenderSpinner = rootView.findViewById(R.id.browseGenderSpinner);
        browsePerpageSpinner = rootView.findViewById(R.id.browsePerpageSpinner);
        browsePageEditText = rootView.findViewById(R.id.browsePageEditText);
        browseRatingGeneralSwitch = rootView.findViewById(R.id.browseRatingGeneralSwitch);
        browseRatingMatureSwitch = rootView.findViewById(R.id.browseRatingMatureSwitch);
        browseRatingAdultSwitch = rootView.findViewById(R.id.browseRatingAdultSwitch);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new open.furaffinity.client.pages.Browse(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

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
        final SharedPreferences sharedPref = requireActivity()
            .getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getString(R.string.saveBrowseState),
            Settings.saveBrowseStateDefault)) {
            page.setCat(sharedPref.getString(getString(R.string.browseCatSetting), ""));
            page.setAtype(sharedPref.getString(getString(R.string.browseAtypeSetting), ""));
            page.setSpecies(sharedPref.getString(getString(R.string.browseSpeciesSetting), ""));
            page.setGender(sharedPref.getString(getString(R.string.browseGenderSetting), ""));
            page.setPerpage(sharedPref.getString(getString(R.string.browsePerpageSetting), ""));
            page.setRatingGeneral(
                sharedPref.getBoolean(getString(R.string.browseRatingGeneralSetting), true));
            page.setRatingMature(
                sharedPref.getBoolean(getString(R.string.browseRatingMatureSetting), false));
            page.setRatingAdult(
                sharedPref.getBoolean(getString(R.string.browseRatingAdultSetting), false));
        }

        isInitialized = true;
    }

    protected void initPages() {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new ImageListAdapter(mDataSet, requireActivity(), requireActivity());
        recyclerView.setAdapter(mAdapter);

        if (recyclerViewPosition > -1) {
            staggeredGridLayoutManager.scrollToPosition(recyclerViewPosition);
        }

        loginCheck = new LoginCheck(requireActivity(), new AbstractPage.PageListener() {
            private void updateUIElements() {
                if (loginCheck.getIsLoggedIn() && loginCheck.getIsNSFWAllowed()) {
                    browseRatingMatureSwitch.setVisibility(View.VISIBLE);
                    browseRatingAdultSwitch.setVisibility(View.VISIBLE);
                }
                else {
                    browseRatingMatureSwitch.setVisibility(View.GONE);
                    browseRatingAdultSwitch.setVisibility(View.GONE);
                }
            }

            @Override public void requestSucceeded(AbstractPage abstractPage) {
                updateUIElements();
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                updateUIElements();
                Toast.makeText(getActivity(), "Failed to detect login", Toast.LENGTH_SHORT).show();
            }
        });

        loginCheck.execute();

        page = new open.furaffinity.client.pages.Browse(this.getActivity(),
            new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    if (!isInitialized) {
                        isLoading = false;
                        initCurrentSettings();
                        fab.setVisibility(View.VISIBLE);

                        if (isCacheInitialized || mDataSet == null || mDataSet.size() == 0) {
                            isCacheInitialized = true;
                            resetRecycler();
                        }
                        else {
                            fetchPageData();
                        }
                    }
                    else if (!isCacheInitialized) {
                        isLoading = false;
                        isCacheInitialized = true;

                        List<HashMap<String, String>> pageResults = page.getPageResults();
                        if (pageResults.size() > pageCacheCheckResultCount) {
                            pageResults = pageResults.subList(0, pageCacheCheckResultCount);
                        }

                        if (mDataSet.size() > 0 && pageResults.contains(mDataSet.get(0))) {
                            page.setPage(Integer.toString(pageNumber));
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        else {
                            resetRecycler();
                        }
                    }
                    else {
                        final open.furaffinity.client.pages.Browse browsePage =
                            ((open.furaffinity.client.pages.Browse) abstractPage);

                        new AdRetrieval(getActivity(),
                            new AbstractPage.PageListener() {
                                @Override
                                public void requestSucceeded(
                                    AbstractPage abstractPage) {
                                    List<HashMap<String, String>> pageResults =
                                        browsePage.getPageResults();
                                    final List<HashMap<String, String>> adResults =
                                        ((AdRetrieval) abstractPage).getAdData();

                                    final int curSize = mAdapter.getItemCount();

                                    // Deduplicate results
                                    final List<String> newPostPaths = pageResults.stream()
                                        .map(currentMap -> currentMap.get("postPath"))
                                        .collect(Collectors.toList());
                                    final List<String> oldPostPaths = mDataSet.stream()
                                        .map(currentMap -> currentMap.get("postPath"))
                                        .collect(Collectors.toList());
                                    newPostPaths.removeAll(oldPostPaths);
                                    pageResults = pageResults.stream().filter(
                                            currentMap -> {
                                                return newPostPaths.contains(currentMap.get(
                                                    "postPath"));
                                            })
                                        .collect(Collectors.toList());

                                    if (!adResults.isEmpty()) {
                                        final int totalAddItems = pageResults.size()
                                            + adResults.size();
                                        final int spacing = totalAddItems / adResults.size();

                                        for (int index = 0; index < adResults.size(); index++) {
                                            final int addPosition = (spacing * index) + index;
                                            if (addPosition <= pageResults.size()) {
                                                pageResults.add(addPosition, adResults.get(index));
                                            }
                                        }
                                    }

                                    mDataSet.addAll(pageResults);
                                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());

                                    isLoading = false;
                                    swipeRefreshLayout.setRefreshing(false);
                                }

                                @Override
                                public void requestFailed(
                                    AbstractPage abstractPage) {

                                }
                            }, browsePage.getAdZones()).execute();
                    }
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Failed to load data from browse page",
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private JSONObject getSearchParamaterObject() {
        final JSONObject result = new JSONObject();

        try {
            result.put(BrowseContract.browseItemEntry.COLUMN_NAME_CAT, page.getCurrentCat());
            result.put(BrowseContract.browseItemEntry.COLUMN_NAME_ATYPE, page.getCurrentAtype());
            result.put(BrowseContract.browseItemEntry.COLUMN_NAME_SPECIES,
                page.getCurrentSpecies());
            result.put(BrowseContract.browseItemEntry.COLUMN_NAME_GENDER, page.getCurrentGender());
            result.put(BrowseContract.browseItemEntry.COLUMN_NAME_PERPAGE,
                page.getCurrentPerpage());
            result.put(BrowseContract.browseItemEntry.COLUMN_NAME_RATINGGENERAL,
                (!page.getCurrentRatingGeneral().equals("")));
            result.put(BrowseContract.browseItemEntry.COLUMN_NAME_RATINGMATURE,
                (!page.getCurrentRatingMature().equals("")));
            result.put(BrowseContract.browseItemEntry.COLUMN_NAME_RATINGADULT,
                (!page.getCurrentRatingAdult().equals("")));
        }
        catch (JSONException exception) {
            exception.printStackTrace();
        }

        return result;
    }

    private void loadCurrentSettings() {
        if (browseParamaters != null) {
            try {
                final JSONObject loadedBrowseParamaters = new JSONObject(browseParamaters);

                for (Iterator<String> iterator = loadedBrowseParamaters.keys();
                     iterator.hasNext();) {
                    final String key = iterator.next();

                    switch (key) {
                        case BrowseContract.browseItemEntry.COLUMN_NAME_CAT:
                            page.setCat(loadedBrowseParamaters.getString(
                                BrowseContract.browseItemEntry.COLUMN_NAME_CAT));
                            break;
                        case BrowseContract.browseItemEntry.COLUMN_NAME_ATYPE:
                            page.setAtype(loadedBrowseParamaters.getString(
                                BrowseContract.browseItemEntry.COLUMN_NAME_ATYPE));
                            break;
                        case BrowseContract.browseItemEntry.COLUMN_NAME_SPECIES:
                            page.setSpecies(loadedBrowseParamaters.getString(
                                BrowseContract.browseItemEntry.COLUMN_NAME_SPECIES));
                            break;
                        case BrowseContract.browseItemEntry.COLUMN_NAME_GENDER:
                            page.setGender(loadedBrowseParamaters.getString(
                                BrowseContract.browseItemEntry.COLUMN_NAME_GENDER));
                            break;
                        case BrowseContract.browseItemEntry.COLUMN_NAME_PERPAGE:
                            page.setPerpage(loadedBrowseParamaters.getString(
                                BrowseContract.browseItemEntry.COLUMN_NAME_PERPAGE));
                            break;
                        case BrowseContract.browseItemEntry.COLUMN_NAME_RATINGGENERAL:
                            page.setRatingGeneral(loadedBrowseParamaters.getBoolean(
                                BrowseContract.browseItemEntry.COLUMN_NAME_RATINGGENERAL));
                            break;
                        case BrowseContract.browseItemEntry.COLUMN_NAME_RATINGMATURE:
                            page.setRatingMature(loadedBrowseParamaters.getBoolean(
                                BrowseContract.browseItemEntry.COLUMN_NAME_RATINGMATURE));
                            break;
                        case BrowseContract.browseItemEntry.COLUMN_NAME_RATINGADULT:
                            page.setRatingAdult(loadedBrowseParamaters.getBoolean(
                                BrowseContract.browseItemEntry.COLUMN_NAME_RATINGADULT));
                            break;
                        default:
                            break;
                    }
                }
            } catch (JSONException exception) {
                exception.printStackTrace();
            }

            browseParamaters = null;
        }

        UiControls.spinnerSetAdapter(requireContext(), browseCatSpinner, page.getCat(),
            page.getCurrentCat(), true, true);
        UiControls.spinnerSetAdapter(requireContext(), browseAtypeSpinner, page.getAtype(),
            page.getCurrentAtype(), true, true);
        UiControls.spinnerSetAdapter(requireContext(), browseSpeciesSpinner, page.getSpecies(),
            page.getCurrentSpecies(), true, true);
        UiControls.spinnerSetAdapter(requireContext(), browseGenderSpinner, page.getGender(),
            page.getCurrentGender(), true, true);
        UiControls.spinnerSetAdapter(requireContext(), browsePerpageSpinner, page.getPerpage(),
            page.getCurrentPerpage(), true, true);
        browsePageEditText.setText(page.getCurrentPage());
        browseRatingGeneralSwitch.setChecked((!page.getCurrentRatingGeneral().equals("")));

        // fa will ignore these in sfw mode anyways
        browseRatingMatureSwitch.setChecked(!page.getCurrentRatingMature().equals(""));
        browseRatingAdultSwitch.setChecked(!page.getCurrentRatingAdult().equals(""));
    }

    private void saveCurrentSettings() {
        boolean valueChanged = false;

        final String selectedCatValue = ((KvPair) browseCatSpinner.getSelectedItem())
            .getKey();
        if (!page.getCurrentCat().equals(selectedCatValue)) {
            page.setCat(selectedCatValue);
            valueChanged = true;
        }

        final String selectedAtypeValue = ((KvPair) browseAtypeSpinner.getSelectedItem())
            .getKey();
        if (!page.getCurrentAtype().equals(selectedAtypeValue)) {
            page.setAtype(selectedAtypeValue);
            valueChanged = true;
        }

        final String selectedSpeciesValue = ((KvPair) browseSpeciesSpinner.getSelectedItem())
            .getKey();
        if (!page.getCurrentSpecies().equals(selectedSpeciesValue)) {
            page.setSpecies(selectedSpeciesValue);
            valueChanged = true;
        }

        final String selectedGenderValue = ((KvPair) browseGenderSpinner.getSelectedItem())
            .getKey();
        if (!page.getCurrentGender().equals(selectedGenderValue)) {
            page.setGender(selectedGenderValue);
            valueChanged = true;
        }

        final String selectedPerpageValue = ((KvPair) browsePerpageSpinner.getSelectedItem())
            .getKey();
        if (!page.getCurrentPerpage().equals(selectedPerpageValue)) {
            page.setPerpage(selectedPerpageValue);
            valueChanged = true;
        }

        final String selectedRatingGeneralValue;
        if (browseRatingGeneralSwitch.isChecked()) {
            selectedRatingGeneralValue = "on";
        }
        else {
            selectedRatingGeneralValue = "";
        }
        if (!page.getCurrentRatingGeneral().equals(selectedRatingGeneralValue)) {
            page.setRatingGeneral(browseRatingGeneralSwitch.isChecked());
            valueChanged = true;
        }

        final String selectedRatingMatureValue;
        if (browseRatingMatureSwitch.isChecked()) {
            selectedRatingMatureValue = "on";
        }
        else {
            selectedRatingMatureValue = "";
        }
        if (!page.getCurrentRatingMature().equals(selectedRatingMatureValue)) {
            page.setRatingMature(browseRatingMatureSwitch.isChecked());
            valueChanged = true;
        }

        final String selectedRatingAdultValue;
        if (browseRatingAdultSwitch.isChecked()) {
            selectedRatingAdultValue = "on";
        }
        else {
            selectedRatingAdultValue = "";
        }
        if (!page.getCurrentRatingAdult().equals(selectedRatingAdultValue)) {
            page.setRatingAdult(browseRatingAdultSwitch.isChecked());
            valueChanged = true;
        }

        if (valueChanged) {
            browsePageEditText.setText("1");
        }
        if (!page.getCurrentPage().equals(browsePageEditText.getText().toString())) {
            page.setPage(browsePageEditText.getText().toString());
            valueChanged = true;
        }

        if (valueChanged) {
            final SharedPreferences sharedPref = requireActivity()
                .getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

            if (sharedPref.getBoolean(getString(R.string.saveBrowseState),
                Settings.saveBrowseStateDefault)) {
                final SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.browseCatSetting), selectedCatValue);
                editor.putString(getString(R.string.browseAtypeSetting), selectedAtypeValue);
                editor.putString(getString(R.string.browseSpeciesSetting), selectedSpeciesValue);
                editor.putString(getString(R.string.browseGenderSetting), selectedGenderValue);
                editor.putString(getString(R.string.browsePerpageSetting), selectedPerpageValue);
                editor.putBoolean(getString(R.string.browseRatingGeneralSetting),
                    selectedRatingGeneralValue.equals("on"));
                editor.putBoolean(getString(R.string.browseRatingMatureSetting),
                    selectedRatingMatureValue.equals("on"));
                editor.putBoolean(getString(R.string.browseRatingAdultSetting),
                    selectedRatingAdultValue.equals("on"));
                editor.apply();
            }

            resetRecycler();
        }
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener =
            new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                @Override
                public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                    if (!isLoading) {
                        page.setPage(Integer.toString(page.getPage() + 1));
                        fetchPageData();
                    }
                }
            };

        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        fab.setOnClickListener(view -> {
            if (settingsTableLayout.getVisibility() == View.VISIBLE) {
                settingsTableLayout.setVisibility(View.GONE);
                saveCurrentSettings();
                ((MainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(),
                    getSearchParamaterObject().toString());
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
            else {
                swipeRefreshLayout.setVisibility(View.GONE);
                loadCurrentSettings();
                settingsTableLayout.setVisibility(View.VISIBLE);
            }
        });

        fab.setOnLongClickListener(view -> {
            if (settingsTableLayout.getVisibility() == View.VISIBLE) {
                resetRecycler();
            }
            return false;
        });
    }

    private int getRecyclerFirstItem() {
        int result = -1;
        int[] firstVisibleItems = null;
        firstVisibleItems =
            staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
        if (firstVisibleItems != null && firstVisibleItems.length > 0) {
            result = firstVisibleItems[0];
        }
        return result;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        browseParamaters = ((MainActivity) requireActivity()).getBrowseParamaters();

        if (browseParamaters == null) {
            final Context context = requireActivity();
            final SharedPreferences sharedPref = requireActivity()
                .getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

            if (sharedPref.getBoolean(getString(R.string.cachedBrowseStateSetting),
                Settings.cachedBrowseDefault)) {
                final long sessionTimestamp =
                    sharedPref.getLong(getString(R.string.browseSessionTimestamp), 0);
                long sessionInvalidateCachedTime =
                    sharedPref.getInt(getString(R.string.InvalidateCachedBrowseTimeSetting),
                        Settings.InvalidateCachedBrowseTimeDefault);
                // convert min to seconds
                sessionInvalidateCachedTime =
                    sessionInvalidateCachedTime * 60;

                final long currentTimestamp = Instant.now().getEpochSecond();
                final long minTimestamp = currentTimestamp - sessionInvalidateCachedTime;

                if (sessionTimestamp >= minTimestamp && sessionTimestamp <= currentTimestamp) {
                    pageCacheCheckResultCount = sharedPref.getInt(
                        context.getString(R.string.InvalidateCachedBrowseAfterSetting),
                        Settings.InvalidateCachedBrowseAfterDefault);

                    final String mDataSetString =
                        sharedPref.getString(context.getString(R.string.browseSessionDataSet),
                            null);
                    pageNumber =
                        sharedPref.getInt(context.getString(R.string.browseSessionPage), -1);
                    recyclerViewPosition =
                        sharedPref.getInt(context.getString(R.string.browseSessionRecyclerView),
                            -1);

                    if (mDataSetString != null) {
                        mDataSet =
                            (List<HashMap<String, String>>) deSearilizeFromString(mDataSetString);
                    }
                    else {
                        isCacheInitialized = true;
                    }
                }
                else {
                    isCacheInitialized = true;
                }
            }
            else {
                isCacheInitialized = true;
            }
        }
        else {
            isCacheInitialized = true;
        }
    }

    @Override public void onStop() {
        super.onStop();
        if (mDataSet != null && page != null && recyclerView != null) {
            final Context context = requireActivity();
            final SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.settingsFile),
                    Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();

            if (sharedPref.getBoolean(getString(R.string.cachedBrowseStateSetting),
                Settings.cachedBrowseDefault)) {
                editor.putLong(context.getString(R.string.browseSessionTimestamp),
                    Instant.now().getEpochSecond());
                editor.putString(context.getString(R.string.browseSessionDataSet),
                    searilizeToString(
                        (Serializable) mDataSet));
                editor.putInt(context.getString(R.string.browseSessionPage), page.getPage());
                editor.putInt(context.getString(R.string.browseSessionRecyclerView),
                    getRecyclerFirstItem());
            }
            else {
                editor.putLong(context.getString(R.string.browseSessionTimestamp), 0);
                editor.putString(context.getString(R.string.browseSessionDataSet), "");
                editor.putInt(context.getString(R.string.browseSessionPage), -1);
                editor.putInt(context.getString(R.string.browseSessionRecyclerView), -1);
            }

            editor.apply();
        }
    }
}
