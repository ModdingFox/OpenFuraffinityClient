package open.furaffinity.client.fragments;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.manageImageListAdapter;
import open.furaffinity.client.adapter.msgOthersListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class msgOthersList extends Fragment {
    private static String TAG = msgOthers.class.getName();

    private ConstraintLayout constraintLayout;

    private LinearLayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private fabCircular fab;
    private FloatingActionButton removeSelected;
    private FloatingActionButton removeAll;

    private webClient webClient;
    private open.furaffinity.client.pages.msgOthers page;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private int msgOthersType;

    private void getElements(View rootView) {
        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        layoutManager = new LinearLayoutManager(getActivity());

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);

        removeSelected = new FloatingActionButton(getContext());
        removeAll = new FloatingActionButton(getContext());

        removeSelected.setImageResource(R.drawable.ic_menu_delete);
        removeAll.setImageResource(R.drawable.ic_menu_delete_all);

        removeSelected.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        removeAll.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(removeSelected);
        constraintLayout.addView(removeAll);

        fab.addButton(removeSelected, 1.5f, 270);
        fab.addButton(removeAll, 1.5f, 180);
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
        page = new open.furaffinity.client.pages.msgOthers();
    }

    private void fetchPageData() {
        msgOthersType = getArguments().getInt(messageIds.msgOthersType_MESSAGE);

        try {
            page.execute(webClient).get();

            switch (msgOthersType) {
                case 0:
                    mDataSet = open.furaffinity.client.pages.msgOthers.processWatchNotifications(page.getWatches(), "started watching you");
                    break;
                case 2:
                    mDataSet = open.furaffinity.client.pages.msgOthers.processShoutNotifications(page.getShouts(), "left a shout");
                    break;
                case 1:
                    mDataSet = open.furaffinity.client.pages.msgOthers.processLineNotifications(page.getSubmissionComments(), "replied to");
                    break;
                case 3:
                    mDataSet = open.furaffinity.client.pages.msgOthers.processLineNotifications(page.getFavorites(), "favorited");
                    break;
                case 4:
                    mDataSet = open.furaffinity.client.pages.msgOthers.processJournalNotifications(page.getJournals(), "created journal");
                    break;
                default:
                    break;
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "loadNextPage: ", e);
        }
    }

    private void updateUIElements() {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new msgOthersListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void deleteSelected(String itemType, List<String> itemIds) {
        HashMap<String, String> params = new HashMap<>();
        params.put("remove-all", "Remove Selected");

        for(int i = 0; i < itemIds.size(); i++) {
            params.put(itemType + "[" + Integer.toString(i) + "]", itemIds.get(i));
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
            ((msgOthersListAdapter)mAdapter).clearChecked();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();

            initClientAndPage();
            fetchPageData();
            updateUIElements();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not remove notification: ", e);
        }
    }

    private void deleteAllOfType(String paramKey, String paramValue) {
        HashMap<String, String> params = new HashMap<>();
        params.put(paramKey, paramValue);

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
            ((msgOthersListAdapter)mAdapter).clearChecked();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();

            initClientAndPage();
            fetchPageData();
            updateUIElements();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not remove notifications: ", e);
        }
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                ((msgOthersListAdapter)mAdapter).clearChecked();
                mAdapter.notifyDataSetChanged();
                endlessRecyclerViewScrollListener.resetState();

                initClientAndPage();
                fetchPageData();
                updateUIElements();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //we wont be loading more for now. The data should come in prepped. Just reusing the recycler view to be consistent
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        removeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (msgOthersType) {
                    case 0:
                        deleteSelected("watches", ((msgOthersListAdapter)mAdapter).getCheckedItems());
                        break;
                    case 2:
                        deleteSelected("shouts", ((msgOthersListAdapter)mAdapter).getCheckedItems());
                        break;
                    case 1:
                        deleteSelected("comments-submissions", ((msgOthersListAdapter)mAdapter).getCheckedItems());
                        break;
                    case 3:
                        deleteSelected("favorites", ((msgOthersListAdapter)mAdapter).getCheckedItems());
                        break;
                    case 4:
                        deleteSelected("journals", ((msgOthersListAdapter)mAdapter).getCheckedItems());
                        break;
                    default:
                        break;
                }
            }
        });

        removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (msgOthersType) {
                    case 0:
                        deleteAllOfType("nuke-watches", "Nuke Watches");
                        break;
                    case 2:
                        deleteAllOfType("nuke-shouts", "Nuke Shouts");
                        break;
                    case 1:
                        deleteAllOfType("nuke-submission-comments", "Nuke Submission Comments");
                        break;
                    case 3:
                        deleteAllOfType("nuke-favorites", "Nuke Favorites");
                        break;
                    case 4:
                        deleteAllOfType("nuke-journals", "Nuke Journals");
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view_with_fab, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
