package open.furaffinity.client.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class shouts extends Fragment {
    private static String TAG = short.class.getName();

    private RecyclerView.LayoutManager layoutManager;

    private LinearLayout controls;
    private EditText comment;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private String pagePath;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.loginTest loginTest;
    private open.furaffinity.client.pages.user user;

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        controls = rootView.findViewById(R.id.controls);
        comment = new EditText(getContext());
        comment.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        comment.setText("Comment...");
        comment.setShowSoftInputOnFocus(false);
        controls.addView(comment);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void fetchPageData() {
        webClient = new open.furaffinity.client.utilities.webClient(getContext());
        loginTest = new open.furaffinity.client.pages.loginTest();
        user = new open.furaffinity.client.pages.user(pagePath);
        try {
            loginTest.execute(webClient).get();
            user.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }

        mDataSet = open.furaffinity.client.pages.user.processShouts(user.getUserShouts());
    }

    private void updateUIElements() {
        if(loginTest.getIsLoggedIn()) {
            comment.setVisibility(View.VISIBLE);
        } else {
            comment.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new commentListAdapter(mDataSet, getActivity(), loginTest.getIsLoggedIn());
        recyclerView.setAdapter(mAdapter);
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                mAdapter.notifyDataSetChanged();
                fetchPageData();
                updateUIElements();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    comment.clearFocus();
                    textDialog textDialog = new textDialog();

                    textDialog.setListener(new textDialog.dialogListener() {

                        @Override
                        public void onDialogPositiveClick(DialogFragment dialog) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("action", "shout");
                            params.put("key", user.getShoutKey());
                            params.put("name", user.getShoutName());
                            params.put("shout", textDialog.getText());
                            params.put("submit", "Submit");

                            try {
                                new AsyncTask<open.furaffinity.client.utilities.webClient, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                        webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
                                        return null;
                                    }
                                }.execute(new webClient(getContext())).get();

                                mDataSet.clear();
                                mAdapter.notifyDataSetChanged();
                                fetchPageData();
                                updateUIElements();
                            } catch (ExecutionException | InterruptedException e) {
                                Log.e(TAG, "Could not post shout: ", e);
                            }
                        }

                        @Override
                        public void onDialogNegativeClick(DialogFragment dialog) {
                            dialog.dismiss();
                        }
                    });

                    textDialog.setTitleText("Shout:");
                    textDialog.show(getChildFragmentManager(), "shout");
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
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view, container, false);
        pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);;
        getElements(rootView);
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}