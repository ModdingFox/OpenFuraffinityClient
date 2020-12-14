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
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.adapter.manageImageListAdapter;
import open.furaffinity.client.dialogs.spinnerDialog;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.gallery;
import open.furaffinity.client.submitPages.submitControlsSubmissionsAssignItemToNewFolder;
import open.furaffinity.client.utilities.fabCircular;

public class manageSubmissions extends appFragment {
    private static final String pagePath = "/controls/submissions/";
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private ConstraintLayout constraintLayout;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<manageImageListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private fabCircular fab;
    private FloatingActionButton assignSelectedToFolder;
    private FloatingActionButton assignSelectedToNewFolder;
    private FloatingActionButton unassignSelectedFromFolders;
    private FloatingActionButton moveSelectedToScraps;
    private FloatingActionButton moveSelectedToGallery;
    private FloatingActionButton removeSelected;
    private gallery page;
    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private final abstractPage.pageListener pageListener = new abstractPage.pageListener() {
        @Override
        public void requestSucceeded(abstractPage abstractPage) {
            List<HashMap<String, String>> pageResults = ((gallery) abstractPage).getPageResults();

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

    public static String getPagePath() {
        return pagePath;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        assignSelectedToFolder = new FloatingActionButton(requireContext());
        assignSelectedToNewFolder = new FloatingActionButton(requireContext());
        unassignSelectedFromFolders = new FloatingActionButton(requireContext());
        moveSelectedToScraps = new FloatingActionButton(requireContext());
        moveSelectedToGallery = new FloatingActionButton(requireContext());
        removeSelected = new FloatingActionButton(requireContext());

        assignSelectedToFolder.setImageResource(R.drawable.ic_menu_existingfolder);
        assignSelectedToNewFolder.setImageResource(R.drawable.ic_menu_newfolder);
        unassignSelectedFromFolders.setImageResource(R.drawable.ic_menu_removefolder);
        moveSelectedToScraps.setImageResource(R.drawable.ic_menu_scrapsfolder);
        moveSelectedToGallery.setImageResource(R.drawable.ic_menu_galleryfolder);
        removeSelected.setImageResource(R.drawable.ic_menu_delete);

        //noinspection deprecation
        assignSelectedToFolder.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        assignSelectedToNewFolder.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        unassignSelectedFromFolders.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        moveSelectedToScraps.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        moveSelectedToGallery.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
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

    protected void fetchPageData() {
        if (!isLoading && loadingStopCounter > 0) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            page = new gallery(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

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

    protected void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new manageImageListAdapter(mDataSet, requireActivity(), requireActivity());
        recyclerView.setAdapter(mAdapter);

        page = new gallery(getActivity(), pageListener, pagePath);
    }

    protected void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setNextPage();
                int curSize = mAdapter.getItemCount();
                fetchPageData();
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        assignSelectedToFolder.setOnClickListener(v -> {
            spinnerDialog spinnerDialog = new spinnerDialog();
            spinnerDialog.setListener(new spinnerDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                    HashMap<String, String> params = new HashMap<>();

                    for (int i = 0; i < elements.size(); i++) {
                        params.put("submission_ids[" + i + "]", elements.get(i));
                    }

                    new open.furaffinity.client.submitPages.submitControlsSubmissionsAssignItemToFolder(getContext(), new abstractPage.pageListener() {
                        @Override
                        public void requestSucceeded(abstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully assigned submission to folder", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(abstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to assign submission to folder", Toast.LENGTH_SHORT).show();
                        }
                    }, spinnerDialog.getSpinnerSelection(), page.getAssignFolderSubmit(), params).execute();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            spinnerDialog.setTitleText("Select Folder To Assign Submissions To");
            spinnerDialog.setData(page.getAssignFolderId());
            spinnerDialog.show(getChildFragmentManager(), "assignFolder");
        });

        assignSelectedToNewFolder.setOnClickListener(v -> {
            textDialog textDialog = new textDialog();
            textDialog.setListener(new textDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                    HashMap<String, String> params = new HashMap<>();

                    for (int i = 0; i < elements.size(); i++) {
                        params.put("submission_ids[" + i + "]", elements.get(i));
                    }

                    new submitControlsSubmissionsAssignItemToNewFolder(getActivity(), new abstractPage.pageListener() {
                        @Override
                        public void requestSucceeded(abstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully assigned submission to new folder", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(abstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to assign submission to new folder", Toast.LENGTH_SHORT).show();
                        }
                    }, ((textDialog) dialog).getText(), page.getCreateFolderSubmit(), params).execute();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });

            textDialog.setTitleText("Enter New Folder Name");
            textDialog.show(getChildFragmentManager(), "newFolder");
        });

        unassignSelectedFromFolders.setOnClickListener(v -> {
            List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

            HashMap<String, String> params = new HashMap<>();

            for (int i = 0; i < elements.size(); i++) {
                params.put("submission_ids[" + i + "]", elements.get(i));
            }

            new open.furaffinity.client.submitPages.submitControlsSubmissionsMoveItem(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    resetRecycler();
                    Toast.makeText(getActivity(), "Successfully removed submission from folders", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed remove submission from folders", Toast.LENGTH_SHORT).show();
                }
            }, "remove_from_folders_submit", page.getRemoveFromFoldersSubmit(), params).execute();
        });

        moveSelectedToScraps.setOnClickListener(v -> {
            List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

            HashMap<String, String> params = new HashMap<>();

            for (int i = 0; i < elements.size(); i++) {
                params.put("submission_ids[" + i + "]", elements.get(i));
            }

            new open.furaffinity.client.submitPages.submitControlsSubmissionsMoveItem(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    resetRecycler();
                    Toast.makeText(getActivity(), "Successfully moved submission to scraps", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to move submission to scraps", Toast.LENGTH_SHORT).show();
                }
            }, "move_to_scraps_submit", page.getMoveToScrapsSubmit(), params).execute();
        });

        moveSelectedToGallery.setOnClickListener(v -> {
            List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

            HashMap<String, String> params = new HashMap<>();

            for (int i = 0; i < elements.size(); i++) {
                params.put("submission_ids[" + i + "]", elements.get(i));
            }

            new open.furaffinity.client.submitPages.submitControlsSubmissionsMoveItem(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    resetRecycler();
                    Toast.makeText(getActivity(), "Successfully moved submission to gallery", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to move submission to gallery", Toast.LENGTH_SHORT).show();
                }
            }, "move_from_scraps_submit", page.getMoveFromScrapsSubmit(), params).execute();
        });

        removeSelected.setOnClickListener(v -> {
            textDialog textDialog = new textDialog();
            textDialog.setListener(new textDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    List<String> elements = ((manageImageListAdapter) mAdapter).getCheckedItems();

                    HashMap<String, String> params = new HashMap<>();


                    for (int i = 0; i < elements.size(); i++) {
                        params.put("submission_ids[" + i + "]", elements.get(i));
                    }

                    new open.furaffinity.client.submitPages.submitControlsSubmissionsDeleteItem(getActivity(), new abstractPage.pageListener() {
                        @Override
                        public void requestSucceeded(abstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully deleted submission", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(abstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to deleted submission", Toast.LENGTH_SHORT).show();
                        }
                    }, params, ((textDialog) dialog).getText()).execute();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            textDialog.setTitleText("Confirm Password To Delete Submissions");
            textDialog.setIsPassword();
            textDialog.show(getChildFragmentManager(), "passwordConfirm");
        });
    }
}
