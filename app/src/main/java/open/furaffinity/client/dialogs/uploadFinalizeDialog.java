package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.submitPages.submitSubmissionPart2;
import open.furaffinity.client.submitPages.submitSubmissionPart3;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;

public class uploadFinalizeDialog extends DialogFragment {
    private final submitSubmissionPart2 page;
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

    public uploadFinalizeDialog(submitSubmissionPart2 page) {
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
    }

    private void updateUIElements() {
        uiControls.spinnerSetAdapter(requireContext(), cat, page.getCat(), "", true, false);
        uiControls.spinnerSetAdapter(requireContext(), aType, page.getaType(), "", true, false);
        uiControls.spinnerSetAdapter(requireContext(), species, page.getSpecies(), "", true, false);
        uiControls.spinnerSetAdapter(requireContext(), gender, page.getGender(), "", true, false);
        uiControls.spinnerSetAdapter(requireContext(), rating, page.getRating(), "", true, false);
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

        Context context = requireContext();

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> {
            new submitSubmissionPart3(context, new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    Toast.makeText(context, "Successfully uploaded submission", Toast.LENGTH_SHORT).show();
                    uploadFinalizeDialog.this.dismiss();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(context, "Failed to upload submission step 3", Toast.LENGTH_SHORT).show();
                    uploadFinalizeDialog.this.dismiss();
                }
            }, page.getSubmissionKey(), kvPair.getSelectedValue(cat), kvPair.getSelectedValue(aType), kvPair.getSelectedValue(species), kvPair.getSelectedValue(gender), kvPair.getSelectedValue(rating), title.getText().toString(), description.getText().toString(), keywords.getText().toString(), disableComments.isChecked(), putInScraps.isChecked()).execute();
        });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> {

        });

        return builder.create();
    }
}
