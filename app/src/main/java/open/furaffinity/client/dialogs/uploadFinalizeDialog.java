package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class uploadFinalizeDialog extends DialogFragment {
    private static final String TAG = uploadFinalizeDialog.class.getName();

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

    private webClient webClient;
    private open.furaffinity.client.pagesOld.submitSubmissionPart3 page;

    public uploadFinalizeDialog(open.furaffinity.client.pagesOld.submitSubmissionPart3 page) {
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
        result.put("value", ((kvPair) spinnerIn.getSelectedItem()).getKey());
        return result;
    }

    private HashMap<String, String> getText(EditText editTextIn, String name) {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("value", editTextIn.getText().toString());
        return result;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_fragment_uploadfinalizedialog, null);
        getElements(rootView);
        initClientAndPage();
        updateUIElements();

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    List<HashMap<String, String>> params = new ArrayList<>();

                    for (String key : page.getParams().keySet()) {
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

                    if (disableComments.isChecked()) {
                        HashMap<String, String> disableCommentsHashMap = new HashMap<>();
                        disableCommentsHashMap.put("name", "lock_comments");
                        disableCommentsHashMap.put("value", "on");
                        params.add(disableCommentsHashMap);
                    }

                    if (putInScraps.isChecked()) {
                        HashMap<String, String> putInScrapsHashMap = new HashMap<>();
                        putInScrapsHashMap.put("name", "scrap");
                        putInScrapsHashMap.put("value", "1");
                        params.add(putInScrapsHashMap);
                    }

                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + open.furaffinity.client.pagesOld.submitSubmissionPart3.getPagePath(), params);
                            return null;
                        }
                    }.execute(webClient).get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not upload submission user: ", e);
                }
            }
        });
        builder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}
