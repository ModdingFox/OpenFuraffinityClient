package open.furaffinity.client.dialogs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.submitPages.submitSubmissionPart1;
import open.furaffinity.client.submitPages.submitSubmissionPart2;
import open.furaffinity.client.submitPages.submitSubmissionPart3;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;

public class uploadDialog extends DialogFragment {
    private static final int submissionFileRequestCode = 132;
    private static final int thumbnailFileRequestCode = 133;

    private Spinner submissionType;
    private Button selectSourceFile;
    private TextView sourceFilePath;
    private Button selectThumbnailFile;
    private TextView thumbnailFilePath;

    private submitSubmissionPart1 page;

    private void getElements(View rootView) {
        submissionType = rootView.findViewById(R.id.submissionType);
        selectSourceFile = rootView.findViewById(R.id.selectSourceFile);
        sourceFilePath = rootView.findViewById(R.id.sourceFilePath);
        selectThumbnailFile = rootView.findViewById(R.id.selectThumbnailFile);
        thumbnailFilePath = rootView.findViewById(R.id.thumbnailFilePath);
    }

    private void updateUIElements() {
        uiControls.spinnerSetAdapter(requireContext(), submissionType, page.getSubmissionType(), page.getSubmissionTypeCurrent(), true, false);
    }

    private void initClientAndPage() {
        page = new submitSubmissionPart1(requireContext(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                updateUIElements();
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(requireContext(), "Failed to upload submission step 1", Toast.LENGTH_SHORT).show();
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Fragment uploadFrag = this;
        selectSourceFile.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                new MaterialFilePicker()
                        .withSupportFragment(uploadFrag)
                        .withPath(Environment.getRootDirectory().getPath())
                        .withFilterDirectories(false)
                        .withRequestCode(submissionFileRequestCode)
                        .start();
            } else {
                String [] permissions = { Manifest.permission.READ_EXTERNAL_STORAGE };
                requestPermissions(permissions, 0);
            }
        });

        selectThumbnailFile.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                new MaterialFilePicker()
                        .withSupportFragment(uploadFrag)
                        .withPath(Environment.getRootDirectory().getPath())
                        .withFilterDirectories(false)
                        .withRequestCode(thumbnailFileRequestCode)
                        .start();
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

        View rootView = inflater.inflate(R.layout.dialog_fragment_uploaddialog, null);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners();

        Context context = requireContext();
        FragmentManager fragmentManager = getParentFragmentManager();

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> new submitSubmissionPart2(context, new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                new submitSubmissionPart3(context, new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(open.furaffinity.client.abstractClasses.abstractPage abstractPage) {
                        uploadFinalizeDialog uploadFinalizeDialog = new uploadFinalizeDialog(((submitSubmissionPart3) abstractPage));
                        uploadFinalizeDialog.show(fragmentManager, "uploadFinalizeDialog");
                        uploadDialog.this.dismiss();
                    }

                    @Override
                    public void requestFailed(open.furaffinity.client.abstractClasses.abstractPage abstractPage) {
                        Toast.makeText(context, "Failed to upload submission step 3", Toast.LENGTH_SHORT).show();
                        uploadDialog.this.dismiss();
                    }
                }, ((submitSubmissionPart2) abstractPage), sourceFilePath.getText().toString(), thumbnailFilePath.getText().toString()).execute();
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(context, "Failed to upload submission step 2", Toast.LENGTH_SHORT).show();
                uploadDialog.this.dismiss();
            }
        }, page).execute());
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> {

        });

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
            case thumbnailFileRequestCode:
                if (resultCode == FilePickerActivity.RESULT_OK) {
                    thumbnailFilePath.setText(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                } else {
                    thumbnailFilePath.setText("");
                }
                break;
        }
    }
}
