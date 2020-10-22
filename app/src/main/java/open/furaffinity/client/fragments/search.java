package open.furaffinity.client.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;

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
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.imageListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class search extends Fragment {
    private static final String TAG = search.class.getName();

    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

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

    private FloatingActionButton fab;

    private webClient webClient;
    private loginTest loginTest;
    private open.furaffinity.client.pages.search page;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private boolean loadedMainActivitySearchQuery = false;

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);

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
    }

    private void loadCurrentSettings() {
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
            recyclerView.scrollTo(0, 0);
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();
            page = new open.furaffinity.client.pages.search(page);
            fetchPageData();
        }
    }

    private void updateUIElements() {
        loadCurrentSettings();

        if (loginTest.getIsLoggedIn()) {
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
            ScrollView searchOptionsScrollView = rootView.findViewById(R.id.searchOptionsScrollView);
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
                loadCurrentSettings();
                searchOptionsScrollView.setVisibility(View.VISIBLE);
            } else {
                searchOptionsScrollView.setVisibility(View.GONE);
                saveCurrentSettings();
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                recyclerView.setVisibility(View.VISIBLE);
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
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
