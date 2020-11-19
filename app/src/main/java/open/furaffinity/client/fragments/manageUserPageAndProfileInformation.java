package open.furaffinity.client.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsProfile;
import open.furaffinity.client.utilities.dynamicEditItem;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

public class manageUserPageAndProfileInformation extends Fragment {
    private static String TAG = manageUserPageAndProfileInformation.class.getName();

    private LinearLayout linearLayout;

    private fabCircular fab;

    private open.furaffinity.client.utilities.webClient webClient;
    private controlsProfile page;

    private boolean isLoading = false;
    List<dynamicEditItem> uiElementList;

    private void getElements(View rootView) {
        linearLayout = rootView.findViewById(R.id.linearLayout);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_save);
        fab.setVisibility(View.GONE);
    }

    private void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            page = new controlsProfile(page);
            page.execute();
        }
    }

    private void initPages() {
        webClient = new webClient(requireContext());
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
                Toast.makeText(getActivity(), "Failed to load data for watches", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("do", "update");
                params.put("key", page.getKey());

                for (dynamicEditItem currentItem : uiElementList) {
                    params.put(currentItem.getName(), currentItem.getValue());
                }

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsProfile.getPagePath(), params);
                            return null;
                        }
                    }.execute(webClient).get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not update profile/user info: ", e);
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scrollview_with_fab, container, false);
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
