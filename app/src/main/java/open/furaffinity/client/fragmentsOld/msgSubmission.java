package open.furaffinity.client.fragmentsOld;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
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
import open.furaffinity.client.adapter.manageImageListAdapter;
import open.furaffinity.client.fragments.settings;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class msgSubmission extends Fragment {
    private static final String TAG = msgSubmission.class.getName();

    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    TableLayout settingsTableLayout;

    private Switch msgSubmissionOrder;
    private Spinner msgSubmissionPerPageSpinner;

    private fabCircular fab;
    private FloatingActionButton pageSettings;
    private FloatingActionButton deleteSelected;
    private FloatingActionButton deleteAll;

    private webClient webClient;
    private open.furaffinity.client.pagesOld.msgSubmission page;

    private int loadingStopCounter = 3;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(sharedPref.getInt(getString(R.string.imageListColumns), settings.imageListColumnsDefault), sharedPref.getInt(getString(R.string.imageListOrientation), settings.imageListOrientationDefault));

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        settingsTableLayout = rootView.findViewById(R.id.settingsTableLayout);

        msgSubmissionOrder = rootView.findViewById(R.id.msgSubmissionOrder);
        msgSubmissionPerPageSpinner = rootView.findViewById(R.id.msgSubmissionPerPageSpinner);

        fab = rootView.findViewById(R.id.fab);

        pageSettings = new FloatingActionButton(getContext());
        deleteSelected = new FloatingActionButton(getContext());
        deleteAll = new FloatingActionButton(getContext());

        pageSettings.setImageResource(R.drawable.ic_menu_settings);
        deleteSelected.setImageResource(R.drawable.ic_menu_delete);
        deleteAll.setImageResource(R.drawable.ic_menu_delete_all);

        pageSettings.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        deleteSelected.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        deleteAll.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(pageSettings);
        constraintLayout.addView(deleteSelected);
        constraintLayout.addView(deleteAll);

        fab.addButton(pageSettings, 1.5f, 270);
        fab.addButton(deleteSelected, 1.5f, 180);
        fab.addButton(deleteAll, 1.5f, 225);
    }

    private void initClientAndPage() {
        webClient = new webClient(getActivity());
        page = new open.furaffinity.client.pagesOld.msgSubmission(true);
    }

    private void fetchPageData() {
        if (!(loadingStopCounter == 0)) {
            page = new open.furaffinity.client.pagesOld.msgSubmission(page);
            try {
                page.execute(webClient).get();
            }//we wait to get the data here. Fuck if i know the proper way to do this in android
            catch (ExecutionException | InterruptedException e) {
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
        mAdapter = new manageImageListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void loadCurrentSettings() {
        if (page.getIsNewestFirst()) {
            msgSubmissionOrder.setChecked(true);
        } else {
            msgSubmissionOrder.setChecked(false);
        }

        uiControls.spinnerSetAdapter(requireContext(), msgSubmissionPerPageSpinner, page.getPerpage(), page.getCurrentPerpage(), true, true);
    }

    private void saveCurrentSettings() {
        boolean valueChanged = false;

        if (page.getIsNewestFirst() != msgSubmissionOrder.isChecked()) {
            page = new open.furaffinity.client.pagesOld.msgSubmission(msgSubmissionOrder.isChecked());
            valueChanged = true;
        }

        String selectedPerpageValue = ((kvPair) msgSubmissionPerPageSpinner.getSelectedItem()).getKey();
        if (!page.getCurrentPerpage().equals(selectedPerpageValue)) {
            page.setPerpage(selectedPerpageValue);
            valueChanged = true;
        }

        if (valueChanged) {
            recyclerView.scrollTo(0, 0);
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();
            page = new open.furaffinity.client.pagesOld.msgSubmission(page);
            fetchPageData();
        }
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                ((manageImageListAdapter) mAdapter).clearChecked();
                mAdapter.notifyDataSetChanged();
                endlessRecyclerViewScrollListener.resetState();
                fetchPageData();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                if (page.setNextPage()) {
                    int curSize = mAdapter.getItemCount();
                    fetchPageData();
                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
                }
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        ((manageImageListAdapter) mAdapter).setListener(new manageImageListAdapter.manageImageListAdapterListener() {
            @Override
            public void onSwipeLeft(String postId) {

            }

            @Override
            public void onSwipeRight(String postId) {
                HashMap<String, String> params = new HashMap<>();
                params.put("messagecenter-action", "remove_checked");
                params.put("submissions[]", postId);

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
                    ((manageImageListAdapter) mAdapter).clearChecked();
                    mAdapter.notifyDataSetChanged();
                    endlessRecyclerViewScrollListener.resetState();
                    fetchPageData();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not delete selected msgSubmissions: ", e);
                }
            }
        });

        pageSettings.setOnClickListener(view ->
        {
            if (swipeRefreshLayout.getVisibility() == View.VISIBLE) {
                swipeRefreshLayout.setVisibility(View.GONE);
                loadCurrentSettings();
                settingsTableLayout.setVisibility(View.VISIBLE);
            } else {
                settingsTableLayout.setVisibility(View.GONE);
                saveCurrentSettings();
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        });

        deleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                HashMap<String, String> params = new HashMap<>();
                params.put("messagecenter-action", "remove_checked");

                for (int i = 0; i < elements.size(); i++) {
                    params.put("submissions[" + Integer.toString(i) + "]", elements.get(i));
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
                    ((manageImageListAdapter) mAdapter).clearChecked();
                    mAdapter.notifyDataSetChanged();
                    endlessRecyclerViewScrollListener.resetState();
                    fetchPageData();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not delete selected msgSubmissions: ", e);
                }
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("messagecenter-action", "nuke_notifications");

                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + page.getPagePath(), params);
                            return null;
                        }
                    }.execute(webClient).get();

                    recyclerView.scrollTo(0, 0);
                    mDataSet.clear();
                    ((manageImageListAdapter) mAdapter).clearChecked();
                    mAdapter.notifyDataSetChanged();
                    endlessRecyclerViewScrollListener.resetState();
                    fetchPageData();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not delete all msgSubmissions: ", e);
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
        View rootView = inflater.inflate(R.layout.fragment_msg_submission, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
