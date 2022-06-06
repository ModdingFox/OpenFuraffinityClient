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
import open.furaffinity.client.pages.ControlsContacts;
import open.furaffinity.client.submitPages.SubmitControlsContacts;
import open.furaffinity.client.utilities.DynamicEditItem;
import open.furaffinity.client.utilities.FabCircular;

public class ManageContactInfo extends AbstractAppFragment {
    List<DynamicEditItem> uiElementList;
    private LinearLayout linearLayout;
    private FabCircular fab;
    private ControlsContacts page;
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
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    protected void initPages() {
        page = new ControlsContacts(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                if (uiElementList != null) {
                    for (DynamicEditItem currentItem : uiElementList) {
                        currentItem.removeFromView();
                    }
                }

                uiElementList = new ArrayList<>();

                if (((ControlsContacts) abstractPage).getPageResults() != null) {
                    for (HashMap<String, String> currentItem :
                        ((ControlsContacts) abstractPage).getPageResults()) {
                        String label =
                            ((currentItem.containsKey("label")) ? (currentItem.get("label")) :
                                (""));
                        String value =
                            ((currentItem.containsKey("value")) ? (currentItem.get("value")) :
                                (""));
                        String name =
                            ((currentItem.containsKey("name")) ? (currentItem.get("name")) : (""));
                        String placeholder = ((currentItem.containsKey("placeholder")) ?
                            (currentItem.get("placeholder")) : (""));

                        uiElementList.add(
                            new DynamicEditItem(requireContext(), linearLayout, name, label, value,
                                placeholder));
                    }
                }

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for contact info",
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

            new SubmitControlsContacts(getActivity(),
                new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Successfully updated contact info",
                            Toast.LENGTH_SHORT).show();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to update contact info",
                            Toast.LENGTH_SHORT).show();
                    }
                }, page.getKey(), params).execute();
        });
    }
}