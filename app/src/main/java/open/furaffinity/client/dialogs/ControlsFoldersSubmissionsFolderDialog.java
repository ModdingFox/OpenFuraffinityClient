package open.furaffinity.client.dialogs;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class ControlsFoldersSubmissionsFolderDialog extends DialogFragment {
    private Spinner spinner;
    private EditText groupNameEditText;
    private EditText folderNameEditText;
    private EditText descriptionEditText;

    private HashMap<String, String> data;
    private String spinnerSelected = "";
    private String folderName = "";
    private String description = "";
    private ControlsFoldersSubmissionsFolderDialogListener listener;

    public void setListener(
        ControlsFoldersSubmissionsFolderDialogListener
            controlsFoldersSubmissionsFolderDialogListener) {
        listener = controlsFoldersSubmissionsFolderDialogListener;
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View rootView = requireActivity()
            .getLayoutInflater()
            .inflate(R.layout.folders_submissions_folder, null);

        spinner = rootView.findViewById(R.id.spinner);
        groupNameEditText = rootView.findViewById(R.id.groupNameEditText);
        folderNameEditText = rootView.findViewById(R.id.folderNameEditText);
        descriptionEditText = rootView.findViewById(R.id.descriptionEditText);

        UiControls.spinnerSetAdapter(getContext(), spinner, data,
            spinnerSelected, true, false);
        folderNameEditText.setText(folderName);
        descriptionEditText.setText(description);

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> {
            listener.onDialogPositiveClick(((KvPair) spinner.getSelectedItem()).getKey(),
                groupNameEditText.getText().toString(), folderNameEditText.getText().toString(),
                descriptionEditText.getText().toString());
            dismiss();
        });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> dismiss());

        return builder.create();
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public void setSpinnerSelected(String spinnerSelected) {
        if (spinnerSelected != null) {
            this.spinnerSelected = spinnerSelected;
        }
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public interface ControlsFoldersSubmissionsFolderDialogListener {
        void onDialogPositiveClick(String spinnerSelected, String groupName, String folderName,
                                   String description);
    }
}
