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

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.manageAvatarListAdapter;
import open.furaffinity.client.dialogs.uploadAvatarDialog;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pagesRead.controlsAvatar;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

public class manageAvatar extends Fragment {
    private static final String TAG = manageAvatar.class.getName();

    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private fabCircular fab;

    private webClient webClient;
    private controlsAvatar page;

    private boolean isLoading = false;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_upload);
        fab.setVisibility(View.GONE);
    }

    private void fetchPageData() {
        if(!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new controlsAvatar(page);
            page.execute();
        }
    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        fetchPageData();
    }

    private void initPages() {
        webClient = new webClient(getActivity());

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new manageAvatarListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new controlsAvatar(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                mDataSet.clear();
                mDataSet.addAll(((controlsAvatar)abstractPage).getPageResults());
                mAdapter.notifyDataSetChanged();
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data for avatars", Toast.LENGTH_SHORT).show();
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

        ((manageAvatarListAdapter) mAdapter).setListener(new manageAvatarListAdapter.manageAvatarListAdapterListener() {
            @Override
            public void onSet(String url) {
                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + url);
                            return null;
                        }
                    }.execute(webClient).get();

                    resetRecycler();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not set avatar: ", e);
                }
            }

            @Override
            public void onDelete(String url) {
                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + url);
                            return null;
                        }
                    }.execute(webClient).get();

                    resetRecycler();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not delete avatar: ", e);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAvatarDialog uploadAvatarDialog = new uploadAvatarDialog();
                uploadAvatarDialog.setListener(new uploadAvatarDialog.uploadAvatarDialogListener() {
                    @Override
                    public void onDialogPositiveClick(String filePath) {
                        try {
                            List<HashMap<String, String>> params = new ArrayList<>();

                            HashMap<String, String> newParam = new HashMap<>();
                            newParam.put("name", "do");
                            newParam.put("value", "uploadavatar");
                            params.add(newParam);

                            newParam = new HashMap<>();
                            newParam.put("name", "avatarfile");
                            newParam.put("filePath", filePath);
                            params.add(newParam);

                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsAvatar.getPagePath(), params);
                                    return null;
                                }
                            }.execute(webClient).get();

                            resetRecycler();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not upload new avatar: ", e);
                        }
                    }
                });
                uploadAvatarDialog.show(getChildFragmentManager(), "uploadAvatarDialog");
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
