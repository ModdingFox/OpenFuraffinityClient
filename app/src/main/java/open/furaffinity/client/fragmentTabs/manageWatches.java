package open.furaffinity.client.fragmentTabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.watchListAdapter;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsBuddyList;

public class manageWatches extends Fragment {
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private controlsBuddyList page;

    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new controlsBuddyList(page);
            page.execute();
        }
    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    private void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new watchListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new controlsBuddyList(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                List<HashMap<String, String>> pageResults = ((controlsBuddyList)abstractPage).getPageResults();

                int curSize = mAdapter.getItemCount();

                if (pageResults.size() == 0 && loadingStopCounter > 0) {
                    loadingStopCounter--;
                } else {
                    //Deduplicate results
                    List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("userLink")).collect(Collectors.toList());
                    List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("userLink")).collect(Collectors.toList());
                    newPostPaths.removeAll(oldPostPaths);
                    pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("userLink"))).collect(Collectors.toList());
                    mDataSet.addAll(pageResults);
                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());
                }

                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                loadingStopCounter--;
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data for watches", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecycler();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view, container, false);
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
