package open.furaffinity.client.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.submitPages.SubmitSubmissionPart1;
import open.furaffinity.client.submitPages.SubmitSubmissionPart2;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class UploadDialog extends DialogFragment {
    private static final List<String> ImageMimeTypes =
        Arrays.asList("image/jpeg", "image/png", "image/gif");
    private static final List<String> TextMimeTypes = Arrays.asList("text/plain",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.oasis.opendocument.text", "text/rtf", "application/pdf");
    private static final List<String> AudioMimeTypes =
        Arrays.asList("audio/mpeg", "audio/x-wav", "audio/midi");
    private static final List<String> AllMimeTypes =
        Stream.concat(Stream.concat(ImageMimeTypes.stream(), TextMimeTypes.stream()),
            AudioMimeTypes.stream()).collect(Collectors.toList());

    private static final int SubmissionFileRequestCode = 132;
    private static final int ThumbnailFileRequestCode = 133;

    private Spinner submissionType;
    private Button selectSourceFile;
    private TextView sourceFileName;
    private String sourceFilePath;
    private Button selectThumbnailFile;
    private TextView thumbnailFileName;
    private String thumbnailFilePath;

    private SubmitSubmissionPart1 page;

    private void getElements(View rootView) {
        submissionType = rootView.findViewById(R.id.submissionType);
        selectSourceFile = rootView.findViewById(R.id.selectSourceFile);
        sourceFileName = rootView.findViewById(R.id.sourceFileName);
        selectThumbnailFile = rootView.findViewById(R.id.selectThumbnailFile);
        thumbnailFileName = rootView.findViewById(R.id.thumbnailFileName);
    }

    private void updateUiElements() {
        UiControls.spinnerSetAdapter(requireContext(), submissionType, page.getSubmissionType(),
            page.getSubmissionTypeCurrent(), true, false);
    }

    private void initClientAndPage() {
        page = new SubmitSubmissionPart1(requireContext(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                updateUiElements();
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                Toast.makeText(requireContext(), "Failed to upload submission step 1",
                    Toast.LENGTH_SHORT).show();
                UploadDialog.this.dismiss();
            }
        });
    }

    private void fetchPageData() {
        page = new SubmitSubmissionPart1(page);
        page.execute();
    }

    private void updateUiElementListeners() {
        submissionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                page.setSubmissionTypeCurrent(((KvPair) submissionType.getSelectedItem()).getKey());
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectSourceFile.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                switch (((KvPair) submissionType.getSelectedItem()).getKey()) {
                    case "submission":
                        intent.setType(ImageMimeTypes.stream().collect(Collectors.joining(",")));
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, ImageMimeTypes.toArray());
                        break;
                    case "story":
                    case "poetry":
                        intent.setType(TextMimeTypes.stream().collect(Collectors.joining(",")));
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, TextMimeTypes.toArray());
                        break;
                    case "music":
                        intent.setType(AudioMimeTypes.stream().collect(Collectors.joining(",")));
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, AudioMimeTypes.toArray());
                        break;
                    default:
                        intent.setType(AllMimeTypes.stream().collect(Collectors.joining(",")));
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, AllMimeTypes.toArray());
                        break;
                }

                startActivityForResult(intent, SubmissionFileRequestCode);
            }
            else {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        });

        selectThumbnailFile.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(ImageMimeTypes.stream().collect(Collectors.joining(",")));
                intent.putExtra(Intent.EXTRA_MIME_TYPES, ImageMimeTypes.toArray());
                startActivityForResult(intent, ThumbnailFileRequestCode);
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
            .inflate(R.layout.dialog_fragment_uploaddialog, null);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUiElements();
        updateUiElementListeners();

        final Context context = requireContext();
        final FragmentManager fragmentManager = getParentFragmentManager();

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton,
            (dialog, which) -> {
                new SubmitSubmissionPart2(context, new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        final UploadFinalizeDialog uploadFinalizeDialog = new UploadFinalizeDialog(
                            (SubmitSubmissionPart2) abstractPage);
                        uploadFinalizeDialog.show(fragmentManager, "uploadFinalizeDialog");
                        UploadDialog.this.dismiss();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(
                            context,
                            "Failed to upload submission step 2",
                            Toast.LENGTH_SHORT).show();
                        UploadDialog.this.dismiss();
                    }
                }, page, sourceFilePath, thumbnailFilePath).execute();
            });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> {

        });

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
            case ThumbnailFileRequestCode:
                if (resultCode == Activity.RESULT_OK) {
                    final Uri selectedFile = data.getData();
                    final Cursor sourceFileCursor =
                        requireContext().getContentResolver().query(
                            selectedFile,
                            null,
                            null,
                            null);
                    if (sourceFileCursor.moveToFirst()) {
                        thumbnailFilePath = selectedFile.toString();
                        thumbnailFileName.setText(
                            sourceFileCursor.getString(
                                sourceFileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                    }
                    else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to find file info",
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
            default:
                break;
        }
    }
}
