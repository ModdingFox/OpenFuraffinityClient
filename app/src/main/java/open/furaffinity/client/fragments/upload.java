package open.furaffinity.client.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class upload extends Fragment {
    private static final String TAG = upload.class.getName();
    private static final int submissionFileRequestCode = 132;
    private static final int thumbnailFileRequestCode = 133;

    private LinearLayout linearLayout;

    private LinearLayout uploadSubmission;
    private Spinner submissionType;
    private Button selectSourceFile;
    private TextView sourceFilePath;
    private Button selectThumbnailFile;
    private TextView thumbnailFilePath;
    private LinearLayout finalize;
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

    private fabCircular fab;

    private webClient webClient;
    private open.furaffinity.client.pages.submitSubmission page;

    private void getElements(View rootView) {
        linearLayout = rootView.findViewById(R.id.linearLayout);

        uploadSubmission = rootView.findViewById(R.id.uploadSubmission);
        submissionType = rootView.findViewById(R.id.submissionType);
        selectSourceFile = rootView.findViewById(R.id.selectSourceFile);
        sourceFilePath = rootView.findViewById(R.id.sourceFilePath);
        selectThumbnailFile = rootView.findViewById(R.id.selectThumbnailFile);
        thumbnailFilePath = rootView.findViewById(R.id.thumbnailFilePath);
        finalize = rootView.findViewById(R.id.finalize);
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

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_upload);
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
        page = new open.furaffinity.client.pages.submitSubmission();
    }

    private void fetchPageData() {
        page = new open.furaffinity.client.pages.submitSubmission();
        try {
            page.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "loadPage: ", e);
        }
    }

    private void updateUIElements() {
        if(page.getSubmissionType().size() > 0) {
            uiControls.spinnerSetAdapter(requireContext(), submissionType, page.getSubmissionType(), page.getSubmissionTypeCurrent(), true, false);
            uploadSubmission.setVisibility(View.VISIBLE);
        } else {
            uploadSubmission.setVisibility(View.GONE);
        }
    }

    private void updateUIElementListeners(View rootView) {
        submissionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //part=2&submission_type=submission
                //https://www.furaffinity.net/submit/
                //gotta get key needed for send
                updateUIElements();
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
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

        updateUIElements();
    }
}
