package open.furaffinity.client.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.page;
import open.furaffinity.client.adapter.imageListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class browse extends Fragment {
    private static final String TAG = open.furaffinity.client.fragments.browse.class.getName();

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private TableLayout settingsTableLayout;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
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

    private webClient webClient;
    private loginTest loginTest;
    private open.furaffinity.client.pages.browse page;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        Context context = getActivity();
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
    }

    private void initClientAndPage() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new imageListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        webClient = new webClient(this.getActivity());
        loginTest = new loginTest();

        page = new open.furaffinity.client.pages.browse(this.getActivity(), new page.pageListener() {
            @Override
            public void requestSucceeded() {
                List<HashMap<String, String>> pageResults = page.getPageResults();

                int curSize = mAdapter.getItemCount();

                //Deduplicate results
                List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                newPostPaths.removeAll(oldPostPaths);
                pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("postPath"))).collect(Collectors.toList());
                mDataSet.addAll(pageResults);
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
            }

            @Override
            public void requestFailed() {
                Toast.makeText(getActivity(), "Failed to load data from browse page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPageData() {
        loginTest = new loginTest();
        try {
            loginTest.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }

        page = new open.furaffinity.client.pages.browse(page);
        page.execute();
    }

    private void updateUIElements() {
        if (loginTest.getIsLoggedIn() && loginTest.getIsNSFWAllowed()) {
            browseRatingMatureSwitch.setVisibility(View.VISIBLE);
            browseRatingAdultSwitch.setVisibility(View.VISIBLE);
        } else {
            browseRatingMatureSwitch.setVisibility(View.GONE);
            browseRatingAdultSwitch.setVisibility(View.GONE);
        }
    }

    private void initCurrentSettings() {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getString(R.string.saveBrowseState), open.furaffinity.client.fragments.settings.saveBrowseStateDefault)) {
            fetchPageData();
            mDataSet.clear();
            page = new open.furaffinity.client.pages.browse(page);

            page.setCat(sharedPref.getString(getString(R.string.browseCatSetting), ""));
            page.setAtype(sharedPref.getString(getString(R.string.browseAtypeSetting), ""));
            page.setSpecies(sharedPref.getString(getString(R.string.browseSpeciesSetting), ""));
            page.setGender(sharedPref.getString(getString(R.string.browseGenderSetting), ""));
            page.setPerpage(sharedPref.getString(getString(R.string.browsePerpageSetting), ""));
            page.setRatingGeneral(sharedPref.getBoolean(getString(R.string.browseRatingGeneralSetting), true));
            page.setRatingMature(sharedPref.getBoolean(getString(R.string.browseRatingMatureSetting), false));
            page.setRatingAdult(sharedPref.getBoolean(getString(R.string.browseRatingAdultSetting), false));
        }
    }

    private void loadCurrentSettings() {
        uiControls.spinnerSetAdapter(requireContext(), browseCatSpinner, page.getCat(), page.getCurrentCat(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browseAtypeSpinner, page.getAtype(), page.getCurrentAtype(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browseSpeciesSpinner, page.getSpecies(), page.getCurrentSpecies(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browseGenderSpinner, page.getGender(), page.getCurrentGender(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browsePerpageSpinner, page.getPerpage(), page.getCurrentPerpage(), true, true);
        browsePageEditText.setText(page.getCurrentPage());
        browseRatingGeneralSwitch.setChecked((!page.getCurrentRatingGeneral().equals("")));

        if (loginTest.getIsLoggedIn()) {
            browseRatingMatureSwitch.setChecked((!page.getCurrentRatingMature().equals("")));
            browseRatingAdultSwitch.setChecked((!page.getCurrentRatingAdult().equals("")));
        } else {
            browseRatingMatureSwitch.setChecked(false);
            browseRatingAdultSwitch.setChecked(false);
        }
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
            Context context = getActivity();
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

            if (sharedPref.getBoolean(getString(R.string.saveBrowseState), open.furaffinity.client.fragments.settings.saveBrowseStateDefault)) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.browseCatSetting), selectedCatValue);
                editor.putString(getString(R.string.browseAtypeSetting), selectedAtypeValue);
                editor.putString(getString(R.string.browseSpeciesSetting), selectedSpeciesValue);
                editor.putString(getString(R.string.browseGenderSetting), selectedGenderValue);
                editor.putString(getString(R.string.browsePerpageSetting), selectedPerpageValue);
                editor.putBoolean(getString(R.string.browseRatingGeneralSetting), ((selectedRatingGeneralValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.browseRatingMatureSetting), ((selectedRatingMatureValue.equals("on")) ? (true) : (false)));
                editor.putBoolean(getString(R.string.browseRatingAdultSetting), ((selectedRatingAdultValue.equals("on")) ? (true) : (false)));
                editor.apply();
                editor.commit();
            }

            recyclerView.scrollTo(0, 0);
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();
            page = new open.furaffinity.client.pages.browse(page);
            fetchPageData();
        }
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
                initClientAndPage();
                fetchPageData();
                swipeRefreshLayout.setRefreshing(false);
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

        fab.setOnClickListener(view ->
        {
            if (settingsTableLayout.getVisibility() == View.VISIBLE) {
                settingsTableLayout.setVisibility(View.GONE);
                saveCurrentSettings();
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                swipeRefreshLayout.setVisibility(View.GONE);
                loadCurrentSettings();
                settingsTableLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse, container, false);
        getElements(rootView);
        initClientAndPage();
        initCurrentSettings();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
