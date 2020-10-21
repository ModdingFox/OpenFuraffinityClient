package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.journalListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class userJournals extends Fragment {
    private static final String TAG = userJournals.class.getName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private LinearLayoutManager layoutManager;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.journals journals;

    private int loadingStopCounter = 3;

    private void loadPage() {
        if (!(loadingStopCounter == 0)) {
            journals = new open.furaffinity.client.pages.journals(journals);
            try {
                journals.execute(webClient).get();
            }//we wait to get the data here. Fuck if i know the proper way to do this in android
            catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "loadNextPage: ", e);
            }

            List<HashMap<String, String>> pageResults = journals.getPageResults();

            if (pageResults.size() == 0 && loadingStopCounter > 0) {
                loadingStopCounter--;
            }

            //Deduplicate results
            List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("journalPath")).collect(Collectors.toList());
            List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("journalPath")).collect(Collectors.toList());
            newPostPaths.removeAll(oldPostPaths);
            pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("journalPath"))).collect(Collectors.toList());
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
                journals.setPage(Integer.toString(journals.getPage() + 1));
                int curSize = mAdapter.getItemCount();
                loadPage();
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        mAdapter = new journalListAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        webClient = new webClient(this.getActivity());

        journals = new open.furaffinity.client.pages.journals(getArguments().getString(messageIds.pagePath_MESSAGE));

        initEndlessRecyclerView(rootView);

        return rootView;
    }
}
