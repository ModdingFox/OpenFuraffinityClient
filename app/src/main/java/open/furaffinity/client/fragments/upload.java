package open.furaffinity.client.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class upload extends Fragment {
    private static final String TAG = upload.class.getName();

    private Spinner cat;
    private Spinner aType;
    private Spinner species;
    private Spinner gender;
    private Spinner rating;
    private EditText title;
    private EditText description;
    private EditText keywords;
    private Switch disableComments;
    private Switch putInScraps;

    private fabCircular fab;

    private webClient webClient;
    private open.furaffinity.client.pages.submitSubmissionPart3 page;

    public upload(open.furaffinity.client.pages.submitSubmissionPart3 page) {
        super();
        this.page = page;
    }

    private void getElements(View rootView) {
        cat = rootView.findViewById(R.id.cat);
        aType = rootView.findViewById(R.id.aType);
        species = rootView.findViewById(R.id.species);
        gender = rootView.findViewById(R.id.gender);
        rating = rootView.findViewById(R.id.rating);
        title = rootView.findViewById(R.id.title);
        description = rootView.findViewById(R.id.description);
        keywords = rootView.findViewById(R.id.keywords);
        disableComments = rootView.findViewById(R.id.disableComments);
        putInScraps = rootView.findViewById(R.id.putInScraps);

        fab = rootView.findViewById(R.id.fab);

        fab.setImageResource(R.drawable.ic_menu_upload);
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
    }

    private void updateUIElements() {
        uiControls.spinnerSetAdapter(requireContext(), cat, page.getCat(), page.getCatCurrent(), true, false);
        uiControls.spinnerSetAdapter(requireContext(), aType, page.getaType(), page.getaTypeCurrent(), true, false);
        uiControls.spinnerSetAdapter(requireContext(), species, page.getSpecies(), page.getSpeciesCurrent(), true, false);
        uiControls.spinnerSetAdapter(requireContext(), gender, page.getGender(), page.getGenderCurrent(), true, false);
        uiControls.spinnerSetAdapter(requireContext(), rating, page.getRating(), page.getRatingCurrent(), true, false);
    }

    private HashMap<String, String> getSelected(Spinner spinnerIn, String name) {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("value", ((kvPair)spinnerIn.getSelectedItem()).getKey());
        return result;
    }

    private HashMap<String, String> getText(EditText editTextIn, String name) {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("value", editTextIn.getText().toString());
        return result;
    }

    private HashMap<String, String> getSwitch(Switch switchIn, String name, String onState, String offState) {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("value", ((switchIn.isChecked())?(onState):(offState)));
        return result;
    }

    private void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<HashMap<String, String>> params = new ArrayList<>();

                    for(String key : page.getParams().keySet()){
                        HashMap<String, String> newParam = new HashMap<>();
                        newParam.put("name", key);
                        newParam.put("value", page.getParams().get(key));
                        params.add(newParam);
                    }

                    params.add(getSelected(cat, "cat"));
                    params.add(getSelected(aType, "atype"));
                    params.add(getSelected(species, "species"));
                    params.add(getSelected(gender, "gender"));
                    params.add(getSelected(rating, "rating"));

                    params.add(getText(title, "title"));
                    params.add(getText(description, "message"));
                    params.add(getText(keywords, "keywords"));

                    params.add(getSwitch(disableComments, "lock_comments", "off", "on"));
                    params.add(getSwitch(putInScraps, "scrap", "1", "0"));

                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + open.furaffinity.client.pages.submitSubmissionPart3.getPagePath(), params);
                            return null;
                        }
                    }.execute(webClient).get();

                    getChildFragmentManager().popBackStack();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not upload submission user: ", e);
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        getElements(rootView);
        initClientAndPage();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
