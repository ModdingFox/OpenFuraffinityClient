package open.furaffinity.client.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

public class manageContactInfo extends Fragment {
    private static String TAG = manageContactInfo.class.getName();

    private LinearLayout linearLayout;

    private fabCircular fab;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.controlsContacts page;

    private int loadingStopCounter = 3;

    private class editItem {
        private LinearLayout linearLayout;
        private TextView textView;
        private EditText editText;
        private String name;

        public editItem(Context context, LinearLayout linearLayout, String label, String value, String name, String placeholder) {
            textView = new TextView(context);
            editText = new EditText(context);

            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            textView.setText(label);
            editText.setText(value);
            editText.setHint(placeholder);

            this.name = name;

            this.linearLayout = linearLayout;
            linearLayout.addView(textView);
            linearLayout.addView(editText);
        }

        public String getName() {
            return name;
        }

        public String getValue(){
            return editText.getText().toString();
        }

        public void removeFromView() {
            this.linearLayout.removeView(textView);
            this.linearLayout.removeView(editText);
        }
    }

    List<editItem> uiElementList;

    private void getElements(View rootView) {
        linearLayout = rootView.findViewById(R.id.linearLayout);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_save);
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
        page = new open.furaffinity.client.pages.controlsContacts();
    }

    private void fetchPageData() {
        if (!(loadingStopCounter == 0)) {
            page = new open.furaffinity.client.pages.controlsContacts();
            try {
                page.execute(webClient).get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "loadPage: ", e);
            }

            if(uiElementList != null){
                for(editItem currentItem : uiElementList){
                    currentItem.removeFromView();
                }
            }

            uiElementList = new ArrayList<>();

            if(page.getPageResults() != null) {
                for(HashMap<String, String> currentItem : page.getPageResults()) {
                    String label = ((currentItem.containsKey("label"))?(currentItem.get("label")):(""));
                    String value = ((currentItem.containsKey("value"))?(currentItem.get("value")):(""));
                    String name = ((currentItem.containsKey("name"))?(currentItem.get("name")):(""));
                    String placeholder = ((currentItem.containsKey("placeholder"))?(currentItem.get("placeholder")):(""));

                    uiElementList.add(new editItem(getContext(), linearLayout, label, value, name, placeholder));
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

                for(editItem currentItem : uiElementList) {
                    params.put(currentItem.getName(), currentItem.getValue());
                }

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + open.furaffinity.client.pages.controlsContacts.getPagePath(), params);
                            return null;
                        }
                    }.execute(webClient).get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not update contact info: ", e);
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
