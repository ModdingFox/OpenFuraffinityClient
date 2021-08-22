package open.furaffinity.client.fragmentDrawers;

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
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.msgPmsListAdapter;
import open.furaffinity.client.dialogs.confirmDialog;
import open.furaffinity.client.dialogs.spinnerDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.fabCircular;

import static open.furaffinity.client.utilities.sendPm.sendPM;

public class msgPms extends appFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private ConstraintLayout constraintLayout;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<msgPmsListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private fabCircular fab;
    private FloatingActionButton newMessage;
    private FloatingActionButton deleteSelectedMessages;
    private FloatingActionButton setSelectedMessagesPriority;
    private FloatingActionButton messageListOptions;
    private open.furaffinity.client.pages.msgPms page;
    private boolean isLoading = false;

    @Override
    protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

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

        //noinspection deprecation
        newMessage.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        deleteSelectedMessages.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        setSelectedMessagesPriority.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        messageListOptions.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

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
            page = new open.furaffinity.client.pages.msgPms(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    private void resetRecycler() {
        page.setPage(1);
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        ((msgPmsListAdapter) mAdapter).clearChecked();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    protected void initPages() {
        ((mainActivity)requireActivity()).drawerFragmentPush(this.getClass().getName(), "");

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new msgPmsListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new open.furaffinity.client.pages.msgPms(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                List<HashMap<String, String>> messages = page.getMessages();

                int curSize = mAdapter.getItemCount();

                //Deduplicate results
                List<String> newMessages = messages.stream().map(currentMap -> currentMap.get("messageid")).collect(Collectors.toList());
                List<String> oldMessages = mDataSet.stream().map(currentMap -> currentMap.get("messageid")).collect(Collectors.toList());
                newMessages.removeAll(oldMessages);
                messages = messages.stream().filter(currentMap -> newMessages.contains(currentMap.get("messageid"))).collect(Collectors.toList());
                mDataSet.addAll(messages);
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
                Toast.makeText(getActivity(), "Failed to load data for notes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setPage(page.getPage() + 1);
                fetchPageData();
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        newMessage.setOnClickListener(view -> sendPM(getActivity(), getChildFragmentManager(), null));

        deleteSelectedMessages.setOnClickListener(view -> {
            confirmDialog confirmDialog = new confirmDialog();
            confirmDialog.setTitleText("Delete Selected Messages?");
            confirmDialog.setListener(new confirmDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    String action = "trash";

                    if(page.getSelectedFolder().equals(open.furaffinity.client.pages.msgPms.mailFolders.trash)) {
                        action = "delete";
                    }

                    List<String> itemIds = ((msgPmsListAdapter) mAdapter).getCheckedItems();

                    HashMap<String, String> params = new HashMap<>();
                    for (int i = 0; i < itemIds.size(); i++) {
                        params.put("items[" + i + "]", itemIds.get(i));
                    }

                    new open.furaffinity.client.submitPages.submitMsgPmsMoveItem(getActivity(), new abstractPage.pageListener() {
                        @Override
                        public void requestSucceeded(abstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully deleted selected notes", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(abstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to delete selected notes", Toast.LENGTH_SHORT).show();
                        }
                    }, page.getPagePath(), "move_to", action, params).execute();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
            confirmDialog.show(getChildFragmentManager(), "getDeleteConfirm");
        });

        setSelectedMessagesPriority.setOnClickListener(view ->
        {
            HashMap<String, String> prioritiesList = new HashMap<>();
            for (open.furaffinity.client.pages.msgPms.priorities currentPriority : open.furaffinity.client.pages.msgPms.priorities.values()) {
                prioritiesList.put(currentPriority.toString(), currentPriority.getPrintableName());
            }

            spinnerDialog spinnerDialog = new spinnerDialog();
            spinnerDialog.setTitleText("Select Priority");
            spinnerDialog.setData(prioritiesList);
            spinnerDialog.setListener(new spinnerDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    List<String> itemIds = ((msgPmsListAdapter) mAdapter).getCheckedItems();

                    String moveKey;
                    String moveValue = spinnerDialog.getSpinnerSelection();

                    if (spinnerDialog.getSpinnerSelection().equals(open.furaffinity.client.pages.msgPms.priorities.archive.toString())) {
                        moveKey = "move_to";
                    } else {
                        moveKey = "set_prio";
                    }

                    HashMap<String, String> params = new HashMap<>();
                    for (int i = 0; i < itemIds.size(); i++) {
                        params.put("items[" + i + "]", itemIds.get(i));
                    }

                    new open.furaffinity.client.submitPages.submitMsgPmsMoveItem(getActivity(), new abstractPage.pageListener() {
                        @Override
                        public void requestSucceeded(abstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully moved selected notes", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(abstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to move selected notes", Toast.LENGTH_SHORT).show();
                        }
                    }, page.getPagePath(), moveKey, moveValue, params).execute();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            spinnerDialog.show(getChildFragmentManager(), "selectPriority");
        });

        messageListOptions.setOnClickListener(view ->
        {
            HashMap<String, String> foldersList = new HashMap<>();
            for (open.furaffinity.client.pages.msgPms.mailFolders currentMailFolder : open.furaffinity.client.pages.msgPms.mailFolders.values()) {
                foldersList.put(currentMailFolder.toString(), currentMailFolder.getPrintableName());
            }

            spinnerDialog spinnerDialog = new spinnerDialog();
            spinnerDialog.setTitleText("Select Folder");
            spinnerDialog.setData(foldersList);
            spinnerDialog.setListener(new spinnerDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    String selectedFolderValue = spinnerDialog.getSpinnerSelection();
                    if (!page.getSelectedFolder().toString().equals(selectedFolderValue)) {
                        HashMap<String, open.furaffinity.client.pages.msgPms.mailFolders> foldersList = new HashMap<>();
                        for (open.furaffinity.client.pages.msgPms.mailFolders currentMailFolder : open.furaffinity.client.pages.msgPms.mailFolders.values()) {
                            foldersList.put(currentMailFolder.toString(), currentMailFolder);
                        }

                        page.setSelectedFolder(foldersList.get(selectedFolderValue));

                        resetRecycler();
                    }
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            spinnerDialog.show(getChildFragmentManager(), "selectPriority");
        });
    }
}
