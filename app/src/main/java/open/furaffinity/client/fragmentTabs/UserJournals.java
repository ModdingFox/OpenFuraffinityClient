package open.furaffinity.client.fragmentTabs;

import android.view.View;
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
import open.furaffinity.client.adapter.JournalListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.Journals;
import open.furaffinity.client.utilities.MessageIds;

public class UserJournals extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<JournalListAdapter.ViewHolder> mAdapter;
    @SuppressWarnings("FieldCanBeLocal") private EndlessRecyclerViewScrollListener
        endlessRecyclerViewScrollListener;
    private Journals page;
    private int loadingStopCounter = 3;
    private boolean isLoading = false;

    @Override protected int getLayout() {
        return R.layout.fragment_recycler_view;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    protected void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            page = new Journals(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    protected void initPages() {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new JournalListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        if (getArguments() != null) {
            page = new Journals(getActivity(), new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    List<HashMap<String, String>> pageResults = page.getPageResults();

                    int curSize = mAdapter.getItemCount();

                    if (pageResults.size() == 0 && loadingStopCounter > 0) {
                        loadingStopCounter--;
                    }

                    //Deduplicate results
                    List<String> newPostPaths =
                        pageResults.stream().map(currentMap -> currentMap.get("journalPath"))
                            .collect(Collectors.toList());
                    List<String> oldPostPaths =
                        mDataSet.stream().map(currentMap -> currentMap.get("journalPath"))
                            .collect(Collectors.toList());
                    newPostPaths.removeAll(oldPostPaths);
                    pageResults = pageResults.stream()
                        .filter(currentMap -> newPostPaths.contains(currentMap.get("journalPath")))
                        .collect(Collectors.toList());
                    mDataSet.addAll(pageResults);
                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());

                    isLoading = false;
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    loadingStopCounter--;
                    isLoading = false;
                    Toast.makeText(getActivity(), "Failed to load data for journals",
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
    }
}
