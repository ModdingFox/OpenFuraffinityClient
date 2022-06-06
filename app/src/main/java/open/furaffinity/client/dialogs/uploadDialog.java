package open.furaffinity.client.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.submitPages.submitSubmissionPart1;
import open.furaffinity.client.submitPages.submitSubmissionPart2;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;

public class uploadDialog extends DialogFragment {
    private static final List<String> imageMimeTypes =
        Arrays.asList("image/jpeg", "image/png", "image/gif");
    private static final List<String> textMimeTypes = Arrays.asList("text/plain",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.oasis.opendocument.text", "text/rtf", "application/pdf");
    private static final List<String> audioMimeTypes =
        Arrays.asList("audio/mpeg", "audio/x-wav", "audio/midi");
    private static final List<String> allMimeTypes =
        Stream.concat(Stream.concat(imageMimeTypes.stream(), textMimeTypes.stream()),
            audioMimeTypes.stream()).collect(Collectors.toList());

    private static final int submissionFileRequestCode = 132;
    private static final int thumbnailFileRequestCode = 133;

    private Spinner submissionType;
    private Button selectSourceFile;
    private TextView sourceFileName;
    private String sourceFilePath;
    private Button selectThumbnailFile;
    private TextView thumbnailFileName;
    private String thumbnailFilePath;

    private submitSubmissionPart1 page;

    private void getElements(View rootView) {
        submissionType = rootView.findViewById(R.id.submissionType);
        selectSourceFile = rootView.findViewById(R.id.selectSourceFile);
        sourceFileName = rootView.findViewById(R.id.sourceFileName);
        selectThumbnailFile = rootView.findViewById(R.id.selectThumbnailFile);
        thumbnailFileName = rootView.findViewById(R.id.thumbnailFileName);
    }

    private void updateUIElements() {
        uiControls.spinnerSetAdapter(requireContext(), submissionType, page.getSubmissionType(),
            page.getSubmissionTypeCurrent(), true, false);
    }

    private void initClientAndPage() {
        page = new submitSubmissionPart1(requireContext(), new abstractPage.pageListener() {
            @Override public void requestSucceeded(abstractPage abstractPage) {
                updateUIElements();
            }

            @Override public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(requireContext(), "Failed to upload submission step 1",
                    Toast.LENGTH_SHORT).show();
                uploadDialog.this.dismiss();
            }
        });
    }

    private void fetchPageData() {
        page = new submitSubmissionPart1(page);
        page.execute();
    }

    private void updateUIElementListeners() {
        submissionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                page.setSubmissionTypeCurrent(((kvPair) submissionType.getSelectedItem()).getKey());
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectSourceFile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                switch (((kvPair) submissionType.getSelectedItem()).getKey()) {
                    case "submission":
                        intent.setType(imageMimeTypes.stream().collect(Collectors.joining(",")));
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, imageMimeTypes.toArray());
                        break;
                    case "story":
                    case "poetry":
                        intent.setType(textMimeTypes.stream().collect(Collectors.joining(",")));
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, textMimeTypes.toArray());
                        break;
                    case "music":
                        intent.setType(audioMimeTypes.stream().collect(Collectors.joining(",")));
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, audioMimeTypes.toArray());
                        break;
                    default:
                        intent.setType(allMimeTypes.stream().collect(Collectors.joining(",")));
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, allMimeTypes.toArray());
                        break;
                }

                startActivityForResult(intent, submissionFileRequestCode);
            }
            else {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, 0);
            }
        });

        selectThumbnailFile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(imageMimeTypes.stream().collect(Collectors.joining(",")));
                intent.putExtra(Intent.EXTRA_MIME_TYPES, imageMimeTypes.toArray());
                startActivityForResult(intent, thumbnailFileRequestCode);
            }
            else {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, 0);
            }
        });
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_fragment_uploaddialog, null);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners();

        Context context = requireContext();
        FragmentManager fragmentManager = getParentFragmentManager();

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton,
            (dialog, which) -> new submitSubmissionPart2(context, new abstractPage.pageListener() {
                @Override public void requestSucceeded(abstractPage abstractPage) {
                    uploadFinalizeDialog uploadFinalizeDialog =
                        new uploadFinalizeDialog(((submitSubmissionPart2) abstractPage));
                    uploadFinalizeDialog.show(fragmentManager, "uploadFinalizeDialog");
                    uploadDialog.this.dismiss();
                }

                @Override public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(context, "Failed to upload submission step 2",
                        Toast.LENGTH_SHORT).show();
                    uploadDialog.this.dismiss();
                }
            }, page, sourceFilePath, thumbnailFilePath).execute());
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> {

        });

        return builder.create();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case submissionFileRequestCode:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedFile = data.getData();
                    Cursor sourceFileCursor =
                        requireContext().getContentResolver().query(selectedFile, null, null, null);
                    if (sourceFileCursor.moveToFirst()) {
                        sourceFilePath = selectedFile.toString();
                        int displayNameColumnIndex =
                            sourceFileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        String displayNameString =
                            sourceFileCursor.getString(displayNameColumnIndex);
                        sourceFileName.setText(displayNameString);
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
            case thumbnailFileRequestCode:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedFile = data.getData();
                    Cursor sourceFileCursor =
                        requireContext().getContentResolver().query(selectedFile, null, null, null);
                    if (sourceFileCursor.moveToFirst()) {
                        thumbnailFilePath = selectedFile.toString();
                        int displayNameColumnIndex =
                            sourceFileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        String displayNameString =
                            sourceFileCursor.getString(displayNameColumnIndex);
                        thumbnailFileName.setText(displayNameString);
                    }
                    else {
                        Toast.makeText(requireContext(), "Failed to find file info",
                            Toast.LENGTH_SHORT).show();
                        thumbnailFilePath = null;
                        thumbnailFileName.setText("");
                    }
                }
                else {
                    thumbnailFilePath = null;
                    thumbnailFileName.setText("");
                }
                break;
        }
    }
}
