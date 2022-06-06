package open.furaffinity.client.fragmentTabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.adapter.ImageListAdapter;
import open.furaffinity.client.fragmentDrawers.Settings;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.Gallery;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.MessageIds;
import open.furaffinity.client.utilities.UiControls;

public class UserGallery extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ImageListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private LinearLayout controls;
    private Spinner folderSpinner;
    private Gallery page;
    private int loadingStopCounter = 3;
    private boolean isInitialized = false;
    private boolean isLoading = false;
    private String pagePath = null;
    private HashMap<String, String> folderList = new HashMap<>();

    private final AbstractPage.PageListener pageListener = new AbstractPage.PageListener() {
        @Override public void requestSucceeded(AbstractPage abstractPage) {
            List<HashMap<String, String>> pageResults = ((Gallery) abstractPage).getPageResults();

            int curSize = mAdapter.getItemCount();

            if (pageResults.size() == 0 && loadingStopCounter > 0) {
                loadingStopCounter--;
            }

            //Deduplicate results
            List<String> newPostPaths =
                pageResults.stream().map(currentMap -> currentMap.get("postPath"))
                    .collect(Collectors.toList());
            List<String> oldPostPaths =
                mDataSet.stream().map(currentMap -> currentMap.get("postPath"))
                    .collect(Collectors.toList());
            newPostPaths.removeAll(oldPostPaths);
            pageResults = pageResults.stream()
                .filter(currentMap -> newPostPaths.contains(currentMap.get("postPath")))
                .collect(Collectors.toList());
            mDataSet.addAll(pageResults);
            mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());

            if (!isInitialized) {
                folderList = ((Gallery) abstractPage).getFolderResults();

                if (folderList.size() > 0) {
                    folderSpinner.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    UiControls.spinnerSetAdapter(requireContext(), folderSpinner, folderList,
                        ((Gallery) abstractPage).getPagePath(), true, false);

                    controls.removeView(folderSpinner);
                    controls.addView(folderSpinner);
                }

                isInitialized = true;
            }

            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override public void requestFailed(AbstractPage abstractPage) {
            loadingStopCounter--;
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed to load data for gallery", Toast.LENGTH_SHORT)
                .show();
        }
    };

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view;
    }

    protected void getElements(View rootView) {
        SharedPreferences sharedPref =
            requireActivity().getSharedPreferences(getString(R.string.settingsFile),
                Context.MODE_PRIVATE);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(
            sharedPref.getInt(getString(R.string.imageListColumns),
                Settings.imageListColumnsDefault),
            sharedPref.getInt(getString(R.string.imageListOrientation),
                Settings.imageListOrientationDefault));

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        controls = rootView.findViewById(R.id.controls);
        folderSpinner = new Spinner(requireContext());
    }

    protected void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new Gallery(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        loadingStopCounter = 3;
        page = new Gallery(getActivity(), pageListener, pagePath);
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    protected void initPages() {
        if (getArguments() != null) {
            pagePath = getArguments().getString(MessageIds.pagePath_MESSAGE);

            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            mAdapter = new ImageListAdapter(mDataSet, requireActivity(), requireActivity());
            recyclerView.setAdapter(mAdapter);

            page = new Gallery(getActivity(), pageListener, pagePath);
        }
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener =
            new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
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
                String newPagePath = ((KvPair) folderSpinner.getItemAtPosition(position)).getKey();
                if (!newPagePath.equals(pagePath)) {
                    pagePath = newPagePath;
                    folderList = new HashMap<>();
                    resetRecycler();
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}