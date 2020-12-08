package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import open.furaffinity.client.R;

public class uploadAvatarDialog extends DialogFragment {
    private static final int submissionFileRequestCode = 132;

    private Button selectSourceFile;
    private TextView sourceFilePath;
    private uploadAvatarDialogListener listener;

    public void setListener(uploadAvatarDialogListener uploadAvatarDialogListener) {
        listener = uploadAvatarDialogListener;
    }

    private void getElements(View rootView) {
        selectSourceFile = rootView.findViewById(R.id.selectSourceFile);
        sourceFilePath = rootView.findViewById(R.id.sourceFilePath);
    }

    private void initClientAndPage() {
    }

    private void updateUIElementListeners() {
        Fragment uploadFrag = this;
        selectSourceFile.setOnClickListener(v -> new MaterialFilePicker()
                .withSupportFragment(uploadFrag)
                .withPath(Environment.getRootDirectory().getPath())
                .withFilterDirectories(false)
                .withRequestCode(submissionFileRequestCode)
                .start());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_fragment_avataruploaddialog, null);
        getElements(rootView);
        initClientAndPage();
        updateUIElementListeners();

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> {
            listener.onDialogPositiveClick(sourceFilePath.getText().toString());
            dismiss();
        });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> dismiss());

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case submissionFileRequestCode:
                if (resultCode == FilePickerActivity.RESULT_OK) {
                    sourceFilePath.setText(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                } else {
                    sourceFilePath.setText("");
                }
                break;
        }
    }

    public interface uploadAvatarDialogListener {
        void onDialogPositiveClick(String filePath);
    }
}
