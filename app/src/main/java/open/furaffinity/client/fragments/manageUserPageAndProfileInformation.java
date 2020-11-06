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
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.webClient;

public class manageUserPageAndProfileInformation extends Fragment {
    private static String TAG = manageUserPageAndProfileInformation.class.getName();

    private LinearLayout linearLayout;

    private fabCircular fab;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.controlsProfile page;

    private int loadingStopCounter = 3;

    private class editItem {
        private LinearLayout linearLayout;
        private TextView textView;
        private EditText editText;
        private Spinner spinner;
        private String name;

        private void initEditItem(Context context, LinearLayout linearLayout, String name, String header, String value, int maxLength, HashMap<String, String> options) {
            this.name = name;

            this.linearLayout = linearLayout;
            this.textView = new TextView(context);
            this.textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            this.textView.setText(header);
            this.linearLayout.addView(this.textView);

            if(options == null) {
                this.editText = new EditText(context);
                this.editText.setText(value);

                if (maxLength > 0) {
                    InputFilter[] inputFilter = new InputFilter[1];
                    inputFilter[0] = new InputFilter.LengthFilter(maxLength);
                    this.editText.setFilters(inputFilter);
                }

                this.linearLayout.addView(this.editText);
            } else  {
                this.spinner = new Spinner(context);
                open.furaffinity.client.utilities.uiControls.spinnerSetAdapter(context,this.spinner, options, value, false, false);
                this.linearLayout.addView(this.spinner);
            }
        }

        public editItem(Context context, LinearLayout linearLayout, String name, String header, String value) {
            initEditItem(context, linearLayout, name, header, value, Integer.MAX_VALUE, null);
        }

        public editItem(Context context, LinearLayout linearLayout, String name, String header, String value, int maxLength) {
            initEditItem(context, linearLayout, name, header, value, maxLength, null);
        }

        public editItem(Context context, LinearLayout linearLayout, String name, String header, HashMap<String, String> options) {
            initEditItem(context, linearLayout, name, header, "", 0, options);
        }

        public editItem(Context context, LinearLayout linearLayout, String name, String header, String value, HashMap<String, String> options) {
            initEditItem(context, linearLayout, name, header, value, 0, options);
        }

        public String getName() {
            return name;
        }

        public String getValue(){
            if(this.editText != null) {
                return this.editText.getText().toString();
            } else if (this.spinner != null) {
                return ((kvPair)this.spinner.getSelectedItem()).getKey();
            }

            return "";
        }

        public void removeFromView() {
            this.linearLayout.removeView(textView);

            if(editText != null) {
                this.linearLayout.removeView(this.editText);
            } else if (spinner != null) {
                this.linearLayout.removeView(this.spinner);
            }
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
                for (editItem currentItem : uiElementList) {
                    currentItem.removeFromView();
                }
            }

            uiElementList = new ArrayList<>();

            if (page.getPageResults() != null) {
                for (controlsProfile.inputItem currentInputItem : page.getPageResults()) {
                    if(currentInputItem.isSelect()) {
                        uiElementList.add(new editItem(requireContext(), linearLayout, currentInputItem.getName(), currentInputItem.getHeader(), currentInputItem.getValue(), currentInputItem.getOptions()));
                    } else {
                        uiElementList.add(new editItem(requireContext(), linearLayout, currentInputItem.getName(), currentInputItem.getHeader(), currentInputItem.getValue(), currentInputItem.getMaxLength()));
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

                for(editItem currentItem : uiElementList) {
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
