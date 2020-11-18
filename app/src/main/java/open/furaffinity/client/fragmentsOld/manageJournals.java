package open.furaffinity.client.fragmentsOld;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
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
import open.furaffinity.client.adapter.controlsJournalListAdapter;
import open.furaffinity.client.dialogs.journalDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pagesOld.controlsJournal;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

public class manageJournals extends Fragment {
    private static final String TAG = manageJournals.class.getName();

    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private fabCircular fab;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pagesOld.controlsJournal page;

    private int loadingStopCounter = 3;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_newmessage);
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
        page = new open.furaffinity.client.pagesOld.controlsJournal();
    }

    private void fetchPageData() {
        if (!(loadingStopCounter == 0)) {
            page = new open.furaffinity.client.pagesOld.controlsJournal();
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
        }
    }

    private void updateUIElements() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new controlsJournalListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                mAdapter.notifyDataSetChanged();
                endlessRecyclerViewScrollListener.resetState();

                initClientAndPage();
                fetchPageData();
                updateUIElements();
                updateUIElementListeners(rootView);

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

        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        ((controlsJournalListAdapter) mAdapter).setListener(new controlsJournalListAdapter.controlsJournalListAdapterListener() {
            @Override
            public void updateJournal(String editPath) {
                controlsJournal existingJournal = new controlsJournal(editPath);

                try {
                    existingJournal.execute(webClient).get();

                    journalDialog journalDialog = new journalDialog();
                    journalDialog.setSubject(existingJournal.getSubject());
                    journalDialog.setBody(existingJournal.getBody());
                    journalDialog.setListener(new journalDialog.journalDialogListener() {
                        @Override
                        public void onDialogPositiveClick(String subject, String body, boolean lockComments, boolean makeFeatured) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("id", existingJournal.getId());
                            params.put("key", existingJournal.getKey());
                            params.put("do", "update");
                            params.put("subject", subject);
                            params.put("message", body);
                            params.put("submit", "Create / Update Journal");

                            if (lockComments) {
                                params.put("lock_comments", "on");
                            }

                            if (makeFeatured) {
                                params.put("make_featured", "on");
                            }

                            try {
                                new AsyncTask<webClient, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                        webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + existingJournal.getPagePath(), params);
                                        return null;
                                    }
                                }.execute(webClient).get();

                                recyclerView.scrollTo(0, 0);
                                mDataSet.clear();
                                mAdapter.notifyDataSetChanged();
                                endlessRecyclerViewScrollListener.resetState();

                                initClientAndPage();
                                fetchPageData();
                                updateUIElements();
                                updateUIElementListeners(rootView);
                            } catch (ExecutionException | InterruptedException e) {
                                Log.e(TAG, "Could not un fav post: ", e);
                            }
                        }
                    });
                    journalDialog.show(getChildFragmentManager(), "updateJournalDialog");
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "updateJournal: ", e);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                journalDialog journalDialog = new journalDialog();
                journalDialog.setListener(new journalDialog.journalDialogListener() {
                    @Override
                    public void onDialogPositiveClick(String subject, String body, boolean lockComments, boolean makeFeatured) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("id", "");
                        params.put("key", page.getKey());
                        params.put("do", "update");
                        params.put("subject", subject);
                        params.put("message", body);
                        params.put("submit", "Create / Update Journal");

                        if (lockComments) {
                            params.put("lock_comments", "on");
                        }

                        if (makeFeatured) {
                            params.put("make_featured", "on");
                        }

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + page.getPagePath(), params);
                                    return null;
                                }
                            }.execute(webClient).get();

                            recyclerView.scrollTo(0, 0);
                            mDataSet.clear();
                            mAdapter.notifyDataSetChanged();
                            endlessRecyclerViewScrollListener.resetState();

                            initClientAndPage();
                            fetchPageData();
                            updateUIElements();
                            updateUIElementListeners(rootView);
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could post journal: ", e);
                        }
                    }
                });
                journalDialog.show(getChildFragmentManager(), "CreateJournalDialog");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view_with_fab, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
