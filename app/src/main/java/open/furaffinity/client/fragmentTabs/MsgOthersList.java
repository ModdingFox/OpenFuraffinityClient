package open.furaffinity.client.fragmentTabs;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.adapter.MsgOthersListAdapter;
import open.furaffinity.client.dialogs.ConfirmDialog;
import open.furaffinity.client.pages.MsgOthers;
import open.furaffinity.client.submitPages.SubmitMsgOthersDeleteAllOfType;
import open.furaffinity.client.submitPages.SubmitMsgOthersDeleteSelected;
import open.furaffinity.client.utilities.FabCircular;
import open.furaffinity.client.utilities.MessageIds;

public class MsgOthersList extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal") private ConstraintLayout constraintLayout;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<MsgOthersListAdapter.ViewHolder> mAdapter;
    private FabCircular fab;
    private FloatingActionButton removeSelected;
    private FloatingActionButton removeAll;
    private MsgOthers page;
    private boolean isLoading = false;
    private int msgOthersType;

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        layoutManager = new LinearLayoutManager(getActivity());

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        removeSelected = new FloatingActionButton(requireContext());
        removeAll = new FloatingActionButton(requireContext());

        removeSelected.setImageResource(R.drawable.ic_menu_delete);
        removeAll.setImageResource(R.drawable.ic_menu_delete_all);

        //noinspection deprecation
        removeSelected.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        removeAll.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(removeSelected);
        constraintLayout.addView(removeAll);

        fab.addButton(removeSelected, 1.5f, 270);
        fab.addButton(removeAll, 1.5f, 180);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new MsgOthers(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        ((MsgOthersListAdapter) mAdapter).clearChecked();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    protected void initPages() {
        if (getArguments() != null) {
            msgOthersType = getArguments().getInt(MessageIds.msgOthersType_MESSAGE);

            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new MsgOthersListAdapter(mDataSet, getActivity());
            recyclerView.setAdapter(mAdapter);

            page = new MsgOthers(getActivity(),
                new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        switch (msgOthersType) {
                            case 0:
                                mDataSet.addAll(
                                    MsgOthers.processWatchNotifications(
                                        page.getWatches(), "started watching you"));
                                break;
                            case 1:
                                mDataSet.addAll(
                                    MsgOthers.processLineNotifications(
                                        page.getSubmissionComments(), "replied to"));
                                break;
                            case 2:
                                mDataSet.addAll(
                                    MsgOthers.processJournalLineNotifications(
                                        page.getJournalComments(), "replied to"));
                                break;
                            case 3:
                                mDataSet.addAll(
                                    MsgOthers.processShoutNotifications(
                                        page.getShouts(), "left a shout"));
                                break;
                            case 4:
                                mDataSet.addAll(
                                    MsgOthers.processLineNotifications(
                                        page.getFavorites(), "favorited"));
                                break;
                            case 5:
                                mDataSet.addAll(
                                    MsgOthers.processJournalNotifications(
                                        page.getJournals(), "created journal"));
                                break;
                            default:
                                break;
                        }

                        mAdapter.notifyDataSetChanged();

                        fab.setVisibility(View.VISIBLE);
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        fab.setVisibility(View.GONE);
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Failed to load data for notification",
                            Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private void deleteSelected(String itemType, List<String> itemIds) {
        HashMap<String, String> params = new HashMap<>();

        for (int i = 0; i < itemIds.size(); i++) {
            params.put(itemType + "[" + i + "]", itemIds.get(i));
        }

        new SubmitMsgOthersDeleteSelected(getContext(),
            new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    resetRecycler();
                    Toast.makeText(getActivity(), "Successfully deleted selected notifications",
                        Toast.LENGTH_SHORT).show();
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to delete selected notifications",
                        Toast.LENGTH_SHORT).show();
                }
            }, page.getPagePath(), params).execute();
    }

    private void deleteAllOfType(String paramKey, String paramValue) {
        new SubmitMsgOthersDeleteAllOfType(getActivity(),
            new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    resetRecycler();
                    Toast.makeText(getActivity(), "Successfully nuked notifications",
                        Toast.LENGTH_SHORT).show();
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to nuke notifications",
                        Toast.LENGTH_SHORT).show();
                }
            }, page.getPagePath(), paramKey, paramValue).execute();
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        removeSelected.setOnClickListener(v -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setTitleText("Delete Selected Notifications?");
            confirmDialog.setListener(new ConfirmDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    switch (msgOthersType) {
                        case 0:
                            deleteSelected("watches",
                                ((MsgOthersListAdapter) mAdapter).getCheckedItems());
                            break;
                        case 1:
                            deleteSelected("comments-submissions",
                                ((MsgOthersListAdapter) mAdapter).getCheckedItems());
                            break;
                        case 2:
                            deleteSelected("comments-journals",
                                ((MsgOthersListAdapter) mAdapter).getCheckedItems());
                            break;
                        case 3:
                            deleteSelected("shouts",
                                ((MsgOthersListAdapter) mAdapter).getCheckedItems());
                            break;
                        case 4:
                            deleteSelected("favorites",
                                ((MsgOthersListAdapter) mAdapter).getCheckedItems());
                            break;
                        case 5:
                            deleteSelected("journals",
                                ((MsgOthersListAdapter) mAdapter).getCheckedItems());
                            break;
                        default:
                            break;
                    }
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
            confirmDialog.show(getChildFragmentManager(), "getDeleteConfirm");
        });

        removeAll.setOnClickListener(v -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setTitleText("Delete All Notifications?");
            confirmDialog.setListener(new ConfirmDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    switch (msgOthersType) {
                        case 0:
                            deleteAllOfType("nuke-watches", "Nuke Watches");
                            break;
                        case 1:
                            deleteAllOfType("nuke-submission-comments", "Nuke Submission Comments");
                            break;
                        case 2:
                            deleteAllOfType("nuke-journal-comments", "Nuke Journal Comments");
                            break;
                        case 3:
                            deleteAllOfType("nuke-shouts", "Nuke Shouts");
                            break;
                        case 4:
                            deleteAllOfType("nuke-favorites", "Nuke Favorites");
                            break;
                        case 5:
                            deleteAllOfType("nuke-journals", "Nuke Journals");
                            break;
                        default:
                            break;
                    }
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
            confirmDialog.show(getChildFragmentManager(), "getDeleteConfirm");
        });
    }
}
