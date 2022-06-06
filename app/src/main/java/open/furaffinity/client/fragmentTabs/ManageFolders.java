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
import open.furaffinity.client.adapter.ManageFolderListAdapter;
import open.furaffinity.client.dialogs.ControlsFoldersSubmissionsFolderDialog;
import open.furaffinity.client.dialogs.SpinnerTextDialog;
import open.furaffinity.client.dialogs.TextDialog;
import open.furaffinity.client.pages.ControlsFoldersSubmissions;
import open.furaffinity.client.pages.ControlsFoldersSubmissionsFolder;
import open.furaffinity.client.submitPages.SubmitControlsFoldersSubmissionsAddEditFolder;
import open.furaffinity.client.submitPages.SubmitControlsFoldersSubmissionsCreateGroup;
import open.furaffinity.client.submitPages.SubmitControlsFoldersSubmissionsDeleteItem;
import open.furaffinity.client.submitPages.SubmitControlsFoldersSubmissionsMoveFolder;
import open.furaffinity.client.submitPages.SubmitControlsFoldersSubmissionsRenameGroup;
import open.furaffinity.client.utilities.FabCircular;

public class ManageFolders extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ManageFolderListAdapter.ViewHolder> mAdapter;
    private FabCircular fab;
    private FloatingActionButton createFolder;
    private FloatingActionButton createGroup;
    private FloatingActionButton renameGroup;
    private ControlsFoldersSubmissions page;
    private boolean isLoading = false;

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager =
            new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        ConstraintLayout constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        createFolder = new FloatingActionButton(requireContext());
        createGroup = new FloatingActionButton(requireContext());
        renameGroup = new FloatingActionButton(requireContext());

        createFolder.setImageResource(R.drawable.ic_menu_newfolder);
        createGroup.setImageResource(R.drawable.ic_menu_newgroup);
        renameGroup.setImageResource(R.drawable.ic_menu_edit);


        //noinspection deprecation
        createFolder.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        createGroup.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        renameGroup.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(createFolder);
        constraintLayout.addView(createGroup);
        constraintLayout.addView(renameGroup);

        fab.addButton(createFolder, 1.5f, 270);
        fab.addButton(createGroup, 1.5f, 225);
        fab.addButton(renameGroup, 1.5f, 180);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new ControlsFoldersSubmissions(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    protected void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new ManageFolderListAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);

        page = new ControlsFoldersSubmissions(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                List<HashMap<String, String>> pageResults =
                    ((ControlsFoldersSubmissions) abstractPage).getPageResults();

                int curSize = mAdapter.getItemCount();

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
                fab.setVisibility(View.GONE);
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Failed to load data for folders", Toast.LENGTH_SHORT)
                    .show();
            }
        });
    }

    private void moveFolder(String postURL, String key, String direction, String id,
                            String idName) {
        new SubmitControlsFoldersSubmissionsMoveFolder(
            getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                resetRecycler();
                Toast.makeText(getActivity(), "Successfully move item", Toast.LENGTH_SHORT).show();
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to move item", Toast.LENGTH_SHORT).show();
            }
        }, postURL, key, direction, idName, id).execute();
    }

    private void addEditFolder(String postURL, String id) {
        new ControlsFoldersSubmissionsFolder(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                ControlsFoldersSubmissionsFolderDialog controlsFoldersSubmissionsFolderDialog =
                    new ControlsFoldersSubmissionsFolderDialog();
                controlsFoldersSubmissionsFolderDialog.setData(
                    ((ControlsFoldersSubmissionsFolder) abstractPage).getExistingGroups());
                controlsFoldersSubmissionsFolderDialog.setSpinnerSelected(
                    ((ControlsFoldersSubmissionsFolder) abstractPage).getSelectedGroup());
                controlsFoldersSubmissionsFolderDialog.setFolderName(
                    ((ControlsFoldersSubmissionsFolder) abstractPage).getFolderName());
                controlsFoldersSubmissionsFolderDialog.setDescription(
                    ((ControlsFoldersSubmissionsFolder) abstractPage).getDescription());

                controlsFoldersSubmissionsFolderDialog.setListener(
                    (spinnerSelected, groupName, folderName, description) -> new SubmitControlsFoldersSubmissionsAddEditFolder(
                        getActivity(), new AbstractPage.PageListener() {
                        @Override
                        public void requestSucceeded(
                            AbstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully add/edit item",
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(
                            AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to add/edit item",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, ((ControlsFoldersSubmissionsFolder) abstractPage).getPagePath(),
                        ((ControlsFoldersSubmissionsFolder) abstractPage).getKey(),
                        ((id == null) ? ("") : (id)), spinnerSelected, groupName, folderName,
                        description).execute());

                controlsFoldersSubmissionsFolderDialog.show(getChildFragmentManager(),
                    "editFolder");
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to load data for folder", Toast.LENGTH_SHORT)
                    .show();
            }
        }, postURL, id).execute();
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        ((ManageFolderListAdapter) mAdapter).setListener(
            new ManageFolderListAdapter.ManageFolderListAdapterListener() {
                @Override
                public void upButton(String postUrl, String key, String direction, String id,
                                     String idName) {
                    moveFolder(postUrl, key, direction, id, idName);
                }

                @Override
                public void downButton(String postUrl, String key, String direction, String id,
                                       String idName) {
                    moveFolder(postUrl, key, direction, id, idName);
                }

                @Override
                public void deleteButton(String posturl, String key, String id, String idName) {
                    new SubmitControlsFoldersSubmissionsDeleteItem(
                        getActivity(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully deleted item",
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to deleted item",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, posturl, key, idName, id).execute();
                }

                @Override public void editButton(String posturl, String id) {
                    addEditFolder(posturl, id);
                }
            });

        createFolder.setOnClickListener(
            v -> addEditFolder("/controls/folders/submissions/folder/add", null));

        createGroup.setOnClickListener(v -> {
            TextDialog textDialog = new TextDialog();
            textDialog.setTitleText("Create new group:");
            textDialog.setListener(new TextDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    new SubmitControlsFoldersSubmissionsCreateGroup(
                        getActivity(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully create folder",
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to create folder",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, page.getCreateGroupKey(), ((TextDialog) dialog).getText()).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            textDialog.show(getChildFragmentManager(), "newGroupDialog");
        });

        renameGroup.setOnClickListener(v -> {
            SpinnerTextDialog spinnerTextDialog = new SpinnerTextDialog();
            spinnerTextDialog.setData(page.getExistingGroups(), page.getSelectedGroup());
            spinnerTextDialog.setListener(
                (selectedKey, userText) -> new SubmitControlsFoldersSubmissionsRenameGroup(
                    getActivity(), new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        resetRecycler();
                        Toast.makeText(getActivity(), "Successfully renamed folder",
                            Toast.LENGTH_SHORT).show();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to renamed folder",
                            Toast.LENGTH_SHORT).show();
                    }
                }, page.getRenameGroupKey(), selectedKey, userText).execute());
            spinnerTextDialog.show(getChildFragmentManager(), "renameGroupDialog");
        });
    }
}
