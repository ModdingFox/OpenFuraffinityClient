package open.furaffinity.client.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class downloadContent {

    public static void downloadSubmission(Activity activity, Context context, open.furaffinity.client.pages.view page) {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(page.getDownload());

            //noinspection ResultOfMethodCallIgnored
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();

            Matcher fileNameMatcher = Pattern.compile("/([^/]+)/([^/]+)/([^/]+)/([^/]+)/([^/]+)$").matcher(page.getDownload());

            if (fileNameMatcher.find()) {
                String fileName = fileNameMatcher.group(5);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(page.getSubmissionTitle() + " by " + page.getSubmissionUser());
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(true);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                downloadManager.enqueue(request);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("File naming error. Aborting download.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } else {
            String [] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
            activity.requestPermissions(permissions, 0);
        }

    }
}
