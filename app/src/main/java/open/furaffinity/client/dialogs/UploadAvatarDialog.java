package open.furaffinity.client.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.R;

public class UploadAvatarDialog extends DialogFragment {
    private static final List<String> ImageMimeTypes =
        Arrays.asList("image/jpeg", "image/png", "image/gif");

    private static final int SubmissionFileRequestCode = 132;

    private Button selectSourceFile;
    private TextView sourceFileName;
    private String sourceFilePath;
    private UploadAvatarDialogListener listener;

    public void setListener(UploadAvatarDialogListener uploadAvatarDialogListener) {
        listener = uploadAvatarDialogListener;
    }

    private void getElements(View rootView) {
        selectSourceFile = rootView.findViewById(R.id.selectSourceFile);
        sourceFileName = rootView.findViewById(R.id.sourceFileName);
    }

    private void initClientAndPage() {
    }

    private void updateUiElementListeners() {
        selectSourceFile.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(ImageMimeTypes.stream().collect(Collectors.joining(",")));
                intent.putExtra(Intent.EXTRA_MIME_TYPES, ImageMimeTypes.toArray());
                startActivityForResult(intent, SubmissionFileRequestCode);
            }
            else {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        });
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View rootView = requireActivity()
            .getLayoutInflater()
            .inflate(R.layout.dialog_fragment_avataruploaddialog, null);
        getElements(rootView);
        initClientAndPage();
        updateUiElementListeners();

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> {
            listener.onDialogPositiveClick(sourceFilePath);
            dismiss();
        });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> dismiss());

        return builder.create();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SubmissionFileRequestCode:
                if (resultCode == Activity.RESULT_OK) {
                    final Uri selectedFile = data.getData();
                    final Cursor sourceFileCursor =
                        requireContext().getContentResolver().query(
                            selectedFile,
                            null,
                            null,
                            null);
                    if (sourceFileCursor.moveToFirst()) {
                        sourceFilePath = selectedFile.toString();
                        sourceFileName.setText(
                            sourceFileCursor.getString(
                                sourceFileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                    }
                    else {
                        Toast.makeText(requireContext(), "Failed to find file info",
                            Toast.LENGTH_SHORT).show();
                        sourceFilePath = null;
                        sourceFileName.setText("");
                    }
                }
                else {
                    sourceFilePath = null;
                    sourceFileName.setText("");
                }
                break;
            default:
                break;
        }
    }

    public interface UploadAvatarDialogListener {
        void onDialogPositiveClick(String filePath);
    }
}
