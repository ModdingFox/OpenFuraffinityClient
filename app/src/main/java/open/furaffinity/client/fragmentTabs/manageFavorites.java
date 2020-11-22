package open.furaffinity.client.fragmentTabs;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.adapter.manageImageListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.gallery;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

public class manageFavorites extends Fragment {
    private static final String TAG = manageFavorites.class.getName();

    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private fabCircular fab;
    private FloatingActionButton removeSelected;

    private webClient webClient;
    private gallery page;

    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private String pagePath = null;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

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

            fab.setVisibility(View.VISIBLE);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void requestFailed(abstractPage abstractPage) {
            loadingStopCounter--;
            fab.setVisibility(View.GONE);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed to load data for favorites", Toast.LENGTH_SHORT).show();
        }
    };

    private void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        removeSelected = new FloatingActionButton(getContext());

        removeSelected.setImageResource(R.drawable.ic_menu_delete);
        removeSelected.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(removeSelected);

        fab.addButton(removeSelected, 1.5f, 270);
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
        ((manageImageListAdapter) mAdapter).clearChecked();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    private void initPages() {
        webClient = new webClient(requireContext());

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new manageImageListAdapter(mDataSet, getActivity());
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

        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        removeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                HashMap<String, String> params = new HashMap<>();
                params.put("do", "delete");

                for (int i = 0; i < elements.size(); i++) {
                    params.put("favorites[" + Integer.toString(i) + "]", elements.get(i));
                }

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
                            return null;
                        }
                    }.execute(webClient).get();

                resetRecycler();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not un fav post: ", e);
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view_with_fab, container, false);
        pagePath = "/controls/favorites/";
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
