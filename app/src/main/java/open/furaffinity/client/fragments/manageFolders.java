package open.furaffinity.client.fragments;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.manageFolderListAdapter;
import open.furaffinity.client.adapter.manageImageListAdapter;
import open.furaffinity.client.dialogs.controlsFoldersSubmissionsFolderDialog;
import open.furaffinity.client.dialogs.spinnerDialog;
import open.furaffinity.client.dialogs.spinnerTextDialog;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

public class manageFolders extends Fragment {
    private static final String TAG = manageFolders.class.getName();

    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private fabCircular fab;
    private FloatingActionButton createFolder;
    private FloatingActionButton createGroup;
    private FloatingActionButton renameGroup;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.controlsFoldersSubmissions page;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        createFolder = new FloatingActionButton(getContext());
        createGroup = new FloatingActionButton(getContext());
        renameGroup = new FloatingActionButton(getContext());

        createFolder.setImageResource(R.drawable.ic_menu_newfolder);
        createGroup.setImageResource(R.drawable.ic_menu_newgroup);
        renameGroup.setImageResource(R.drawable.ic_menu_edit);

        createFolder.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        createGroup.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        renameGroup.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(createFolder);
        constraintLayout.addView(createGroup);
        constraintLayout.addView(renameGroup);

        fab.addButton(createFolder, 1.5f, 270);
        fab.addButton(createGroup, 1.5f, 225);
        fab.addButton(renameGroup, 1.5f, 180);
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
        page = new open.furaffinity.client.pages.controlsFoldersSubmissions();
    }

    private void fetchPageData() {
        page = new open.furaffinity.client.pages.controlsFoldersSubmissions();
        try {
            page.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "loadNextPage: ", e);
        }

        mDataSet = page.getPageResults();
    }

    private void updateUIElements() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new manageFolderListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void moveFolder(View rootView, String postURL, String key, String position, String id, String idName) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("position", position);
        params.put(idName, id);

        try {
            new AsyncTask<webClient, Void, Void>() {
                @Override
                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + postURL, params);
                    return null;
                }
            }.execute(webClient).get();

            recyclerView.scrollTo(0, 0);
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();

            initClientAndPage();
            fetchPageData();
            updateUIElements();
            updateUIElementListeners(rootView);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not move item up/down: ", e);
        }
    }

    private void addEditFolder(View rootView, String postURL, String id) {
        open.furaffinity.client.pages.controlsFoldersSubmissionsFolder controlsFoldersSubmissionsFolder = new open.furaffinity.client.pages.controlsFoldersSubmissionsFolder(postURL, id);

        try {
            controlsFoldersSubmissionsFolder.execute(webClient).get();

            controlsFoldersSubmissionsFolderDialog controlsFoldersSubmissionsFolderDialog = new controlsFoldersSubmissionsFolderDialog();
            controlsFoldersSubmissionsFolderDialog.setData(controlsFoldersSubmissionsFolder.getExistingGroups());
            controlsFoldersSubmissionsFolderDialog.setSpinnerSelected(controlsFoldersSubmissionsFolder.getSelectedGroup());
            controlsFoldersSubmissionsFolderDialog.setFolderName(controlsFoldersSubmissionsFolder.getFolderName());
            controlsFoldersSubmissionsFolderDialog.setDescription(controlsFoldersSubmissionsFolder.getDescription());

            controlsFoldersSubmissionsFolderDialog.setListener(new controlsFoldersSubmissionsFolderDialog.controlsFoldersSubmissionsFolderDialogListener() {
                @Override
                public void onDialogPositiveClick(String spinnerSelected, String groupName, String folderName, String description) {
                    HashMap<String, String> params = new HashMap<>();
                    if(id == null) {
                        params.put("folder_id", "");
                    } else {
                        params.put("folder_id", id);
                    }
                    params.put("group_id", spinnerSelected);
                    params.put("create_group_name", groupName);
                    params.put("folder_name", folderName);
                    params.put("folder_description", description);
                    params.put("key", controlsFoldersSubmissionsFolder.getKey());

                    try {
                        new AsyncTask<webClient, Void, Void>() {
                            @Override
                            protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsFoldersSubmissionsFolder.getPagePath(), params);
                                return null;
                            }
                        }.execute(webClient).get();

                        recyclerView.scrollTo(0, 0);
                        mDataSet.clear();
                        mAdapter.notifyDataSetChanged();
                        endlessRecyclerViewScrollListener.resetState();

                        initClientAndPage();
                        fetchPageData();
                        updateUIElements();
                        updateUIElementListeners(rootView);
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(TAG, "Could not add/edit folder: ", e);
                    }
                }
            });

            controlsFoldersSubmissionsFolderDialog.show(getChildFragmentManager(), "editFolder");
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "getFolderInfo: ", e);
        }
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                mAdapter.notifyDataSetChanged();
                endlessRecyclerViewScrollListener.resetState();

                initClientAndPage();
                fetchPageData();
                updateUIElements();
                updateUIElementListeners(rootView);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
            }
        };

        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        ((manageFolderListAdapter)mAdapter).setListener(new manageFolderListAdapter.manageFolderListAdapterListener() {
            @Override
            public void upButton(String postURL, String key, String position, String id, String idName) {
                moveFolder(rootView, postURL, key, position, id, idName);
            }

            @Override
            public void downButton(String postURL, String key, String position, String id, String idName) {
                moveFolder(rootView, postURL, key, position, id, idName);
            }

            @Override
            public void deleteButton(String postURL, String key, String id, String idName) {
                HashMap<String, String> params = new HashMap<>();
                params.put("key", key);
                params.put(idName, id);

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + postURL, params);
                            return null;
                        }
                    }.execute(webClient).get();

                    recyclerView.scrollTo(0, 0);
                    mDataSet.clear();
                    mAdapter.notifyDataSetChanged();
                    endlessRecyclerViewScrollListener.resetState();

                    initClientAndPage();
                    fetchPageData();
                    updateUIElements();
                    updateUIElementListeners(rootView);
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not move item up/down: ", e);
                }
            }

            @Override
            public void editButton(String postURL, String id) {
                addEditFolder(rootView, postURL, id);
            }
        });

        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEditFolder(rootView, "/controls/folders/submissions/folder/add", null);
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog textDialog = new textDialog();
                textDialog.setTitleText("Create new group:");
                textDialog.setListener(new textDialog.dialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("key", page.getCreateGroupKey());
                        params.put("position", "-1");
                        params.put("group_name", ((textDialog)dialog).getText());

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + "/controls/folders/submissions/group/add", params);
                                    return null;
                                }
                            }.execute(webClient).get();

                            recyclerView.scrollTo(0, 0);
                            mDataSet.clear();
                            mAdapter.notifyDataSetChanged();
                            endlessRecyclerViewScrollListener.resetState();

                            initClientAndPage();
                            fetchPageData();
                            updateUIElements();
                            updateUIElementListeners(rootView);
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not create group: ", e);
                        }
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });
                textDialog.show(getChildFragmentManager(),"newGroupDialog");
            }
        });

        renameGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerTextDialog spinnerTextDialog = new spinnerTextDialog();
                spinnerTextDialog.setData(page.getExistingGroups(), page.getSelectedGroup());
                spinnerTextDialog.setListener(new spinnerTextDialog.spinnerTextDialogListener() {
                    @Override
                    public void onDialogPositiveClick(String selectedKey, String userText) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("key", page.getRenameGroupKey());
                        params.put("group_id", selectedKey);
                        params.put("group_name", userText);

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + "/controls/folders/submissions/group/edit", params);
                                    return null;
                                }
                            }.execute(webClient).get();

                            recyclerView.scrollTo(0, 0);
                            mDataSet.clear();
                            mAdapter.notifyDataSetChanged();
                            endlessRecyclerViewScrollListener.resetState();

                            initClientAndPage();
                            fetchPageData();
                            updateUIElements();
                            updateUIElementListeners(rootView);
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not rename group: ", e);
                        }
                    }
                });
                spinnerTextDialog.show(getChildFragmentManager(), "renameGroupDialog");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view_with_fab, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
