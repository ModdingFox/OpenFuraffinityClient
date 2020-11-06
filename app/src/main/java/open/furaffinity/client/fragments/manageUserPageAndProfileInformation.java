package open.furaffinity.client.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.pages.controlsProfile;
import open.furaffinity.client.utilities.dynamicEditItem;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class manageUserPageAndProfileInformation extends Fragment {
    private static String TAG = manageUserPageAndProfileInformation.class.getName();

    private LinearLayout linearLayout;

    private fabCircular fab;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.controlsProfile page;

    private int loadingStopCounter = 3;

    List<dynamicEditItem> uiElementList;

    private void getElements(View rootView) {
        linearLayout = rootView.findViewById(R.id.linearLayout);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_save);
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
        page = new open.furaffinity.client.pages.controlsProfile();
    }

    private void fetchPageData() {
        if (!(loadingStopCounter == 0)) {
            page = new open.furaffinity.client.pages.controlsProfile();
            try {
                page.execute(webClient).get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "loadPage: ", e);
            }

            if (uiElementList != null) {
                for (dynamicEditItem currentItem : uiElementList) {
                    currentItem.removeFromView();
                }
            }

            uiElementList = new ArrayList<>();

            if (page.getPageResults() != null) {
                for (controlsProfile.inputItem currentInputItem : page.getPageResults()) {
                    if(currentInputItem.isSelect()) {
                        uiElementList.add(new dynamicEditItem(requireContext(), linearLayout, currentInputItem.getName(), currentInputItem.getHeader(), currentInputItem.getValue(), currentInputItem.getOptions()));
                    } else {
                        uiElementList.add(new dynamicEditItem(requireContext(), linearLayout, currentInputItem.getName(), currentInputItem.getHeader(), currentInputItem.getValue(), "", currentInputItem.getMaxLength()));
                    }
                }
            }
        }
    }

    private void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("do", "update");
                params.put("key", page.getKey());

                for(dynamicEditItem currentItem : uiElementList) {
                    params.put(currentItem.getName(), currentItem.getValue());
                }

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + open.furaffinity.client.pages.controlsProfile.getPagePath(), params);
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
        initClientAndPage();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
