package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.imageListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.webClient;

public class msgSubmission extends Fragment {
    private static final String TAG = msgSubmission.class.getName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private LinearLayoutManager layoutManager;

    private Switch msgSubmissionOrder;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.msgSubmission msgSubmission;

    private int loadingStopCounter = 3;

    private void loadPage() {
        if (!(loadingStopCounter == 0)) {
            msgSubmission = new open.furaffinity.client.pages.msgSubmission(msgSubmission);
            try {
                msgSubmission.execute(webClient).get();
            }//we wait to get the data here. Fuck if i know the proper way to do this in android
            catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "loadNextPage: ", e);
            }

            List<HashMap<String, String>> pageResults = msgSubmission.getPageResults();

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

    private void initEndlessRecyclerView(View rootView) {
        loadPage();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (msgSubmission.setNextPage()) {
                    int curSize = mAdapter.getItemCount();
                    loadPage();
                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
                }
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        mAdapter = new imageListAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);
    }

    private void loadCurrentSettings() {
        if (msgSubmission.getIsNewestFirst()) {
            msgSubmissionOrder.setChecked(true);
        } else {
            msgSubmissionOrder.setChecked(false);
        }
    }

    private void saveCurrentSettings() {
        boolean valueChanged = false;

        if (msgSubmission.getIsNewestFirst() != msgSubmissionOrder.isChecked()) {
            msgSubmission = new open.furaffinity.client.pages.msgSubmission(msgSubmissionOrder.isChecked());
            valueChanged = true;
        }

        if (valueChanged) {
            recyclerView.scrollTo(0, 0);
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();
            msgSubmission = new open.furaffinity.client.pages.msgSubmission(msgSubmission);
            loadPage();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msg_submission, container, false);

        webClient = new webClient(this.getActivity());

        msgSubmissionOrder = rootView.findViewById(R.id.msgSubmissionOrder);

        msgSubmission = new open.furaffinity.client.pages.msgSubmission(true);

        initEndlessRecyclerView(rootView);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(view ->
        {
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
            TableLayout settingsTableLayout = rootView.findViewById(R.id.settingsTableLayout);
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
                loadCurrentSettings();
                settingsTableLayout.setVisibility(View.VISIBLE);
            } else {
                settingsTableLayout.setVisibility(View.GONE);
                saveCurrentSettings();
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }
}
