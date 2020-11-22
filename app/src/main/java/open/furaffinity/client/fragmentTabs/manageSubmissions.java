package open.furaffinity.client.fragmentTabs;

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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.adapter.manageImageListAdapter;
import open.furaffinity.client.dialogs.spinnerDialog;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.gallery;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

public class manageSubmissions extends Fragment {
    private static final String TAG = manageSubmissions.class.getName();

    private ConstraintLayout constraintLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private fabCircular fab;
    private FloatingActionButton assignSelectedToFolder;
    private FloatingActionButton assignSelectedToNewFolder;
    private FloatingActionButton unassignSelectedFromFolders;
    private FloatingActionButton moveSelectedToScraps;
    private FloatingActionButton moveSelectedToGallery;
    private FloatingActionButton removeSelected;

    private webClient webClient;
    private gallery page;

    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private String pagePath = null;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private abstractPage.pageListener pageListener = new abstractPage.pageListener() {
        @Override
        public void requestSucceeded(abstractPage abstractPage) {
            List<HashMap<String, String>> pageResults = ((gallery)abstractPage).getPageResults();

            int curSize = mAdapter.getItemCount();

            if (pageResults.size() == 0 && loadingStopCounter > 0) {
                loadingStopCounter--;
            }

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
            loadingStopCounter--;
            fab.setVisibility(View.GONE);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed to load data for submissions", Toast.LENGTH_SHORT).show();
        }
    };

    private void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        assignSelectedToFolder = new FloatingActionButton(getContext());
        assignSelectedToNewFolder = new FloatingActionButton(getContext());
        unassignSelectedFromFolders = new FloatingActionButton(getContext());
        moveSelectedToScraps = new FloatingActionButton(getContext());
        moveSelectedToGallery = new FloatingActionButton(getContext());
        removeSelected = new FloatingActionButton(getContext());

        assignSelectedToFolder.setImageResource(R.drawable.ic_menu_existingfolder);
        assignSelectedToNewFolder.setImageResource(R.drawable.ic_menu_newfolder);
        unassignSelectedFromFolders.setImageResource(R.drawable.ic_menu_removefolder);
        moveSelectedToScraps.setImageResource(R.drawable.ic_menu_scrapsfolder);
        moveSelectedToGallery.setImageResource(R.drawable.ic_menu_galleryfolder);
        removeSelected.setImageResource(R.drawable.ic_menu_delete);

        assignSelectedToFolder.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        assignSelectedToNewFolder.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        unassignSelectedFromFolders.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        moveSelectedToScraps.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        moveSelectedToGallery.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        removeSelected.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(assignSelectedToFolder);
        constraintLayout.addView(assignSelectedToNewFolder);
        constraintLayout.addView(unassignSelectedFromFolders);
        constraintLayout.addView(moveSelectedToScraps);
        constraintLayout.addView(moveSelectedToGallery);
        constraintLayout.addView(removeSelected);

        fab.addButton(assignSelectedToNewFolder, 1.5f, 180);
        fab.addButton(assignSelectedToFolder, 1.5f, 225);
        fab.addButton(unassignSelectedFromFolders, 1.5f, 270);

        fab.addButton(moveSelectedToScraps, 2.5f, 202.5f);
        fab.addButton(moveSelectedToGallery, 2.5f, 247.5f);

        fab.addButton(removeSelected, 3f, 225);
    }

    private void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new gallery(page);
            page.execute();
        }
    }

    private void resetRecycler() {
        loadingStopCounter = 3;
        page = new gallery(getActivity(), pageListener, pagePath);
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        ((manageImageListAdapter) mAdapter).clearChecked();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    private void initPages() {
        webClient = new webClient(requireContext());

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new manageImageListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);

        page = new gallery(getActivity(), pageListener, pagePath);
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecycler();
            }
        });

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setNextPage();
                int curSize = mAdapter.getItemCount();
                fetchPageData();
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
            }
        };

        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        assignSelectedToFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog spinnerDialog = new spinnerDialog();
                spinnerDialog.setListener(new spinnerDialog.dialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                        HashMap<String, String> params = new HashMap<>();

                        for (int i = 0; i < elements.size(); i++) {
                            params.put("submission_ids[" + Integer.toString(i) + "]", elements.get(i));
                        }

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    params.put("assign_folder_id", spinnerDialog.getSpinnerSelection());
                                    params.put("assign_folder_submit", page.getAssignFolderSubmit());
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);

                                    return null;
                                }
                            }.execute(webClient).get();

                            resetRecycler();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not assign submission folder: ", e);
                        }
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });
                spinnerDialog.setTitleText("Select Folder To Assign Submissions To");
                spinnerDialog.setData(page.getAssignFolderId());
                spinnerDialog.show(getChildFragmentManager(), "assignFolder");
            }
        });

        assignSelectedToNewFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog textDialog = new textDialog();
                textDialog.setListener(new textDialog.dialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                        HashMap<String, String> params = new HashMap<>();
                        params.put("create_folder_name", ((textDialog) dialog).getText());
                        params.put("create_folder_submit", page.getCreateFolderSubmit());

                        for (int i = 0; i < elements.size(); i++) {
                            params.put("submission_ids[" + Integer.toString(i) + "]", elements.get(i));
                        }

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
                                    return null;
                                }
                            }.execute(webClient).get();

                            resetRecycler();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not move submission to new folder: ", e);
                        }
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {

                    }
                });

                textDialog.setTitleText("Enter New Folder Name");
                textDialog.show(getChildFragmentManager(), "newFolder");
            }
        });

        unassignSelectedFromFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                HashMap<String, String> params = new HashMap<>();
                params.put("remove_from_folders_submit", page.getRemoveFromFoldersSubmit());

                for (int i = 0; i < elements.size(); i++) {
                    params.put("submission_ids[" + Integer.toString(i) + "]", elements.get(i));
                }

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
                            return null;
                        }
                    }.execute(webClient).get();

                    resetRecycler();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not move submission to scraps: ", e);
                }
            }
        });

        moveSelectedToScraps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                HashMap<String, String> params = new HashMap<>();
                params.put("move_to_scraps_submit", page.getMoveToScrapsSubmit());

                for (int i = 0; i < elements.size(); i++) {
                    params.put("submission_ids[" + Integer.toString(i) + "]", elements.get(i));
                }

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
                            return null;
                        }
                    }.execute(webClient).get();

                    resetRecycler();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not move submission to scraps: ", e);
                }
            }
        });

        moveSelectedToGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                HashMap<String, String> params = new HashMap<>();
                params.put("move_from_scraps_submit", page.getMoveFromScrapsSubmit());

                for (int i = 0; i < elements.size(); i++) {
                    params.put("submission_ids[" + Integer.toString(i) + "]", elements.get(i));
                }

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
                            return null;
                        }
                    }.execute(webClient).get();

                    resetRecycler();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not move submission to gallery: ", e);
                }
            }
        });

        removeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog textDialog = new textDialog();
                textDialog.setListener(new textDialog.dialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                        HashMap<String, String> params = new HashMap<>();
                        params.put("delete_submissions_submit", "1");

                        for (int i = 0; i < elements.size(); i++) {
                            params.put("submission_ids[" + Integer.toString(i) + "]", elements.get(i));
                        }

                        try {

                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    Document doc = Jsoup.parse(webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params));

                                    Element confirmButton = doc.selectFirst("button.type-remove");

                                    if (confirmButton != null) {
                                        String confirmationCode = confirmButton.attr("value");
                                        params.put("confirm", confirmationCode);
                                        params.put("password", ((textDialog) dialog).getText());
                                        webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
                                    }

                                    return null;
                                }
                            }.execute(webClient).get();

                            resetRecycler();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not delete submission: ", e);
                        }
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });
                textDialog.setTitleText("Confirm Password To Delete Submissions");
                textDialog.setIsPassword();
                textDialog.show(getChildFragmentManager(), "passwordConfirm");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view_with_fab, container, false);
        pagePath = "/controls/submissions/";
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
