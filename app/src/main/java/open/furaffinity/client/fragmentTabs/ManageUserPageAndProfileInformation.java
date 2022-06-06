package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.pages.ControlsProfile;
import open.furaffinity.client.submitPages.SubmitControlsProfile;
import open.furaffinity.client.utilities.DynamicEditItem;
import open.furaffinity.client.utilities.FabCircular;

public class ManageUserPageAndProfileInformation extends AbstractAppFragment {
    List<DynamicEditItem> uiElementList;
    private LinearLayout linearLayout;
    private FabCircular fab;
    private ControlsProfile page;
    private boolean isLoading = false;

    @Override protected int getLayout() {
        return R.layout.fragment_scrollview_with_fab;
    }

    protected void getElements(View rootView) {
        linearLayout = rootView.findViewById(R.id.linearLayout);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_save);
        fab.setVisibility(View.GONE);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            page = new ControlsProfile(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    protected void initPages() {
        page = new ControlsProfile(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                if (uiElementList != null) {
                    for (DynamicEditItem currentItem : uiElementList) {
                        currentItem.removeFromView();
                    }
                }

                uiElementList = new ArrayList<>();

                if (((ControlsProfile) abstractPage).getPageResults() != null) {
                    for (ControlsProfile.inputItem currentInputItem :
                        ((ControlsProfile) abstractPage).getPageResults()) {
                        if (currentInputItem.isSelect()) {
                            uiElementList.add(new DynamicEditItem(requireContext(), linearLayout,
                                currentInputItem.getName(), currentInputItem.getHeader(),
                                currentInputItem.getValue(), currentInputItem.getOptions()));
                        }
                        else {
                            uiElementList.add(new DynamicEditItem(requireContext(), linearLayout,
                                currentInputItem.getName(), currentInputItem.getHeader(),
                                currentInputItem.getValue(), "", currentInputItem.getMaxLength()));
                        }
                    }
                }

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for user page/profile",
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUiElementListeners(View rootView) {
        fab.setOnClickListener(v -> {
            HashMap<String, String> params = new HashMap<>();

            for (DynamicEditItem currentItem : uiElementList) {
                params.put(currentItem.getName(), currentItem.getValue());
            }

            new SubmitControlsProfile(getActivity(),
                new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(),
                                "Successfully updated user page and profile info",
                                Toast.LENGTH_SHORT)
                            .show();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to update user page and profile info",
                            Toast.LENGTH_SHORT).show();
                    }
                }, page.getKey(), params).execute();
        });
    }
}
