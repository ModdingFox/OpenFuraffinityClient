package open.furaffinity.client.fragments;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import open.furaffinity.client.utilities.webClient;

public class manageFolders extends Fragment {
    private static final String TAG = manageFolders.class.getName();

    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private fabCircular fab;
    private FloatingActionButton createFolder;
    private FloatingActionButton createGroup;
    private FloatingActionButton renameGroup;

    private open.furaffinity.client.utilities.webClient webClient;
    private controlsFoldersSubmissions page;

    private boolean isLoading = false;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

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

    private void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new controlsFoldersSubmissions(page);
            page.execute();
        }
    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    private void initPages() {
        webClient = new webClient(requireContext());

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

            resetRecycler();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not move item up/down: ", e);
        }
    }

    private void addEditFolder(View rootView, String postURL, String id) {
        controlsFoldersSubmissionsFolder controlsFoldersSubmissionsFolder = new controlsFoldersSubmissionsFolder(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                controlsFoldersSubmissionsFolderDialog controlsFoldersSubmissionsFolderDialog = new controlsFoldersSubmissionsFolderDialog();
                controlsFoldersSubmissionsFolderDialog.setData(((controlsFoldersSubmissionsFolder)abstractPage).getExistingGroups());
                controlsFoldersSubmissionsFolderDialog.setSpinnerSelected(((controlsFoldersSubmissionsFolder)abstractPage).getSelectedGroup());
                controlsFoldersSubmissionsFolderDialog.setFolderName(((controlsFoldersSubmissionsFolder)abstractPage).getFolderName());
                controlsFoldersSubmissionsFolderDialog.setDescription(((controlsFoldersSubmissionsFolder)abstractPage).getDescription());

                controlsFoldersSubmissionsFolderDialog.setListener(new controlsFoldersSubmissionsFolderDialog.controlsFoldersSubmissionsFolderDialogListener() {
                    @Override
                    public void onDialogPositiveClick(String spinnerSelected, String groupName, String folderName, String description) {
                        HashMap<String, String> params = new HashMap<>();
                        if (id == null) {
                            params.put("folder_id", "");
                        } else {
                            params.put("folder_id", id);
                        }
                        params.put("group_id", spinnerSelected);
                        params.put("create_group_name", groupName);
                        params.put("folder_name", folderName);
                        params.put("folder_description", description);
                        params.put("key", ((controlsFoldersSubmissionsFolder)abstractPage).getKey());

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + ((controlsFoldersSubmissionsFolder)abstractPage).getPagePath(), params);
                                    return null;
                                }
                            }.execute(webClient).get();

                            resetRecycler();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not add/edit folder: ", e);
                        }
                    }
                });

                controlsFoldersSubmissionsFolderDialog.show(getChildFragmentManager(), "editFolder");
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to load data for folder", Toast.LENGTH_SHORT).show();
            }
        }, postURL, id);

        controlsFoldersSubmissionsFolder.execute();
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecycler();
            }
        });

        ((manageFolderListAdapter) mAdapter).setListener(new manageFolderListAdapter.manageFolderListAdapterListener() {
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

                    resetRecycler();
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
                        params.put("group_name", ((textDialog) dialog).getText());

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + "/controls/folders/submissions/group/add", params);
                                    return null;
                                }
                            }.execute(webClient).get();

                            resetRecycler();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not create group: ", e);
                        }
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });
                textDialog.show(getChildFragmentManager(), "newGroupDialog");
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

                            resetRecycler();
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
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
