package open.furaffinity.client.fragmentsOld;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.imageListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
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
    private open.furaffinity.client.pagesOld.gallery page;

    private int loadingStopCounter = 3;
    private String pagePath = null;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private HashMap<String, String> folderList = new HashMap<>();

    private void getElements(View rootView) {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(sharedPref.getInt(getString(R.string.imageListColumns), settings.imageListColumnsDefault), sharedPref.getInt(getString(R.string.imageListOrientation), settings.imageListOrientationDefault));

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        controls = rootView.findViewById(R.id.controls);
        folderSpinner = new Spinner(requireContext());
    }

    private void initClientAndPage() {
        webClient = new webClient(this.getActivity());
        page = new open.furaffinity.client.pagesOld.gallery(pagePath);
    }

    private void fetchPageData() {
        if (!(loadingStopCounter == 0)) {
            page = new open.furaffinity.client.pagesOld.gallery(page);
            try {
                page.execute(webClient).get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "loadNextPage: ", e);
            }

            List<HashMap<String, String>> pageResults = page.getPageResults();

            if (pageResults.size() == 0 && loadingStopCounter > 0) {
                loadingStopCounter--;
            }

            //Deduplicate results
            List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
            List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
            newPostPaths.removeAll(oldPostPaths);
            pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("postPath"))).collect(Collectors.toList());
            mDataSet.addAll(pageResults);

            folderList = page.getFolderResults();
        }
    }

    private void updateUIElements() {
        if (folderList.size() > 0) {
            folderSpinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            uiControls.spinnerSetAdapter(requireContext(), folderSpinner, folderList, page.getPagePath(), true, false);

            controls.removeView(folderSpinner);
            controls.addView(folderSpinner);
        }

//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new imageListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initClientAndPage();

                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                mAdapter.notifyDataSetChanged();
                endlessRecyclerViewScrollListener.resetState();
                page = new open.furaffinity.client.pagesOld.gallery(pagePath);
                fetchPageData();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setNextPage();
                int curSize = mAdapter.getItemCount();
                fetchPageData();
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
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
                    mDataSet = new ArrayList<>();
                    folderList = new HashMap<>();
                    getElements(rootView);
                    initClientAndPage();
                    fetchPageData();
                    updateUIElements();
                    updateUIElementListeners(rootView);
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
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}