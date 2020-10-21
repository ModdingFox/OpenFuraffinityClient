package open.furaffinity.client.fragments;

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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.stringListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.watchList;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class watch extends Fragment {
    private static final String TAG = watch.class.getName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private LinearLayoutManager layoutManager;

    Button button;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.watchList watchList;

    private int loadingStopCounter = 3;

    private void loadPage() {
        if (!(loadingStopCounter == 0)) {
            watchList = new open.furaffinity.client.pages.watchList(watchList);
            try {
                watchList.execute(webClient).get();
            }//we wait to get the data here. Fuck if i know the proper way to do this in android
            catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "loadNextPage: ", e);
            }

            List<HashMap<String, String>> pageResults = watchList.getPageResults();

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

    private void initEndlessRecyclerView(View rootView) {
        loadPage();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                watchList.setPage(Integer.toString(watchList.getPage() + 1));
                int curSize = mAdapter.getItemCount();
                loadPage();
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        mAdapter = new stringListAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);
    }

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

        webClient = new webClient(this.getActivity());

        List<HashMap<String, String>> usersList = new ArrayList<>();

        Document doc = Jsoup.parse(getArguments().getString(messageIds.userWatchRecent_MESSAGE));
        for (Element currentElement : doc.select("a")) {
            HashMap<String, String> newUser = new HashMap<>();
            newUser.put("item", currentElement.text());
            newUser.put("path", currentElement.attr("href"));
            newUser.put("class", open.furaffinity.client.activity.userActivity.class.getName());
            newUser.put("messageId", messageIds.pagePath_MESSAGE);
            usersList.add(newUser);
        }

        recyclerView = rootView.findViewById(R.id.recyclerView);
        button = rootView.findViewById(R.id.button);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new stringListAdapter(usersList);
        recyclerView.setAdapter(mAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.GONE);
                watchList = new watchList(getArguments().getString(messageIds.userWatchesPath_MESSAGE));
                initEndlessRecyclerView(rootView);
            }
        });

        return rootView;
    }
}