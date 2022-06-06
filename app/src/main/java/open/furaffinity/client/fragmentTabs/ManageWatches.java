package open.furaffinity.client.fragmentTabs;

import android.view.View;
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
import open.furaffinity.client.adapter.WatchListAdapter;
import open.furaffinity.client.pages.ControlsBuddyList;

public class ManageWatches extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<WatchListAdapter.ViewHolder> mAdapter;
    private ControlsBuddyList page;
    private int loadingStopCounter = 3;
    private boolean isLoading = false;

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager =
            new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    protected void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new ControlsBuddyList(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    protected void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new WatchListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new ControlsBuddyList(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                List<HashMap<String, String>> pageResults =
                    ((ControlsBuddyList) abstractPage).getPageResults();

                int curSize = mAdapter.getItemCount();

                if (pageResults.size() == 0 && loadingStopCounter > 0) {
                    loadingStopCounter--;
                }
                else {
                    //Deduplicate results
                    List<String> newPostPaths =
                        pageResults.stream().map(currentMap -> currentMap.get("userLink"))
                            .collect(Collectors.toList());
                    List<String> oldPostPaths =
                        mDataSet.stream().map(currentMap -> currentMap.get("userLink"))
                            .collect(Collectors.toList());
                    newPostPaths.removeAll(oldPostPaths);
                    pageResults = pageResults.stream()
                        .filter(currentMap -> newPostPaths.contains(currentMap.get("userLink")))
                        .collect(Collectors.toList());
                    mDataSet.addAll(pageResults);
                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());
                }

                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                loadingStopCounter--;
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data for watches", Toast.LENGTH_SHORT)
                    .show();
            }
        });
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);
    }
}
