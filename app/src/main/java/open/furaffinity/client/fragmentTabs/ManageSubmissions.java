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
import open.furaffinity.client.adapter.ManageImageListAdapter;
import open.furaffinity.client.dialogs.SpinnerDialog;
import open.furaffinity.client.dialogs.TextDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pages.Gallery;
import open.furaffinity.client.submitPages.SubmitControlsSubmissionsAssignItemToFolder;
import open.furaffinity.client.submitPages.SubmitControlsSubmissionsAssignItemToNewFolder;
import open.furaffinity.client.submitPages.SubmitControlsSubmissionsDeleteItem;
import open.furaffinity.client.submitPages.SubmitControlsSubmissionsMoveItem;
import open.furaffinity.client.utilities.FabCircular;

public class ManageSubmissions extends AbstractAppFragment {
    private static final String pagePath = "/controls/submissions/";
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal") private ConstraintLayout constraintLayout;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ManageImageListAdapter.ViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private FabCircular fab;
    private FloatingActionButton assignSelectedToFolder;
    private FloatingActionButton assignSelectedToNewFolder;
    private FloatingActionButton unassignSelectedFromFolders;
    private FloatingActionButton moveSelectedToScraps;
    private FloatingActionButton moveSelectedToGallery;
    private FloatingActionButton removeSelected;
    private Gallery page;
    private int loadingStopCounter = 3;
    private boolean isLoading = false;
    private final AbstractPage.PageListener pageListener = new AbstractPage.PageListener() {
        @Override public void requestSucceeded(AbstractPage abstractPage) {
            List<HashMap<String, String>> pageResults = ((Gallery) abstractPage).getPageResults();

            int curSize = mAdapter.getItemCount();

            if (pageResults.size() == 0 && loadingStopCounter > 0) {
                loadingStopCounter--;
            }

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
            loadingStopCounter--;
            fab.setVisibility(View.GONE);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed to load data for submissions", Toast.LENGTH_SHORT)
                .show();
        }
    };

    public static String getPagePath() {
        return pagePath;
    }

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view_with_fab;
    }

    protected void getElements(View rootView) {
        staggeredGridLayoutManager =
            new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

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
        assignSelectedToFolder.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        assignSelectedToNewFolder.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        unassignSelectedFromFolders.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        moveSelectedToScraps.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        moveSelectedToGallery.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        removeSelected.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

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
            page = new Gallery(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void resetRecycler() {
        loadingStopCounter = 3;
        page = new Gallery(getActivity(), pageListener, pagePath);
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        ((ManageImageListAdapter) mAdapter).clearChecked();
        mAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener.resetState();
        fetchPageData();
    }

    protected void initPages() {
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new ManageImageListAdapter(mDataSet, requireActivity(), requireActivity());
        recyclerView.setAdapter(mAdapter);

        page = new Gallery(getActivity(), pageListener, pagePath);
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        endlessRecyclerViewScrollListener =
            new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
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
            SpinnerDialog spinnerDialog = new SpinnerDialog();
            spinnerDialog.setListener(new SpinnerDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    List<String> elements = ((ManageImageListAdapter) mAdapter).getCheckedItems();

                    HashMap<String, String> params = new HashMap<>();

                    for (int i = 0; i < elements.size(); i++) {
                        params.put("submission_ids[" + i + "]", elements.get(i));
                    }

                    new SubmitControlsSubmissionsAssignItemToFolder(
                        getContext(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(),
                                    "Successfully assigned submission to folder",
                                    Toast.LENGTH_SHORT)
                                .show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to assign submission to folder",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, spinnerDialog.getSpinnerSelection(), page.getAssignFolderSubmit(),
                        params).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            spinnerDialog.setTitleText("Select Folder To Assign Submissions To");
            spinnerDialog.setData(page.getAssignFolderId());
            spinnerDialog.show(getChildFragmentManager(), "assignFolder");
        });

        assignSelectedToNewFolder.setOnClickListener(v -> {
            TextDialog textDialog = new TextDialog();
            textDialog.setListener(new TextDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    List<String> elements = ((ManageImageListAdapter) mAdapter).getCheckedItems();

                    HashMap<String, String> params = new HashMap<>();

                    for (int i = 0; i < elements.size(); i++) {
                        params.put("submission_ids[" + i + "]", elements.get(i));
                    }

                    new SubmitControlsSubmissionsAssignItemToNewFolder(getActivity(),
                        new AbstractPage.PageListener() {
                            @Override public void requestSucceeded(AbstractPage abstractPage) {
                                resetRecycler();
                                Toast.makeText(getActivity(),
                                    "Successfully assigned submission to new folder",
                                    Toast.LENGTH_SHORT).show();
                            }

                            @Override public void requestFailed(AbstractPage abstractPage) {
                                Toast.makeText(getActivity(),
                                        "Failed to assign submission to new folder",
                                        Toast.LENGTH_SHORT)
                                    .show();
                            }
                        }, ((TextDialog) dialog).getText(), page.getCreateFolderSubmit(),
                        params).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });

            textDialog.setTitleText("Enter New Folder Name");
            textDialog.show(getChildFragmentManager(), "newFolder");
        });

        unassignSelectedFromFolders.setOnClickListener(v -> {
            List<String> elements = ((ManageImageListAdapter) mAdapter).getCheckedItems();

            HashMap<String, String> params = new HashMap<>();

            for (int i = 0; i < elements.size(); i++) {
                params.put("submission_ids[" + i + "]", elements.get(i));
            }

            new SubmitControlsSubmissionsMoveItem(getActivity(),
                new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        resetRecycler();
                        Toast.makeText(getActivity(),
                                "Successfully removed submission from folders", Toast.LENGTH_SHORT)
                            .show();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed remove submission from folders",
                            Toast.LENGTH_SHORT).show();
                    }
                }, "remove_from_folders_submit", page.getRemoveFromFoldersSubmit(),
                params).execute();
        });

        moveSelectedToScraps.setOnClickListener(v -> {
            List<String> elements = ((ManageImageListAdapter) mAdapter).getCheckedItems();

            HashMap<String, String> params = new HashMap<>();

            for (int i = 0; i < elements.size(); i++) {
                params.put("submission_ids[" + i + "]", elements.get(i));
            }

            new SubmitControlsSubmissionsMoveItem(getActivity(),
                new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        resetRecycler();
                        Toast.makeText(getActivity(), "Successfully moved submission to scraps",
                            Toast.LENGTH_SHORT).show();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to move submission to scraps",
                            Toast.LENGTH_SHORT).show();
                    }
                }, "move_to_scraps_submit", page.getMoveToScrapsSubmit(), params).execute();
        });

        moveSelectedToGallery.setOnClickListener(v -> {
            List<String> elements = ((ManageImageListAdapter) mAdapter).getCheckedItems();

            HashMap<String, String> params = new HashMap<>();

            for (int i = 0; i < elements.size(); i++) {
                params.put("submission_ids[" + i + "]", elements.get(i));
            }

            new SubmitControlsSubmissionsMoveItem(getActivity(),
                new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        resetRecycler();
                        Toast.makeText(getActivity(), "Successfully moved submission to gallery",
                            Toast.LENGTH_SHORT).show();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to move submission to gallery",
                            Toast.LENGTH_SHORT).show();
                    }
                }, "move_from_scraps_submit", page.getMoveFromScrapsSubmit(), params).execute();
        });

        removeSelected.setOnClickListener(v -> {
            TextDialog textDialog = new TextDialog();
            textDialog.setListener(new TextDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    List<String> elements = ((ManageImageListAdapter) mAdapter).getCheckedItems();

                    HashMap<String, String> params = new HashMap<>();


                    for (int i = 0; i < elements.size(); i++) {
                        params.put("submission_ids[" + i + "]", elements.get(i));
                    }

                    new SubmitControlsSubmissionsDeleteItem(
                        getActivity(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Successfully deleted submission",
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to deleted submission",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, params, ((TextDialog) dialog).getText()).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            textDialog.setTitleText("Confirm Password To Delete Submissions");
            textDialog.setIsPassword();
            textDialog.show(getChildFragmentManager(), "passwordConfirm");
        });
    }
}
