package open.furaffinity.client.fragments;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.adapter.controlsJournalListAdapter;
import open.furaffinity.client.dialogs.journalDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.controlsJournal;
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
    private controlsJournal page;

    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private abstractPage.pageListener pageListener = new abstractPage.pageListener() {
        @Override
        public void requestSucceeded(abstractPage abstractPage) {
            List<HashMap<String, String>> pageResults = ((controlsJournal)abstractPage).getPageResults();

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
            Toast.makeText(getActivity(), "Failed to load data for journals", Toast.LENGTH_SHORT).show();
        }
    };

    private void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_newmessage);
        fab.setVisibility(View.GONE);
    }

    private void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new controlsJournal(page);
            page.execute();
        }
    }

    private void resetRecycler() {
        page = new controlsJournal(getActivity(), pageListener);
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    private void initPages() {
        webClient = new webClient(requireContext());

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new controlsJournalListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new controlsJournal(getActivity(), pageListener);
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

        ((controlsJournalListAdapter) mAdapter).setListener(new controlsJournalListAdapter.controlsJournalListAdapterListener() {
            @Override
            public void updateJournal(String editPath) {
                controlsJournal existingJournal = new controlsJournal(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(abstractPage abstractPage) {
                        journalDialog journalDialog = new journalDialog();
                        journalDialog.setSubject(((controlsJournal)abstractPage).getSubject());
                        journalDialog.setBody(((controlsJournal)abstractPage).getBody());
                        journalDialog.setListener(new journalDialog.journalDialogListener() {
                            @Override
                            public void onDialogPositiveClick(String subject, String body, boolean lockComments, boolean makeFeatured) {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("id", ((controlsJournal)abstractPage).getId());
                                params.put("key", ((controlsJournal)abstractPage).getKey());
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
                                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + ((controlsJournal)abstractPage).getPagePath(), params);
                                            return null;
                                        }
                                    }.execute(webClient).get();

                                    resetRecycler();
                                } catch (ExecutionException | InterruptedException e) {
                                    Log.e(TAG, "Could not un fav post: ", e);
                                }
                            }
                        });
                        journalDialog.show(getChildFragmentManager(), "updateJournalDialog");
                    }

                    @Override
                    public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to load data for existing journal", Toast.LENGTH_SHORT).show();
                    }
                }, editPath);

                existingJournal.execute();
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

                            resetRecycler();
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
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
