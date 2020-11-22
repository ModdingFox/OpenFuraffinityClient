package open.furaffinity.client.fragmentTabs;

import android.content.res.ColorStateList;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.pages.controlsShouts;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

public class manageShouts extends Fragment {
    private static final String TAG = manageShouts.class.getName();

    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private fabCircular fab;
    private FloatingActionButton removeSelected;

    private open.furaffinity.client.utilities.webClient webClient;
    private controlsShouts page;

    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        removeSelected = new FloatingActionButton(getContext());

        removeSelected.setImageResource(R.drawable.ic_menu_delete);
        removeSelected.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(removeSelected);

        fab.addButton(removeSelected, 1.5f, 270);
    }

    private void fetchPageData() {
        if(!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new controlsShouts(page);
            page.execute();
        }
    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        ((commentListAdapter) (mAdapter)).clearChecked();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    private void initPages() {
        webClient = new webClient(requireContext());

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new commentListAdapter(mDataSet, getActivity(), true);
        recyclerView.setAdapter(mAdapter);

        page = new controlsShouts(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                List<HashMap<String, String>> pageResults = ((controlsShouts)abstractPage).getPageResults();

                int curSize = mAdapter.getItemCount();

                if (pageResults.size() == 0 && loadingStopCounter > 0) {
                    loadingStopCounter--;
                }

                //Deduplicate results
                List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("checkId")).collect(Collectors.toList());
                List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("checkId")).collect(Collectors.toList());
                newPostPaths.removeAll(oldPostPaths);
                pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("checkId"))).collect(Collectors.toList());
                mDataSet.addAll(pageResults);
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data for shouts", Toast.LENGTH_SHORT).show();
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

        removeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> elements = ((commentListAdapter) mAdapter).getCheckedItems();

                HashMap<String, String> params = new HashMap<>();
                params.put("do", "update");

                for (int i = 0; i < elements.size(); i++) {
                    params.put("shouts[" + Integer.toString(i) + "]", elements.get(i));
                }

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsShouts.getPagePath(), params);
                            return null;
                        }
                    }.execute(webClient).get();

                    resetRecycler();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not remove shout: ", e);
                }
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
