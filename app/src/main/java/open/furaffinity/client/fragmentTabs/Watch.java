package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.adapter.StringListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.WatchList;
import open.furaffinity.client.utilities.MessageIds;

public class Watch extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<StringListAdapter.ViewHolder> mAdapter;
    @SuppressWarnings("FieldCanBeLocal") private EndlessRecyclerViewScrollListener
        endlessRecyclerViewScrollListener;
    private Button button;
    private WatchList page;
    private int loadingStopCounter = 3;
    private boolean isFirstLoad = true;

    @Override protected int getLayout() {
        return R.layout.fragment_watch;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);

        button = rootView.findViewById(R.id.button);
    }

    protected void fetchPageData() {
        if (isFirstLoad) {
            if (getArguments() != null) {
                mDataSet.addAll(WatchList.processWatchList(
                    getArguments().getString(MessageIds.userWatchRecent_MESSAGE), true));
                mAdapter.notifyDataSetChanged();
                isFirstLoad = false;
            }
        }
        else {
            page = new WatchList(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    protected void initPages() {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new StringListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        if (getArguments() != null) {
            page = new WatchList(getContext(), new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    List<HashMap<String, String>> pageResults =
                        ((WatchList) abstractPage).getPageResults();

                    int curSize = mAdapter.getItemCount();

                    if (pageResults.size() == 0 && loadingStopCounter > 0) {
                        loadingStopCounter--;
                    }

                    //Deduplicate results
                    List<String> newPostPaths =
                        pageResults.stream().map(currentMap -> currentMap.get("item"))
                            .collect(Collectors.toList());
                    List<String> oldPostPaths =
                        mDataSet.stream().map(currentMap -> currentMap.get("item"))
                            .collect(Collectors.toList());
                    newPostPaths.removeAll(oldPostPaths);
                    pageResults = pageResults.stream()
                        .filter(currentMap -> newPostPaths.contains(currentMap.get("item")))
                        .collect(Collectors.toList());
                    mDataSet.addAll(pageResults);
                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to load data for watch list",
                        Toast.LENGTH_SHORT).show();
                }
            }, getArguments().getString(MessageIds.pagePath_MESSAGE));
        }
    }

    protected void updateUiElementListeners(View rootView) {
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setPage(Integer.toString(page.getPage() + 1));
                fetchPageData();
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        button.setOnClickListener(v -> {
            button.setVisibility(View.GONE);
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
            fetchPageData();
        });
    }

    @Override public void onResume() {
        super.onResume();
    }
}