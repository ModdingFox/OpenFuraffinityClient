package open.furaffinity.client.fragmentDrawers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.adapter.ManageImageListAdapter;
import open.furaffinity.client.dialogs.ConfirmDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.submitPages.SubmitMsgSubmissionsDeleteAll;
import open.furaffinity.client.submitPages.SubmitMsgSubmissionsDeleteSelected;
import open.furaffinity.client.utilities.FabCircular;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class MsgSubmission extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private TableLayout settingsTableLayout;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ManageImageListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private Switch msgSubmissionOrder;
    private Spinner msgSubmissionPerPageSpinner;
    private FabCircular fab;
    private FloatingActionButton pageSettings;
    private FloatingActionButton deleteSelected;
    private FloatingActionButton deleteAll;
    private open.furaffinity.client.pages.MsgSubmission page;
    private int loadingStopCounter = 3;
    private boolean isLoading;
    private final AbstractPage.PageListener pageListener = new AbstractPage.PageListener() {
        @Override public void requestSucceeded(AbstractPage abstractPage) {
            List<HashMap<String, String>> pageResults =
                ((open.furaffinity.client.pages.MsgSubmission) abstractPage).getPageResults();

            final int curSize = mAdapter.getItemCount();

            if (pageResults.size() == 0 && loadingStopCounter > 0) {
                loadingStopCounter--;
            }

            // Deduplicate results
            final List<String> newPostPaths =
                pageResults.stream().map(currentMap -> currentMap.get("postPath"))
                    .collect(Collectors.toList());
            final List<String> oldPostPaths =
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
            Toast.makeText(getActivity(), "Failed to load data for submissions", Toast.LENGTH_SHORT)
                .show();
        }
    };

    @Override protected int getLayout() {
        return R.layout.fragment_msg_submission;
    }

    protected void getElements(View rootView) {
        final SharedPreferences sharedPref =
            requireContext().getSharedPreferences(getString(R.string.settingsFile),
                Context.MODE_PRIVATE);

        final ConstraintLayout constraintLayout = rootView.findViewById(R.id.constraintLayout);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(
            sharedPref.getInt(getString(R.string.imageListColumns),
                Settings.imageListColumnsDefault),
            sharedPref.getInt(getString(R.string.imageListOrientation),
                Settings.imageListOrientationDefault));

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        settingsTableLayout = rootView.findViewById(R.id.settingsTableLayout);

        msgSubmissionOrder = rootView.findViewById(R.id.msgSubmissionOrder);
        msgSubmissionPerPageSpinner = rootView.findViewById(R.id.msgSubmissionPerPageSpinner);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        pageSettings = new FloatingActionButton(requireContext());
        deleteSelected = new FloatingActionButton(requireContext());
        deleteAll = new FloatingActionButton(requireContext());

        pageSettings.setImageResource(R.drawable.ic_menu_settings);
        deleteSelected.setImageResource(R.drawable.ic_menu_delete);
        deleteAll.setImageResource(R.drawable.ic_menu_delete_all);

        pageSettings.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        deleteSelected.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        deleteAll.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(pageSettings);
        constraintLayout.addView(deleteSelected);
        constraintLayout.addView(deleteAll);

        fab.addButton(pageSettings, 1.5f, 270);
        fab.addButton(deleteSelected, 1.5f, 180);
        fab.addButton(deleteAll, 1.5f, 225);
    }

    protected void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new open.furaffinity.client.pages.MsgSubmission(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        ((ManageImageListAdapter) mAdapter).clearChecked();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    protected void initPages() {
        ((MainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(), "");

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new ManageImageListAdapter(mDataSet, requireActivity(), requireActivity());
        recyclerView.setAdapter(mAdapter);

        page =
            new open.furaffinity.client.pages.MsgSubmission(requireActivity(), pageListener, true);
    }

    private void loadCurrentSettings() {
        msgSubmissionOrder.setChecked(page.getIsNewestFirst());
        UiControls.spinnerSetAdapter(requireContext(), msgSubmissionPerPageSpinner,
            page.getPerpage(), page.getCurrentPerpage(), true, true);
    }

    private void saveCurrentSettings() {
        boolean valueChanged = false;

        if (page.getIsNewestFirst() != msgSubmissionOrder.isChecked()) {
            page = new open.furaffinity.client.pages.MsgSubmission(getActivity(), pageListener,
                msgSubmissionOrder.isChecked());
            valueChanged = true;
        }

        final String selectedPerpageValue =
            ((KvPair) msgSubmissionPerPageSpinner.getSelectedItem()).getKey();
        if (!page.getCurrentPerpage().equals(selectedPerpageValue)) {
            page.setPerpage(selectedPerpageValue);
            valueChanged = true;
        }

        if (valueChanged) {
            resetRecycler();
        }
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener =
            new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                @Override
                public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                    if (page.setNextPage()) {
                        final int curSize = mAdapter.getItemCount();
                        fetchPageData();
                        mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
                    }
                }
            };

        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        ((ManageImageListAdapter) mAdapter).setListener(
            new ManageImageListAdapter.ManageImageListAdapterListener() {
                @Override public void onSwipeLeft(String postId) {

                }

                @Override public void onSwipeRight(String postId) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("submissions[]", postId);

                    new SubmitMsgSubmissionsDeleteSelected(
                        getActivity(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(),
                                    "Successfully deleted submission notification",
                                    Toast.LENGTH_SHORT)
                                .show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(),
                                    "Failed to delete submission notification", Toast.LENGTH_SHORT)
                                .show();
                        }
                    },page.getPagePath(), params).execute();
                }
            });

        pageSettings.setOnClickListener(view -> {
            if (swipeRefreshLayout.getVisibility() == View.VISIBLE) {
                swipeRefreshLayout.setVisibility(View.GONE);
                loadCurrentSettings();
                settingsTableLayout.setVisibility(View.VISIBLE);
            }
            else {
                settingsTableLayout.setVisibility(View.GONE);
                saveCurrentSettings();
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        });

        deleteSelected.setOnClickListener(view -> {
            final ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setTitleText("Delete Selected Submission Notifications?");
            confirmDialog.setListener(new ConfirmDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    final List<String> elements = ((ManageImageListAdapter) mAdapter).getCheckedItems();

                    final HashMap<String, String> params = new HashMap<>();

                    for (int index = 0; index < elements.size(); index++) {
                        params.put("submissions[" + index + "]", elements.get(index));
                    }

                    new SubmitMsgSubmissionsDeleteSelected(
                        getActivity(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(),
                                "Successfully deleted selected submission notifications",
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(),
                                "Failed to delete selected submission notifications",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, page.getPagePath(), params).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
            confirmDialog.show(getChildFragmentManager(), "getDeleteConfirm");
        });

        deleteAll.setOnClickListener(view -> {
            final ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setTitleText("Delete All Submission Notifications?");
            confirmDialog.setListener(new ConfirmDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    new SubmitMsgSubmissionsDeleteAll(
                        getActivity(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(),
                                    "Successfully deleted submission notifications",
                                    Toast.LENGTH_SHORT)
                                .show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(),
                                    "Failed to delete submission notifications", Toast.LENGTH_SHORT)
                                .show();
                        }
                    }, page.getPagePath()).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
            confirmDialog.show(getChildFragmentManager(), "getDeleteConfirm");
        });
    }
}
