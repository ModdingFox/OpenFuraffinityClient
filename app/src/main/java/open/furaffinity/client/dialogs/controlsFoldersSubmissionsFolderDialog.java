package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.kvPair;

public class controlsFoldersSubmissionsFolderDialog extends DialogFragment {
    private String TAG = controlsFoldersSubmissionsFolderDialog.class.getName();

    private Spinner spinner;
    private EditText groupNameEditText;
    private EditText folderNameEditText;
    private EditText descriptionEditText;

    private HashMap<String, String> data;
    private String spinnerSelected = "";
    private String folderName = "";
    private String description = "";

    public interface controlsFoldersSubmissionsFolderDialogListener {
        public void onDialogPositiveClick(String spinnerSelected, String groupName, String folderName, String description);
    }

    private controlsFoldersSubmissionsFolderDialogListener listener;

    public void setListener(controlsFoldersSubmissionsFolderDialogListener controlsFoldersSubmissionsFolderDialogListener) {
        listener = controlsFoldersSubmissionsFolderDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.folders_submissions_folder, null);

        spinner = rootView.findViewById(R.id.spinner);
        groupNameEditText = rootView.findViewById(R.id.groupNameEditText);
        folderNameEditText = rootView.findViewById(R.id.folderNameEditText);
        descriptionEditText = rootView.findViewById(R.id.descriptionEditText);

        open.furaffinity.client.utilities.uiControls.spinnerSetAdapter(getContext(), spinner, data, spinnerSelected, true, false);
        folderNameEditText.setText(folderName);
        descriptionEditText.setText(description);

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogPositiveClick(((kvPair) spinner.getSelectedItem()).getKey(), groupNameEditText.getText().toString(), folderNameEditText.getText().toString(), descriptionEditText.getText().toString());
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

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
}
