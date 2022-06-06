package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.adapter.ManageAvatarListAdapter;
import open.furaffinity.client.dialogs.UploadAvatarDialog;
import open.furaffinity.client.pages.ControlsAvatar;
import open.furaffinity.client.submitPages.SubmitGetRequest;
import open.furaffinity.client.submitPages.SubmitNewAvatar;
import open.furaffinity.client.utilities.FabCircular;

public class ManageAvatar extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ManageAvatarListAdapter.ViewHolder> mAdapter;
    private FabCircular fab;
    private ControlsAvatar page;
    private boolean isLoading = false;

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager =
            new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_upload);
        fab.setVisibility(View.GONE);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new ControlsAvatar(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        fetchPageData();
    }

    protected void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new ManageAvatarListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new ControlsAvatar(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                mDataSet.clear();
                mDataSet.addAll(((ControlsAvatar) abstractPage).getPageResults());
                mAdapter.notifyDataSetChanged();
                fab.setVisibility(View.VISIBLE);
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data for avatars", Toast.LENGTH_SHORT)
                    .show();
            }
        });
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        ((ManageAvatarListAdapter) mAdapter).setListener(
            new ManageAvatarListAdapter.ManageAvatarListAdapterListener() {
                @Override public void onSet(String url) {
                    new SubmitGetRequest(getActivity(),
                        new AbstractPage.PageListener() {
                            @Override public void requestSucceeded(AbstractPage abstractPage) {
                                resetRecycler();
                                Toast.makeText(getActivity(), "Successfully set avatar",
                                    Toast.LENGTH_SHORT).show();
                            }

                            @Override public void requestFailed(AbstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Failed to set avatar",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }, url).execute();
                }

                @Override public void onDelete(String url) {
                    new SubmitGetRequest(getActivity(),
                        new AbstractPage.PageListener() {
                            @Override public void requestSucceeded(AbstractPage abstractPage) {
                                resetRecycler();
                                Toast.makeText(getActivity(), "Successfully deleted avatar",
                                    Toast.LENGTH_SHORT).show();
                            }

                            @Override public void requestFailed(AbstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Failed to delete avatar",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }, url).execute();
                }
            });

        fab.setOnClickListener(v -> {
            UploadAvatarDialog uploadAvatarDialog = new UploadAvatarDialog();
            uploadAvatarDialog.setListener(
                filePath -> new SubmitNewAvatar(getActivity(),
                    new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully uploaded avatar",
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to upload avatar",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, filePath).execute());
            uploadAvatarDialog.show(getChildFragmentManager(), "uploadAvatarDialog");
        });
    }
}
