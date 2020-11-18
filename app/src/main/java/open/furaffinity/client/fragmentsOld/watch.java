package open.furaffinity.client.fragmentsOld;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import open.furaffinity.client.adapter.stringListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pagesOld.watchList;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class watch extends Fragment {
    private static final String TAG = watch.class.getName();

    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private Button button;

    private webClient webClient;
    private open.furaffinity.client.pagesOld.watchList page;

    private int loadingStopCounter = 3;
    private boolean isFirstLoad = true;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);

        button = rootView.findViewById(R.id.button);
    }

    private void initClientAndPage() {
        webClient = new webClient(this.getActivity());
        page = new watchList(getArguments().getString(messageIds.pagePath_MESSAGE));
    }

    private void fetchPageData() {
        if (isFirstLoad) {
            mDataSet = watchList.processWatchList(getArguments().getString(messageIds.userWatchRecent_MESSAGE), true);
            isFirstLoad = false;
        } else {
            if (!(loadingStopCounter == 0)) {
                page = new open.furaffinity.client.pagesOld.watchList(page);
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
                List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("item")).collect(Collectors.toList());
                List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("item")).collect(Collectors.toList());
                newPostPaths.removeAll(oldPostPaths);
                pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("item"))).collect(Collectors.toList());
                mDataSet.addAll(pageResults);
            }
        }
    }

    private void updateUIElements() {
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new stringListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void updateUIElementListeners(View rootView) {
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setPage(Integer.toString(page.getPage() + 1));
                int curSize = mAdapter.getItemCount();
                fetchPageData();
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.GONE);
                mDataSet = new ArrayList<>();
                fetchPageData();
                updateUIElements();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_watch, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}