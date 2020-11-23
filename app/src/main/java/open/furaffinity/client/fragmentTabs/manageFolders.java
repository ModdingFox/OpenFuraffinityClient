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
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.adapter.manageFolderListAdapter;
import open.furaffinity.client.dialogs.controlsFoldersSubmissionsFolderDialog;
import open.furaffinity.client.dialogs.spinnerTextDialog;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.pages.controlsFoldersSubmissionsFolder;
import open.furaffinity.client.pages.controlsFoldersSubmissions;
import open.furaffinity.client.utilities.fabCircular;

public class manageFolders extends open.furaffinity.client.abstractClasses.tabFragment {
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<manageFolderListAdapter.ViewHolder> mAdapter;

    private fabCircular fab;
    private FloatingActionButton createFolder;
    private FloatingActionButton createGroup;
    private FloatingActionButton renameGroup;

    private controlsFoldersSubmissions page;

    private boolean isLoading = false;
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

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
        createFolder.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        createGroup.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        renameGroup.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

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
            page = new controlsFoldersSubmissions(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    protected void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new manageFolderListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new controlsFoldersSubmissions(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                List<HashMap<String, String>> pageResults = ((controlsFoldersSubmissions)abstractPage).getPageResults();

                int curSize = mAdapter.getItemCount();

                //Deduplicate results
                List<String> newPostPaths = pageResults.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                List<String> oldPostPaths = mDataSet.stream().map(currentMap -> currentMap.get("postPath")).collect(Collectors.toList());
                newPostPaths.removeAll(oldPostPaths);
                pageResults = pageResults.stream().filter(currentMap -> newPostPaths.contains(currentMap.get("postPath"))).collect(Collectors.toList());
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
                Toast.makeText(getActivity(), "Failed to load data for folders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveFolder(String postURL, String key, String position, String id, String idName) {
        new open.furaffinity.client.submitPages.submitControlsFoldersSubmissionsMoveFolder(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                resetRecycler();
                Toast.makeText(getActivity(), "Successfully move item", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to move item", Toast.LENGTH_SHORT).show();
            }
        }, postURL, key, position, idName, id).execute();
    }

    private void addEditFolder(String postURL, String id) {
        new controlsFoldersSubmissionsFolder(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                controlsFoldersSubmissionsFolderDialog controlsFoldersSubmissionsFolderDialog = new controlsFoldersSubmissionsFolderDialog();
                controlsFoldersSubmissionsFolderDialog.setData(((controlsFoldersSubmissionsFolder)abstractPage).getExistingGroups());
                controlsFoldersSubmissionsFolderDialog.setSpinnerSelected(((controlsFoldersSubmissionsFolder)abstractPage).getSelectedGroup());
                controlsFoldersSubmissionsFolderDialog.setFolderName(((controlsFoldersSubmissionsFolder)abstractPage).getFolderName());
                controlsFoldersSubmissionsFolderDialog.setDescription(((controlsFoldersSubmissionsFolder)abstractPage).getDescription());

                controlsFoldersSubmissionsFolderDialog.setListener((spinnerSelected, groupName, folderName, description) -> new open.furaffinity.client.submitPages.submitControlsFoldersSubmissionsAddEditFolder(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(open.furaffinity.client.abstractClasses.abstractPage abstractPage) {
                        resetRecycler();
                        Toast.makeText(getActivity(), "Successfully add/edit item", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void requestFailed(open.furaffinity.client.abstractClasses.abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to add/edit item", Toast.LENGTH_SHORT).show();
                    }
                }, ((controlsFoldersSubmissionsFolder)abstractPage).getPagePath(), ((controlsFoldersSubmissionsFolder)abstractPage).getKey(), ((id == null)?(""):(id)), spinnerSelected, groupName, folderName, description).execute());

                controlsFoldersSubmissionsFolderDialog.show(getChildFragmentManager(), "editFolder");
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to load data for folder", Toast.LENGTH_SHORT).show();
            }
        }, postURL, id).execute();
    }

    protected void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        ((manageFolderListAdapter) mAdapter).setListener(new manageFolderListAdapter.manageFolderListAdapterListener() {
            @Override
            public void upButton(String postURL, String key, String position, String id, String idName) {
                moveFolder(postURL, key, position, id, idName);
            }

            @Override
            public void downButton(String postURL, String key, String position, String id, String idName) {
                moveFolder(postURL, key, position, id, idName);
            }

            @Override
            public void deleteButton(String postURL, String key, String id, String idName) {
                new open.furaffinity.client.submitPages.submitControlsFoldersSubmissionsDeleteItem(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(abstractPage abstractPage) {
                        resetRecycler();
                        Toast.makeText(getActivity(), "Successfully deleted item", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to deleted item", Toast.LENGTH_SHORT).show();
                    }
                }, postURL, key, idName, id).execute();
            }

            @Override
            public void editButton(String postURL, String id) {
                addEditFolder(postURL, id);
            }
        });

        createFolder.setOnClickListener(v -> addEditFolder("/controls/folders/submissions/folder/add", null));

        createGroup.setOnClickListener(v -> {
            textDialog textDialog = new textDialog();
            textDialog.setTitleText("Create new group:");
            textDialog.setListener(new textDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    new open.furaffinity.client.submitPages.submitControlsFoldersSubmissionsCreateGroup(getActivity(), new abstractPage.pageListener() {
                        @Override
                        public void requestSucceeded(abstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully create folder", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(abstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to create folder", Toast.LENGTH_SHORT).show();
                        }
                    }, page.getCreateGroupKey(), ((textDialog) dialog).getText()).execute();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            textDialog.show(getChildFragmentManager(), "newGroupDialog");
        });

        renameGroup.setOnClickListener(v -> {
            spinnerTextDialog spinnerTextDialog = new spinnerTextDialog();
            spinnerTextDialog.setData(page.getExistingGroups(), page.getSelectedGroup());
            spinnerTextDialog.setListener((selectedKey, userText) -> new open.furaffinity.client.submitPages.submitControlsFoldersSubmissionsRenameGroup(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    resetRecycler();
                    Toast.makeText(getActivity(), "Successfully renamed folder", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to renamed folder", Toast.LENGTH_SHORT).show();
                }
            }, page.getRenameGroupKey(), selectedKey, userText).execute());
            spinnerTextDialog.show(getChildFragmentManager(), "renameGroupDialog");
        });
    }
}
