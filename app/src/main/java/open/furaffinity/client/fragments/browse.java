package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.imageListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class browse extends Fragment {
    private static final String TAG = open.furaffinity.client.fragments.browse.class.getName();

    LinearLayoutManager layoutManager;

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
    private open.furaffinity.client.pages.browse page;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

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
        webClient = new webClient(this.getActivity());
        page = new open.furaffinity.client.pages.browse();
    }

    private void fetchPageData() {
        page = new open.furaffinity.client.pages.browse(page);
        try {
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
    }

    private void updateUIElements() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new imageListAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);
    }

    private void loadCurrentSettings() {
        uiControls.spinnerSetAdapter(requireContext(), browseCatSpinner, page.getCat(), page.getCurrentCat(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browseAtypeSpinner, page.getAtype(), page.getCurrentAtype(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browseSpeciesSpinner, page.getSpecies(), page.getCurrentSpecies(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browseGenderSpinner, page.getGender(), page.getCurrentGender(), true, true);
        uiControls.spinnerSetAdapter(requireContext(), browsePerpageSpinner, page.getPerpage(), page.getCurrentPerpage(), true, true);
        browsePageEditText.setText(page.getCurrentPage());
        browseRatingGeneralSwitch.setChecked((!page.getCurrentRatingGeneral().equals("")));
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
            recyclerView.scrollTo(0, 0);
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();
            page = new open.furaffinity.client.pages.browse(page);
            fetchPageData();
        }
    }

    private void updateUIElementListeners(View rootView) {
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

        fab.setOnClickListener(view ->
        {
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
            TableLayout settingsTableLayout = rootView.findViewById(R.id.settingsTableLayout);
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
                loadCurrentSettings();
                settingsTableLayout.setVisibility(View.VISIBLE);
            } else {
                settingsTableLayout.setVisibility(View.GONE);
                saveCurrentSettings();
                recyclerView.setVisibility(View.VISIBLE);
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
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
