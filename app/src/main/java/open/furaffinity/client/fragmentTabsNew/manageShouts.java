package open.furaffinity.client.fragmentTabsNew;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.pages.controlsShouts;
import open.furaffinity.client.utilities.fabCircular;

public class manageShouts extends open.furaffinity.client.abstractClasses.tabFragment {
    @SuppressWarnings("FieldCanBeLocal")
    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<commentListAdapter.ViewHolder> mAdapter;

    private fabCircular fab;
    private FloatingActionButton removeSelected;

    private controlsShouts page;

    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        removeSelected = new FloatingActionButton(requireContext());

        removeSelected.setImageResource(R.drawable.ic_menu_delete);

        //noinspection deprecation
        removeSelected.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(removeSelected);

        fab.addButton(removeSelected, 1.5f, 270);
    }

    protected void fetchPageData() {
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
        ((commentListAdapter)(mAdapter)).clearChecked();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    protected void initPages() {
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

    protected void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        removeSelected.setOnClickListener(v -> {
            List<String> elements = ((commentListAdapter) mAdapter).getCheckedItems();

            HashMap<String, String> params = new HashMap<>();

            for (int i = 0; i < elements.size(); i++) {
                params.put("shouts[" + i + "]", elements.get(i));
            }

            new open.furaffinity.client.submitPages.submitShouts(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    resetRecycler();
                    Toast.makeText(getActivity(), "Successfully removed shout", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to remove shout", Toast.LENGTH_SHORT).show();
                }
            }, params).execute();
        });
    }
}
