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
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.adapter.manageAvatarListAdapter;
import open.furaffinity.client.dialogs.uploadAvatarDialog;
import open.furaffinity.client.pages.controlsAvatar;
import open.furaffinity.client.utilities.fabCircular;

public class manageAvatar extends appFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<manageAvatarListAdapter.ViewHolder> mAdapter;
    private fabCircular fab;
    private controlsAvatar page;
    private boolean isLoading = false;

    @Override
    protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

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
            page = new controlsAvatar(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        fetchPageData();
    }

    protected void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new manageAvatarListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new controlsAvatar(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                mDataSet.clear();
                mDataSet.addAll(((controlsAvatar) abstractPage).getPageResults());
                mAdapter.notifyDataSetChanged();
                fab.setVisibility(View.VISIBLE);
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data for avatars", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        ((manageAvatarListAdapter) mAdapter).setListener(new manageAvatarListAdapter.manageAvatarListAdapterListener() {
            @Override
            public void onSet(String url) {
                new open.furaffinity.client.submitPages.submitGetRequest(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(abstractPage abstractPage) {
                        resetRecycler();
                        Toast.makeText(getActivity(), "Successfully set avatar", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to set avatar", Toast.LENGTH_SHORT).show();
                    }
                }, url).execute();
            }

            @Override
            public void onDelete(String url) {
                new open.furaffinity.client.submitPages.submitGetRequest(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(abstractPage abstractPage) {
                        resetRecycler();
                        Toast.makeText(getActivity(), "Successfully deleted avatar", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to delete avatar", Toast.LENGTH_SHORT).show();
                    }
                }, url).execute();
            }
        });

        fab.setOnClickListener(v -> {
            uploadAvatarDialog uploadAvatarDialog = new uploadAvatarDialog();
            uploadAvatarDialog.setListener(filePath -> new open.furaffinity.client.submitPages.submitNewAvatar(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    resetRecycler();
                    Toast.makeText(getActivity(), "Successfully uploaded avatar", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                }
            }, filePath).execute());
            uploadAvatarDialog.show(getChildFragmentManager(), "uploadAvatarDialog");
        });
    }
}
