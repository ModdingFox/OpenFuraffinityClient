package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsProfile;
import open.furaffinity.client.utilities.dynamicEditItem;
import open.furaffinity.client.utilities.fabCircular;

public class manageUserPageAndProfileInformation extends open.furaffinity.client.abstractClasses.tabFragment {
    private LinearLayout linearLayout;

    private fabCircular fab;

    private controlsProfile page;

    private boolean isLoading = false;
    List<dynamicEditItem> uiElementList;

    @Override
    protected int getLayout() {
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
            page = new controlsProfile(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    protected void initPages() {
        page = new controlsProfile(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (uiElementList != null) {
                    for (dynamicEditItem currentItem : uiElementList) {
                        currentItem.removeFromView();
                    }
                }

                uiElementList = new ArrayList<>();

                if (((controlsProfile)abstractPage).getPageResults() != null) {
                    for (controlsProfile.inputItem currentInputItem : ((controlsProfile)abstractPage).getPageResults()) {
                        if (currentInputItem.isSelect()) {
                            uiElementList.add(new dynamicEditItem(requireContext(), linearLayout, currentInputItem.getName(), currentInputItem.getHeader(), currentInputItem.getValue(), currentInputItem.getOptions()));
                        } else {
                            uiElementList.add(new dynamicEditItem(requireContext(), linearLayout, currentInputItem.getName(), currentInputItem.getHeader(), currentInputItem.getValue(), "", currentInputItem.getMaxLength()));
                        }
                    }
                }

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for user page/profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(v -> {
            HashMap<String, String> params = new HashMap<>();

            for (dynamicEditItem currentItem : uiElementList) {
                params.put(currentItem.getName(), currentItem.getValue());
            }

            new open.furaffinity.client.submitPages.submitControlsProfile(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Successfully updated user page and profile info", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to update user page and profile info", Toast.LENGTH_SHORT).show();
                }
            }, page.getKey(), params).execute();
        });
    }
}
