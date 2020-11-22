package open.furaffinity.client.fragmentTabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.adapter.imageListAdapter;
import open.furaffinity.client.fragmentDrawers.settings;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.gallery;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class userGallery extends Fragment {
    private static final String TAG = userGallery.class.getName();

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private LinearLayout controls;
    private Spinner folderSpinner;

    private webClient webClient;
    private gallery page;

    private int loadingStopCounter = 3;
    private boolean isInitialized = false;
    private boolean isLoading = false;
    private String pagePath = null;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private HashMap<String, String> folderList = new HashMap<>();

    private abstractPage.pageListener pageListener = new abstractPage.pageListener() {
        @Override
        public void requestSucceeded(abstractPage abstractPage) {
            List<HashMap<String, String>> pageResults = ((gallery)abstractPage).getPageResults();

            int curSize = mAdapter.getItemCount();

            if (pageResults.size() == 0 && loadingStopCounter > 0) {
                loadingStopCounter--;
            }

            //Deduplicate results
            List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
            List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
            newPostPaths.removeAll(oldPostPaths);
            pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("postPath"))).collect(Collectors.toList());
            mDataSet.addAll(pageResults);
            mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());

            if(!isInitialized) {
                folderList = ((gallery) abstractPage).getFolderResults();

                if (folderList.size() > 0) {
                    folderSpinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    uiControls.spinnerSetAdapter(requireContext(), folderSpinner, folderList, ((gallery) abstractPage).getPagePath(), true, false);

                    controls.removeView(folderSpinner);
                    controls.addView(folderSpinner);
                }

                isInitialized = true;
            }

            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void requestFailed(abstractPage abstractPage) {
            loadingStopCounter--;
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed to load data for gallery", Toast.LENGTH_SHORT).show();
        }
    };

    private void getElements(View rootView) {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(sharedPref.getInt(getString(R.string.imageListColumns), settings.imageListColumnsDefault), sharedPref.getInt(getString(R.string.imageListOrientation), settings.imageListOrientationDefault));

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        controls = rootView.findViewById(R.id.controls);
        folderSpinner = new Spinner(requireContext());
    }

    private void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new gallery(page);
            page.execute();
        }
    }

    private void resetRecycler() {
        loadingStopCounter = 3;
        page = new gallery(getActivity(), pageListener, pagePath);
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    private void initPages() {
        webClient = new webClient(this.getActivity());

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new imageListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new gallery(getActivity(), pageListener, pagePath);
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
                page.setNextPage();
                fetchPageData();
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        folderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newPagePath = ((kvPair) folderSpinner.getItemAtPosition(position)).getKey();
                if (newPagePath != pagePath) {
                    pagePath = newPagePath;
                    folderList = new HashMap<>();
                    resetRecycler();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view, container, false);
        pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}