package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsContacts;
import open.furaffinity.client.utilities.dynamicEditItem;
import open.furaffinity.client.utilities.fabCircular;

public class manageContactInfo extends open.furaffinity.client.abstractClasses.tabFragment {
    private LinearLayout linearLayout;

    private fabCircular fab;

    private controlsContacts page;

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
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    protected void initPages() {
        page = new controlsContacts(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (uiElementList != null) {
                    for (dynamicEditItem currentItem : uiElementList) {
                        currentItem.removeFromView();
                    }
                }

                uiElementList = new ArrayList<>();

                if (((controlsContacts)abstractPage).getPageResults() != null) {
                    for (HashMap<String, String> currentItem : ((controlsContacts)abstractPage).getPageResults()) {
                        String label = ((currentItem.containsKey("label")) ? (currentItem.get("label")) : (""));
                        String value = ((currentItem.containsKey("value")) ? (currentItem.get("value")) : (""));
                        String name = ((currentItem.containsKey("name")) ? (currentItem.get("name")) : (""));
                        String placeholder = ((currentItem.containsKey("placeholder")) ? (currentItem.get("placeholder")) : (""));

                        uiElementList.add(new dynamicEditItem(requireContext(), linearLayout, name, label, value, placeholder));
                    }
                }

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for contact info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(v -> {
            HashMap<String, String> params = new HashMap<>();

            for (dynamicEditItem currentItem : uiElementList) {
                params.put(currentItem.getName(), currentItem.getValue());
            }

            new open.furaffinity.client.submitPages.submitControlsContacts(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Successfully updated contact info", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to update contact info", Toast.LENGTH_SHORT).show();
                }
            }, page.getKey(), params).execute();
        });
    }
}