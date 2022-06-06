package open.furaffinity.client.fragmentDrawers;

import static open.furaffinity.client.utilities.SendPm.sendPM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.adapter.MsgPmsListAdapter;
import open.furaffinity.client.dialogs.ConfirmDialog;
import open.furaffinity.client.dialogs.SpinnerDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.submitPages.SubmitMsgPmsMoveItem;
import open.furaffinity.client.utilities.FabCircular;

public class MsgPms extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<MsgPmsListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private FabCircular fab;
    private FloatingActionButton newMessage;
    private FloatingActionButton deleteSelectedMessages;
    private FloatingActionButton setSelectedMessagesPriority;
    private FloatingActionButton messageListOptions;
    private open.furaffinity.client.pages.MsgPms page;
    private boolean isLoading;

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        final ConstraintLayout constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        newMessage = new FloatingActionButton(requireContext());
        deleteSelectedMessages = new FloatingActionButton(requireContext());
        setSelectedMessagesPriority = new FloatingActionButton(requireContext());
        messageListOptions = new FloatingActionButton(requireContext());

        newMessage.setImageResource(R.drawable.ic_menu_newmessage);
        deleteSelectedMessages.setImageResource(R.drawable.ic_menu_delete);
        setSelectedMessagesPriority.setImageResource(R.drawable.ic_menu_inbox);
        messageListOptions.setImageResource(R.drawable.ic_menu_settings);

        newMessage.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        deleteSelectedMessages.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        setSelectedMessagesPriority.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        messageListOptions.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(newMessage);
        constraintLayout.addView(deleteSelectedMessages);
        constraintLayout.addView(setSelectedMessagesPriority);
        constraintLayout.addView(messageListOptions);

        fab.addButton(deleteSelectedMessages, 1.5f, 180);
        fab.addButton(setSelectedMessagesPriority, 1.5f, 225);
        fab.addButton(newMessage, 1.5f, 270);
        fab.addButton(messageListOptions, 2.625f, 270);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new open.furaffinity.client.pages.MsgPms(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        page.setPage(1);
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        ((MsgPmsListAdapter) mAdapter).clearChecked();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    protected void initPages() {
        ((MainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(), "");

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MsgPmsListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new open.furaffinity.client.pages.MsgPms(getActivity(),
            new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    List<HashMap<String, String>> messages = page.getMessages();

                    int curSize = mAdapter.getItemCount();

                    // Deduplicate results
                    final List<String> newMessages =
                        messages.stream().map(currentMap -> currentMap.get("messageid"))
                            .collect(Collectors.toList());
                    final List<String> oldMessages =
                        mDataSet.stream().map(currentMap -> currentMap.get("messageid"))
                            .collect(Collectors.toList());
                    newMessages.removeAll(oldMessages);
                    messages = messages.stream()
                        .filter(currentMap -> newMessages.contains(currentMap.get("messageid")))
                        .collect(Collectors.toList());
                    mDataSet.addAll(messages);
                    mAdapter.notifyItemRangeInserted(curSize, mDataSet.size());

                    fab.setVisibility(View.VISIBLE);
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    fab.setVisibility(View.GONE);
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Failed to load data for notes",
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setPage(page.getPage() + 1);
                fetchPageData();
            }
        };

        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        newMessage.setOnClickListener(
            view -> sendPM(getActivity(), getChildFragmentManager(), null));

        deleteSelectedMessages.setOnClickListener(view -> {
            final ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setTitleText("Delete Selected Messages?");
            confirmDialog.setListener(new ConfirmDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    String action = "trash";

                    if (page.getSelectedFolder()
                        .equals(open.furaffinity.client.pages.MsgPms.mailFolders.trash)) {
                        action = "delete";
                    }

                    final List<String> itemIds = ((MsgPmsListAdapter) mAdapter).getCheckedItems();

                    final HashMap<String, String> params = new HashMap<>();
                    for (int index = 0; index < itemIds.size(); index++) {
                        params.put("items[" + index + "]", itemIds.get(index));
                    }

                    new SubmitMsgPmsMoveItem(getActivity(),
                        new AbstractPage.PageListener() {
                            @Override public void requestSucceeded(AbstractPage abstractPage) {
                                resetRecycler();
                                Toast.makeText(getActivity(), "Successfully deleted selected notes",
                                    Toast.LENGTH_SHORT).show();
                            }

                            @Override public void requestFailed(AbstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Failed to delete selected notes",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }, page.getPagePath(), "move_to", action, params).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
            confirmDialog.show(getChildFragmentManager(), "getDeleteConfirm");
        });

        setSelectedMessagesPriority.setOnClickListener(view -> {
            final HashMap<String, String> prioritiesList = new HashMap<>();
            for (open.furaffinity.client.pages.MsgPms.priorities currentPriority
                : open.furaffinity.client.pages.MsgPms.priorities.values()) {
                prioritiesList.put(currentPriority.toString(), currentPriority.getPrintableName());
            }

            final SpinnerDialog spinnerDialog = new SpinnerDialog();
            spinnerDialog.setTitleText("Select Priority");
            spinnerDialog.setData(prioritiesList);
            spinnerDialog.setListener(new SpinnerDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    final List<String> itemIds = ((MsgPmsListAdapter) mAdapter).getCheckedItems();

                    final String moveKey;
                    final String moveValue = spinnerDialog.getSpinnerSelection();

                    if (spinnerDialog.getSpinnerSelection().equals(
                        open.furaffinity.client.pages.MsgPms.priorities.archive.toString())) {
                        moveKey = "move_to";
                    }
                    else {
                        moveKey = "set_prio";
                    }

                    final HashMap<String, String> params = new HashMap<>();
                    for (int i = 0; i < itemIds.size(); i++) {
                        params.put("items[" + i + "]", itemIds.get(i));
                    }

                    new SubmitMsgPmsMoveItem(getActivity(),
                        new AbstractPage.PageListener() {
                            @Override public void requestSucceeded(AbstractPage abstractPage) {
                                resetRecycler();
                                Toast.makeText(getActivity(), "Successfully moved selected notes",
                                    Toast.LENGTH_SHORT).show();
                            }

                            @Override public void requestFailed(AbstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Failed to move selected notes",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }, page.getPagePath(), moveKey, moveValue, params).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            spinnerDialog.show(getChildFragmentManager(), "selectPriority");
        });

        messageListOptions.setOnClickListener(view -> {
            final HashMap<String, String> foldersList = new HashMap<>();
            for (open.furaffinity.client.pages.MsgPms.mailFolders currentMailFolder
                : open.furaffinity.client.pages.MsgPms.mailFolders.values()) {
                foldersList.put(currentMailFolder.toString(), currentMailFolder.getPrintableName());
            }

            final SpinnerDialog spinnerDialog = new SpinnerDialog();
            spinnerDialog.setTitleText("Select Folder");
            spinnerDialog.setData(foldersList);
            spinnerDialog.setListener(new SpinnerDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    final String selectedFolderValue = spinnerDialog.getSpinnerSelection();
                    if (!page.getSelectedFolder().toString().equals(selectedFolderValue)) {
                        final HashMap<String, open.furaffinity.client.pages.MsgPms.mailFolders>
                            foldersList = new HashMap<>();
                        for (open.furaffinity.client.pages.MsgPms.mailFolders currentMailFolder
                            : open.furaffinity.client.pages.MsgPms.mailFolders.values()) {
                            foldersList.put(currentMailFolder.toString(), currentMailFolder);
                        }

                        page.setSelectedFolder(foldersList.get(selectedFolderValue));

                        resetRecycler();
                    }
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            spinnerDialog.show(getChildFragmentManager(), "selectPriority");
        });
    }
}
