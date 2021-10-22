package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import open.furaffinity.client.abstractClasses.BasePage;
import open.furaffinity.client.pages.controlsFoldersSubmissions;
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

    private Spinner assignToFolders;
    private EditText assignToANewFolder;

    private final List<String> folderNames = new ArrayList<>();
    private final List<String> folderKeys = new ArrayList<>();

    private CharSequence[] folderItems = new CharSequence[0];
    private boolean[] folderCheckedStates = new boolean[0];

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

        assignToFolders = rootView.findViewById(R.id.assignToFolders);
        assignToANewFolder = rootView.findViewById(R.id.assignToANewFolder);
    }

    private void initClientAndPage() {
        new controlsFoldersSubmissions(requireContext(), new BasePage.pageListener() {
            @Override
            public void requestSucceeded(BasePage BasePage) {
                for (HashMap<String, String> currentFolder : ((controlsFoldersSubmissions) BasePage).getFolders()) {
                    if (currentFolder.containsKey("name") && currentFolder.containsKey("upfolder_id")) {
                        folderNames.add(currentFolder.get("name"));
                        folderKeys.add(currentFolder.get("upfolder_id"));
                    }
                }

                folderItems = folderNames.toArray(new CharSequence[folderNames.size()]);
                folderCheckedStates = new boolean[folderNames.size()];
                open.furaffinity.client.utilities.uiControls.setSpinnerText(requireContext(), assignToFolders, "No items selected");
            }

            @Override
            public void requestFailed(BasePage BasePage) {
                Toast.makeText(requireContext(), "Failed to get existing folder list", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void updateUIElements() {
        uiControls.spinnerSetAdapter(requireContext(), cat, page.getCat(), "", true, false);
        uiControls.spinnerSetAdapter(requireContext(), aType, page.getaType(), "", true, false);
        uiControls.spinnerSetAdapter(requireContext(), species, page.getSpecies(), "", true, false);
        uiControls.spinnerSetAdapter(requireContext(), gender, page.getGender(), "", true, false);
        uiControls.spinnerSetAdapter(requireContext(), rating, page.getRating(), "", true, false);
    }

    private void updateUIElementListeners() {
        //Just need to have it display the options, track which are selected, and maybe update the spinner so it should a selected count. Also setOnTouchListener with the oddness as it gets mad if I use onClick
        assignToFolders.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMultiChoiceItems(folderItems, folderCheckedStates, (dialog, which, isChecked) -> {
                });
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    int selectedFolderCount = 0;
                    for (boolean currentFolder : folderCheckedStates) {
                        if (currentFolder) {
                            selectedFolderCount++;
                        }
                    }
                    open.furaffinity.client.utilities.uiControls.setSpinnerText(requireContext(), assignToFolders, selectedFolderCount + " items selected");
                });
                builder.create();
                builder.show();
            }
            return false;
        });
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
        updateUIElementListeners();

        Context context = requireContext();

        builder.setView(rootView);

        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> {
            List<String> folderIds = new ArrayList<>();
            for (int i = 0; i < folderCheckedStates.length; i++) {
                if (folderCheckedStates[i]) {
                    folderIds.add(folderKeys.get(i));
                }
            }

            new submitSubmissionPart3(context, new BasePage.pageListener() {
                @Override
                public void requestSucceeded(BasePage BasePage) {
                    Toast.makeText(context, "Successfully uploaded submission", Toast.LENGTH_SHORT).show();
                    uploadFinalizeDialog.this.dismiss();
                }

                @Override
                public void requestFailed(BasePage BasePage) {
                    Toast.makeText(context, "Failed to upload submission step 3", Toast.LENGTH_SHORT).show();
                    uploadFinalizeDialog.this.dismiss();
                }
            }, page.getSubmissionKey(), kvPair.getSelectedValue(cat), kvPair.getSelectedValue(aType), kvPair.getSelectedValue(species), kvPair.getSelectedValue(gender), kvPair.getSelectedValue(rating), title.getText().toString(), description.getText().toString(), keywords.getText().toString(), disableComments.isChecked(), putInScraps.isChecked(), folderIds, assignToANewFolder.getText().toString()).execute();
        });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> {

        });

        return builder.create();
    }
}
