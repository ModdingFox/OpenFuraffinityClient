package open.furaffinity.client.dialogs;




import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import open.furaffinity.client.R;

public class uploadAvatarDialog extends DialogFragment {
    private static final List<String> imageMimeTypes = Arrays.asList(new String [] { "image/jpeg", "image/png", "image/gif" });

    private static final int submissionFileRequestCode = 132;

    private Button selectSourceFile;
    private TextView sourceFileName;
    private String sourceFilePath;
    private uploadAvatarDialogListener listener;

    public void setListener(uploadAvatarDialogListener uploadAvatarDialogListener) {
        listener = uploadAvatarDialogListener;
    }

    private void getElements(View rootView) {
        selectSourceFile = rootView.findViewById(R.id.selectSourceFile);
        sourceFileName = rootView.findViewById(R.id.sourceFileName);
    }

    private void initClientAndPage() {
    }

    private void updateUIElementListeners() {
        Fragment uploadFrag = this;
        selectSourceFile.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(imageMimeTypes.stream().collect(Collectors.joining(",")));
                intent.putExtra(Intent.EXTRA_MIME_TYPES, imageMimeTypes.toArray());
                startActivityForResult(intent, submissionFileRequestCode);
            } else {
                String [] permissions = { Manifest.permission.READ_EXTERNAL_STORAGE };
                requestPermissions(permissions, 0);
            }
        });
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
            listener.onDialogPositiveClick(sourceFilePath);
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
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedFile = data.getData();
                    Cursor sourceFileCursor = requireContext().getContentResolver().query(selectedFile, null, null, null);
                    if(sourceFileCursor.moveToFirst()) {
                        sourceFilePath = selectedFile.toString();
                        int displayNameColumnIndex = sourceFileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        String displayNameString = sourceFileCursor.getString(displayNameColumnIndex);
                        sourceFileName.setText(displayNameString);
                    } else {
                        Toast.makeText(requireContext(), "Failed to find file info", Toast.LENGTH_SHORT).show();
                        sourceFilePath = null;
                        sourceFileName.setText("");
                    }
                } else {
                    sourceFilePath = null;
                    sourceFileName.setText("");
                }
                break;
        }
    }

    public interface uploadAvatarDialogListener {
        void onDialogPositiveClick(String filePath);
    }
}
