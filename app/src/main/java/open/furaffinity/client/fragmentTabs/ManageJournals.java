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
import open.furaffinity.client.adapter.ControlsJournalListAdapter;
import open.furaffinity.client.dialogs.JournalDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.ControlsJournal;
import open.furaffinity.client.submitPages.SubmitControlsJournal;
import open.furaffinity.client.utilities.FabCircular;

public class ManageJournals extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ControlsJournalListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private FabCircular fab;
    private ControlsJournal page;
    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private final AbstractPage.PageListener pageListener = new AbstractPage.PageListener() {
        @Override public void requestSucceeded(AbstractPage abstractPage) {
            List<HashMap<String, String>> pageResults =
                ((ControlsJournal) abstractPage).getPageResults();

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

            fab.setVisibility(View.VISIBLE);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override public void requestFailed(AbstractPage abstractPage) {
            loadingStopCounter--;
            fab.setVisibility(View.GONE);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed to load data for journals", Toast.LENGTH_SHORT)
                .show();
        }
    };

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager =
            new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_newmessage);
        fab.setVisibility(View.GONE);
    }

    protected void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new ControlsJournal(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        page = new ControlsJournal(getActivity(), pageListener);
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    protected void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new ControlsJournalListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new ControlsJournal(getActivity(), pageListener);
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

        ((ControlsJournalListAdapter) mAdapter).setListener(editPath -> {
            ControlsJournal existingJournal =
                new ControlsJournal(getActivity(), new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        JournalDialog journalDialog = new JournalDialog();
                        journalDialog.setSubject(((ControlsJournal) abstractPage).getSubject());
                        journalDialog.setBody(((ControlsJournal) abstractPage).getBody());
                        journalDialog.setListener(
                            (subject, body, lockComments, makeFeatured) -> new SubmitControlsJournal(
                                getActivity(), new AbstractPage.PageListener() {
                                @Override
                                public void requestSucceeded(
                                    AbstractPage abstractPage) {
                                    resetRecycler();
                                    Toast.makeText(getActivity(),
                                            "Successfully updated journal item", Toast.LENGTH_SHORT)
                                        .show();
                                }

                                @Override
                                public void requestFailed(
                                    AbstractPage abstractPage) {
                                    Toast.makeText(getActivity(), "Failed to update journal item",
                                        Toast.LENGTH_SHORT).show();
                                }
                            }, ((ControlsJournal) abstractPage).getPagePath(),
                                ((ControlsJournal) abstractPage).getKey(),
                                ((ControlsJournal) abstractPage).getId(), subject, body,
                                lockComments, makeFeatured).execute());
                        journalDialog.show(getChildFragmentManager(), "updateJournalDialog");
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to load data for existing journal",
                            Toast.LENGTH_SHORT).show();
                    }
                }, editPath);

            existingJournal.execute();
        });

        fab.setOnClickListener(v -> {
            JournalDialog journalDialog = new JournalDialog();
            journalDialog.setListener(
                (subject, body, lockComments, makeFeatured) -> new SubmitControlsJournal(
                    getActivity(), new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        resetRecycler();
                        Toast.makeText(getActivity(), "Successfully created journal item",
                            Toast.LENGTH_SHORT).show();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to created journal item",
                            Toast.LENGTH_SHORT).show();
                    }
                }, page.getPagePath(), page.getKey(), "", subject, body, lockComments,
                    makeFeatured).execute());
            journalDialog.show(getChildFragmentManager(), "CreateJournalDialog");
        });
    }
}
