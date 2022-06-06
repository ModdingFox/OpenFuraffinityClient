package open.furaffinity.client.fragmentTabs;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.adapter.ManageImageListAdapter;
import open.furaffinity.client.dialogs.ConfirmDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.Gallery;
import open.furaffinity.client.submitPages.SubmitControlsFavorites;
import open.furaffinity.client.utilities.FabCircular;

public class ManageFavorites extends AbstractAppFragment {
    private static final String pagePath = "/controls/favorites/";
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal") private ConstraintLayout constraintLayout;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ManageImageListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private FabCircular fab;
    private FloatingActionButton removeSelected;
    private Gallery page;
    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private final AbstractPage.PageListener pageListener = new AbstractPage.PageListener() {
        @Override public void requestSucceeded(AbstractPage abstractPage) {
            List<HashMap<String, String>> pageResults = ((Gallery) abstractPage).getPageResults();

            int curSize = mAdapter.getItemCount();

            if (pageResults.size() == 0 && loadingStopCounter > 0) {
                loadingStopCounter--;
            }

            //Deduplicate results
            List<String> newPostPaths =
                pageResults.stream().map(currentMap -> currentMap.get("postPath"))
                    .collect(Collectors.toList());
            List<String> oldPostPaths =
                mDataSet.stream().map(currentMap -> currentMap.get("postPath"))
                    .collect(Collectors.toList());
            newPostPaths.removeAll(oldPostPaths);
            pageResults = pageResults.stream()
                .filter(currentMap -> newPostPaths.contains(currentMap.get("postPath")))
                .collect(Collectors.toList());
            mDataSet.addAll(pageResults);
            mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());

            fab.setVisibility(View.VISIBLE);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override public void requestFailed(AbstractPage abstractPage) {
            loadingStopCounter--;
            fab.setVisibility(View.GONE);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed to load data for favorites", Toast.LENGTH_SHORT)
                .show();
        }
    };


    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager =
            new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        removeSelected = new FloatingActionButton(requireContext());

        removeSelected.setImageResource(R.drawable.ic_menu_delete);
        //noinspection deprecation
        removeSelected.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(removeSelected);

        fab.addButton(removeSelected, 1.5f, 270);
    }

    protected void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new Gallery(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        loadingStopCounter = 3;
        page = new Gallery(getActivity(), pageListener, pagePath);
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        ((ManageImageListAdapter) mAdapter).clearChecked();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    protected void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new ManageImageListAdapter(mDataSet, requireActivity(), requireActivity());
        recyclerView.setAdapter(mAdapter);

        page = new Gallery(getActivity(), pageListener, pagePath);
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener =
            new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                @Override
                public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                    page.setNextPage();
                    fetchPageData();
                }
            };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        removeSelected.setOnClickListener(v -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setTitleText("Delete Selected Favorites?");
            confirmDialog.setListener(new ConfirmDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    List<String> elements = ((ManageImageListAdapter) mAdapter).getCheckedItems();

                    HashMap<String, String> params = new HashMap<>();

                    for (int i = 0; i < elements.size(); i++) {
                        params.put("favorites[" + i + "]", elements.get(i));
                    }

                    new SubmitControlsFavorites(getActivity(),
                        new AbstractPage.PageListener() {
                            @Override public void requestSucceeded(AbstractPage abstractPage) {
                                resetRecycler();
                                Toast.makeText(getActivity(), "Successfully updated favorites",
                                    Toast.LENGTH_SHORT).show();
                            }

                            @Override public void requestFailed(AbstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Failed to update favorites",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }, pagePath, params).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
            confirmDialog.show(getChildFragmentManager(), "getDeleteConfirm");
        });
    }
}
