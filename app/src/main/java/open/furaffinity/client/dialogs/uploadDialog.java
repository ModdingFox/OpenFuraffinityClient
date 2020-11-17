package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class uploadDialog extends DialogFragment {
    private String TAG = uploadDialog.class.getName();
    private static final int submissionFileRequestCode = 132;
    private static final int thumbnailFileRequestCode = 133;

    private Spinner submissionType;
    private Button selectSourceFile;
    private TextView sourceFilePath;
    private Button selectThumbnailFile;
    private TextView thumbnailFilePath;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.submitSubmissionPart1 page;

    private void getElements(View rootView) {
        submissionType = rootView.findViewById(R.id.submissionType);
        selectSourceFile = rootView.findViewById(R.id.selectSourceFile);
        sourceFilePath = rootView.findViewById(R.id.sourceFilePath);
        selectThumbnailFile = rootView.findViewById(R.id.selectThumbnailFile);
        thumbnailFilePath = rootView.findViewById(R.id.thumbnailFilePath);
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
        page = new open.furaffinity.client.pages.submitSubmissionPart1();
    }

    private void fetchPageData() {
        page = new open.furaffinity.client.pages.submitSubmissionPart1();
        try {
            page.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "loadPage: ", e);
        }
    }

    private void updateUIElements() {
        uiControls.spinnerSetAdapter(requireContext(), submissionType, page.getSubmissionType(), page.getSubmissionTypeCurrent(), true, false);
    }

    private void updateUIElementListeners(View rootView) {
        submissionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                page.setSubmissionTypeCurrent(((kvPair)submissionType.getSelectedItem()).getKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Fragment uploadFrag = this;
        selectSourceFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withSupportFragment(uploadFrag)
                        .withPath(Environment.getRootDirectory().getPath())
                        .withFilterDirectories(false)
                        .withRequestCode(submissionFileRequestCode)
                        .start();
            }
        });

        selectThumbnailFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withSupportFragment(uploadFrag)
                        .withPath(Environment.getRootDirectory().getPath())
                        .withFilterDirectories(false)
                        .withRequestCode(thumbnailFileRequestCode)
                        .start();
            }
        });
    }

    private View rootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        rootView = inflater.inflate(R.layout.dialog_fragment_uploaddialog, null);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                open.furaffinity.client.pages.submitSubmissionPart2 page2 = new open.furaffinity.client.pages.submitSubmissionPart2(page);
                try {
                    page2.execute(webClient).get();

                    open.furaffinity.client.pages.submitSubmissionPart3 page3 = new open.furaffinity.client.pages.submitSubmissionPart3(page2, sourceFilePath.getText().toString(), thumbnailFilePath.getText().toString());
                    page3.execute(webClient).get();
                    getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new open.furaffinity.client.fragments.upload(page3), "upload").addToBackStack("upload").commit();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "loadPage: ", e);
                }
            }
        });
        builder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case submissionFileRequestCode:
                if(resultCode == FilePickerActivity.RESULT_OK) {
                    sourceFilePath.setText(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                } else {
                    sourceFilePath.setText("");
                }
                break;
            case thumbnailFileRequestCode:
                if(resultCode == FilePickerActivity.RESULT_OK) {
                    thumbnailFilePath.setText(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                } else {
                    thumbnailFilePath.setText("");
                }
                break;
        }
    }
}
