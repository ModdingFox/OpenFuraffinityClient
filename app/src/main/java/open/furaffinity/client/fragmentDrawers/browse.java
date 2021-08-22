package open.furaffinity.client.fragmentDrawers;

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
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.loginCheck;
import open.furaffinity.client.sqlite.browseContract;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;

public class browse extends appFragment {
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private TableLayout settingsTableLayout;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<imageListAdapter.ViewHolder> mAdapter;
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

    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.browse page;

    private boolean isInitialized = false;
    private boolean isCacheInitialized = false;
    private boolean isLoading = false;
    private List<HashMap<String, String>> mDataSet;

    private String browseParamaters = null;

    private int recyclerViewPosition = -1;
    private int pageNumber = -1;
    private int pageCacheCheckResultCount = 0;

    @Override
    protected int getLayout() {
        return R.layout.fragment_browse;
    }

    protected void getElements(View rootView) {
        Context context = requireActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(sharedPref.getInt(getString(R.string.imageListColumns), settings.imageListColumnsDefault), sharedPref.getInt(getString(R.string.imageListOrientation), settings.imageListOrientationDefault));
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
            page = new open.furaffinity.client.pages.browse(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

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
        Context context = requireActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getString(R.string.saveBrowseState), settings.saveBrowseStateDefault)) {
            page.setCat(sharedPref.getString(getString(R.string.browseCatSetting), ""));
            page.setAtype(sharedPref.getString(getString(R.string.browseAtypeSetting), ""));
            page.setSpecies(sharedPref.getString(getString(R.string.browseSpeciesSetting), ""));
            page.setGender(sharedPref.getString(getString(R.string.browseGenderSetting), ""));
            page.setPerpage(sharedPref.getString(getString(R.string.browsePerpageSetting), ""));
            page.setRatingGeneral(sharedPref.getBoolean(getString(R.string.browseRatingGeneralSetting), true));
            page.setRatingMature(sharedPref.getBoolean(getString(R.string.browseRatingMatureSetting), false));
            page.setRatingAdult(sharedPref.getBoolean(getString(R.string.browseRatingAdultSetting), false));
        }

        isInitialized = true;
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

        loginCheck = new loginCheck(requireActivity(), new abstractPage.pageListener() {
            private void updateUIElements() {
                if (loginCheck.getIsLoggedIn() && loginCheck.getIsNSFWAllowed()) {
                    browseRatingMatureSwitch.setVisibility(View.VISIBLE);
                    browseRatingAdultSwitch.setVisibility(View.VISIBLE);
                } else {
                    browseRatingMatureSwitch.setVisibility(View.GONE);
                    browseRatingAdultSwitch.setVisibility(View.GONE);
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

        page = new open.furaffinity.client.pages.browse(this.getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (!isInitialized) {
                    isLoading = false;
                    initCurrentSettings();
                    fab.setVisibility(View.VISIBLE);

                    if (isCacheInitialized || mDataSet == null || mDataSet.size() == 0) {
                        isCacheInitialized = true;
                        resetRecycler();
                    } else {
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
                        page.setPage(Integer.toString(pageNumber));
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        resetRecycler();
                    }
                } else {
                    final open.furaffinity.client.pages.browse browsePage = ((open.furaffinity.client.pages.browse) abstractPage);

                    new open.furaffinity.client.pages.adRetrieval(getActivity(), new abstractPage.pageListener() {
                        @Override
                        public void requestSucceeded(open.furaffinity.client.abstractClasses.abstractPage abstractPage) {
                            List <HashMap<String, String>> pageResults = browsePage.getPageResults();
                            List<HashMap<String, String>> adResults = ((open.furaffinity.client.pages.adRetrieval) abstractPage).getAdData();

                            int curSize = mAdapter.getItemCount();

                            //Deduplicate results
                            List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                            List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                            newPostPaths.removeAll(oldPostPaths);
                            pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("postPath"))).collect(Collectors.toList());

                            if(!adResults.isEmpty()){
                                int totalAddItems = pageResults.size() + adResults.size();
                                int spacing = totalAddItems/adResults.size();

                                for(int i = 0; i < adResults.size(); i++) {
                                    int addPosition = (spacing * i) + i;
                                    if(addPosition <= pageResults.size()) {
                                        pageResults.add(addPosition, adResults.get(i));
                                    }
                                }
                            }

                            mDataSet.addAll(pageResults);
                            mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());

                            isLoading = false;
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void requestFailed(open.furaffinity.client.abstractClasses.abstractPage abstractPage) {

                        }
                    }).execute();
                }
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data from browse page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private JSONObject getSearchParamaterObject() {
        JSONObject result = new JSONObject();

        try {
            result.put(browseContract.browseItemEntry.COLUMN_NAME_CAT, page.getCurrentCat());
            result.put(browseContract.browseItemEntry.COLUMN_NAME_ATYPE, page.getCurrentAtype());
            result.put(browseContract.browseItemEntry.COLUMN_NAME_SPECIES, page.getCurrentSpecies());
            result.put(browseContract.browseItemEntry.COLUMN_NAME_GENDER, page.getCurrentGender());
            result.put(browseContract.browseItemEntry.COLUMN_NAME_PERPAGE, page.getCurrentPerpage());
            result.put(browseContract.browseItemEntry.COLUMN_NAME_RATINGGENERAL, (!page.getCurrentRatingGeneral().equals("")));
            result.put(browseContract.browseItemEntry.COLUMN_NAME_RATINGMATURE, (!page.getCurrentRatingMature().equals("")));
            result.put(browseContract.browseItemEntry.COLUMN_NAME_RATINGADULT, (!page.getCurrentRatingAdult().equals("")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void loadCurrentSettings() {
        if(browseParamaters != null) {
            try {
                JSONObject loadedBrowseParamaters = new JSONObject(browseParamaters);

                for (Iterator<String> it = loadedBrowseParamaters.keys(); it.hasNext(); ) {
                    String key = it.next();

                    switch(key) {
                        case browseContract.browseItemEntry.COLUMN_NAME_CAT:
                            page.setCat(loadedBrowseParamaters.getString(browseContract.browseItemEntry.COLUMN_NAME_CAT));
                            break;
                        case browseContract.browseItemEntry.COLUMN_NAME_ATYPE:
                            page.setAtype(loadedBrowseParamaters.getString(browseContract.browseItemEntry.COLUMN_NAME_ATYPE));
                            break;
                        case browseContract.browseItemEntry.COLUMN_NAME_SPECIES:
                            page.setSpecies(loadedBrowseParamaters.getString(browseContract.browseItemEntry.COLUMN_NAME_SPECIES));
                            break;
                        case browseContract.browseItemEntry.COLUMN_NAME_GENDER:
                            page.setGender(loadedBrowseParamaters.getString(browseContract.browseItemEntry.COLUMN_NAME_GENDER));
                            break;
                        case browseContract.browseItemEntry.COLUMN_NAME_PERPAGE:
                            page.setPerpage(loadedBrowseParamaters.getString(browseContract.browseItemEntry.COLUMN_NAME_PERPAGE));
                            break;
                        case browseContract.browseItemEntry.COLUMN_NAME_RATINGGENERAL:
                            page.setRatingGeneral(loadedBrowseParamaters.getBoolean(browseContract.browseItemEntry.COLUMN_NAME_RATINGGENERAL));
                            break;
                        case browseContract.browseItemEntry.COLUMN_NAME_RATINGMATURE:
                            page.setRatingMature(loadedBrowseParamaters.getBoolean(browseContract.browseItemEntry.COLUMN_NAME_RATINGMATURE));
                            break;
                        case browseContract.browseItemEntry.COLUMN_NAME_RATINGADULT:
                            page.setRatingAdult(loadedBrowseParamaters.getBoolean(browseContract.browseItemEntry.COLUMN_NAME_RATINGADULT));
                            break;
                        default:
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            browseParamaters = null;
        }

        uiControls.spinnerSetAdapter(requireContext(), browseCatSpinner, page.getCat(), page.getCurrentCat(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browseAtypeSpinner, page.getAtype(), page.getCurrentAtype(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browseSpeciesSpinner, page.getSpecies(), page.getCurrentSpecies(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browseGenderSpinner, page.getGender(), page.getCurrentGender(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browsePerpageSpinner, page.getPerpage(), page.getCurrentPerpage(), true, true);
        browsePageEditText.setText(page.getCurrentPage());
        browseRatingGeneralSwitch.setChecked((!page.getCurrentRatingGeneral().equals("")));

        //fa will ignore these in sfw mode anyways
        browseRatingMatureSwitch.setChecked((!page.getCurrentRatingMature().equals("")));
        browseRatingAdultSwitch.setChecked((!page.getCurrentRatingAdult().equals("")));
    }

    private void saveCurrentSettings() {
        boolean valueChanged = false;

        String selectedCatValue = ((kvPair) browseCatSpinner.getSelectedItem()).getKey();
        if (!page.getCurrentCat().equals(selectedCatValue)) {
            page.setCat(selectedCatValue);
            valueChanged = true;
        }

        String selectedAtypeValue = ((kvPair) browseAtypeSpinner.getSelectedItem()).getKey();
        if (!page.getCurrentAtype().equals(selectedAtypeValue)) {
            page.setAtype(selectedAtypeValue);
            valueChanged = true;
        }

        String selectedSpeciesValue = ((kvPair) browseSpeciesSpinner.getSelectedItem()).getKey();
        if (!page.getCurrentSpecies().equals(selectedSpeciesValue)) {
            page.setSpecies(selectedSpeciesValue);
            valueChanged = true;
        }

        String selectedGenderValue = ((kvPair) browseGenderSpinner.getSelectedItem()).getKey();
        if (!page.getCurrentGender().equals(selectedGenderValue)) {
            page.setGender(selectedGenderValue);
            valueChanged = true;
        }

        String selectedPerpageValue = ((kvPair) browsePerpageSpinner.getSelectedItem()).getKey();
        if (!page.getCurrentPerpage().equals(selectedPerpageValue)) {
            page.setPerpage(selectedPerpageValue);
            valueChanged = true;
        }

        String selectedRatingGeneralValue = (browseRatingGeneralSwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentRatingGeneral().equals(selectedRatingGeneralValue)) {
            page.setRatingGeneral(browseRatingGeneralSwitch.isChecked());
            valueChanged = true;
        }

        String selectedRatingMatureValue = (browseRatingMatureSwitch.isChecked() ? ("on") : (""));
        if (!page.getCurrentRatingMature().equals(selectedRatingMatureValue)) {
            page.setRatingMature(browseRatingMatureSwitch.isChecked());
            valueChanged = true;
        }

        String selectedRatingAdultValue = (browseRatingAdultSwitch.isChecked() ? ("on") : (""));
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
            Context context = requireActivity();
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

            if (sharedPref.getBoolean(getString(R.string.saveBrowseState), settings.saveBrowseStateDefault)) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.browseCatSetting), selectedCatValue);
                editor.putString(getString(R.string.browseAtypeSetting), selectedAtypeValue);
                editor.putString(getString(R.string.browseSpeciesSetting), selectedSpeciesValue);
                editor.putString(getString(R.string.browseGenderSetting), selectedGenderValue);
                editor.putString(getString(R.string.browsePerpageSetting), selectedPerpageValue);
                editor.putBoolean(getString(R.string.browseRatingGeneralSetting), (selectedRatingGeneralValue.equals("on")));
                editor.putBoolean(getString(R.string.browseRatingMatureSetting), (selectedRatingMatureValue.equals("on")));
                editor.putBoolean(getString(R.string.browseRatingAdultSetting), (selectedRatingAdultValue.equals("on")));
                editor.apply();
            }

            resetRecycler();
        }
    }

    protected void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                if (!isLoading) {
                    page.setPage(Integer.toString(page.getPage() + 1));
                    fetchPageData();
                }
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        fab.setOnClickListener(view ->
        {
            if (settingsTableLayout.getVisibility() == View.VISIBLE) {
                settingsTableLayout.setVisibility(View.GONE);
                saveCurrentSettings();
                ((mainActivity)requireActivity()).drawerFragmentPush(this.getClass().getName(), getSearchParamaterObject().toString());
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                swipeRefreshLayout.setVisibility(View.GONE);
                loadCurrentSettings();
                settingsTableLayout.setVisibility(View.VISIBLE);
            }
        });

        fab.setOnLongClickListener(v -> {
            if (settingsTableLayout.getVisibility() == View.VISIBLE) {
                resetRecycler();
            }
            return false;
        });
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

        browseParamaters = ((mainActivity) requireActivity()).getBrowseParamaters();

        if (browseParamaters == null) {
            Context context = requireActivity();
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

            if (sharedPref.getBoolean(getString(R.string.cachedBrowseStateSetting), open.furaffinity.client.fragmentDrawers.settings.cachedBrowseDefault)) {
                long sessionTimestamp = sharedPref.getLong(getString(R.string.browseSessionTimestamp), 0);
                long sessionInvalidateCachedTime = sharedPref.getInt(getString(R.string.InvalidateCachedBrowseTimeSetting), settings.InvalidateCachedBrowseTimeDefault);
                sessionInvalidateCachedTime = sessionInvalidateCachedTime * 60;//convert min to seconds

                long currentTimestamp = Instant.now().getEpochSecond();
                long minTimestamp = currentTimestamp - sessionInvalidateCachedTime;

                if (sessionTimestamp >= minTimestamp && sessionTimestamp <= currentTimestamp) {
                    pageCacheCheckResultCount = sharedPref.getInt(context.getString(R.string.InvalidateCachedBrowseAfterSetting), settings.InvalidateCachedBrowseAfterDefault);

                    String mDataSetString = sharedPref.getString(context.getString(R.string.browseSessionDataSet), null);
                    pageNumber = sharedPref.getInt(context.getString(R.string.browseSessionPage), -1);
                    recyclerViewPosition = sharedPref.getInt(context.getString(R.string.browseSessionRecyclerView), -1);

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

            if (sharedPref.getBoolean(getString(R.string.cachedBrowseStateSetting), open.furaffinity.client.fragmentDrawers.settings.cachedBrowseDefault)) {
                editor.putLong(context.getString(R.string.browseSessionTimestamp), Instant.now().getEpochSecond());
                editor.putString(context.getString(R.string.browseSessionDataSet), open.furaffinity.client.utilities.serialization.searilizeToString((Serializable) mDataSet));
                editor.putInt(context.getString(R.string.browseSessionPage), page.getPage());
                editor.putInt(context.getString(R.string.browseSessionRecyclerView), getRecyclerFirstItem());
            } else {
                editor.putLong(context.getString(R.string.browseSessionTimestamp), 0);
                editor.putString(context.getString(R.string.browseSessionDataSet), "");
                editor.putInt(context.getString(R.string.browseSessionPage), -1);
                editor.putInt(context.getString(R.string.browseSessionRecyclerView), -1);
            }

            editor.apply();
        }
    }
}
