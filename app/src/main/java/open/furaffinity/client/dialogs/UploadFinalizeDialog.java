package open.furaffinity.client.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.pages.ControlsFoldersSubmissions;
import open.furaffinity.client.submitPages.SubmitSubmissionPart2;
import open.furaffinity.client.submitPages.SubmitSubmissionPart3;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class UploadFinalizeDialog extends DialogFragment {
    private final SubmitSubmissionPart2 page;

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
    private EditText assignToNewFolder;

    private final List<String> folderNames = new ArrayList<>();
    private final List<String> folderKeys = new ArrayList<>();

    private CharSequence[] folderItems = new CharSequence[0];
    private boolean[] folderCheckedStates = new boolean[0];

    public UploadFinalizeDialog(SubmitSubmissionPart2 page) {
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
        assignToNewFolder = rootView.findViewById(R.id.assignToANewFolder);
    }

    private void initClientAndPage() {
        new ControlsFoldersSubmissions(requireContext(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                for (HashMap<String, String> currentFolder
                    :((ControlsFoldersSubmissions) abstractPage).getFolders()) {
                    if (currentFolder.containsKey("name")
                        && currentFolder.containsKey("upfolder_id")) {
                        folderNames.add(currentFolder.get("name"));
                        folderKeys.add(currentFolder.get("upfolder_id"));
                    }
                }

                folderItems = folderNames.toArray(new CharSequence[folderNames.size()]);
                folderCheckedStates = new boolean[folderNames.size()];
                UiControls.setSpinnerText(requireContext(),
                    assignToFolders, "No items selected");
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                Toast.makeText(requireContext(), "Failed to get existing folder list",
                    Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void updateUiElements() {
        UiControls.spinnerSetAdapter(requireContext(), cat, page.getCat(), "", true, false);
        UiControls.spinnerSetAdapter(requireContext(), aType, page.getaType(), "", true, false);
        UiControls.spinnerSetAdapter(requireContext(), species, page.getSpecies(), "", true, false);
        UiControls.spinnerSetAdapter(requireContext(), gender, page.getGender(), "", true, false);
        UiControls.spinnerSetAdapter(requireContext(), rating, page.getRating(), "", true, false);
    }

    private void updateUiElementListeners() {
        // Just need to have it display the options, track which are selected, and maybe update
        // the spinner so it should a selected count. Also setOnTouchListener with the oddness as
        // it gets mad if I use onClick
        assignToFolders.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMultiChoiceItems(folderItems, folderCheckedStates,
                    (dialog, which, isChecked) -> {
                    });
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    int selectedFolderCount = 0;
                    for (boolean currentFolder : folderCheckedStates) {
                        if (currentFolder) {
                            selectedFolderCount++;
                        }
                    }
                    UiControls.setSpinnerText(requireContext(),
                        assignToFolders, selectedFolderCount + " items selected");
                });
                builder.create();
                builder.show();
            }
            return false;
        });
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View rootView = requireActivity()
            .getLayoutInflater()
            .inflate(R.layout.dialog_fragment_uploadfinalizedialog, null);
        getElements(rootView);
        initClientAndPage();
        updateUiElements();
        updateUiElementListeners();

        final Context context = requireContext();

        builder.setView(rootView);

        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> {
            final List<String> folderIds = new ArrayList<>();
            for (int index = 0; index < folderCheckedStates.length; index++) {
                if (folderCheckedStates[index]) {
                    folderIds.add(folderKeys.get(index));
                }
            }

            new SubmitSubmissionPart3(context, new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    Toast.makeText(context, "Successfully uploaded submission", Toast.LENGTH_SHORT)
                        .show();
                    UploadFinalizeDialog.this.dismiss();
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    Toast.makeText(context, "Failed to upload submission step 3",
                        Toast.LENGTH_SHORT).show();
                    UploadFinalizeDialog.this.dismiss();
                }
            }, page.getSubmissionKey(), KvPair.getSelectedValue(cat),
                KvPair.getSelectedValue(aType), KvPair.getSelectedValue(species),
                KvPair.getSelectedValue(gender), KvPair.getSelectedValue(rating),
                title.getText().toString(), description.getText().toString(),
                keywords.getText().toString(), disableComments.isChecked(), putInScraps.isChecked(),
                folderIds, assignToNewFolder.getText().toString()).execute();
        });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> {

        });

        return builder.create();
    }
}
