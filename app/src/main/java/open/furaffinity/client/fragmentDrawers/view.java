package open.furaffinity.client.fragmentDrawers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.chrisbanes.photoview.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.rtfparserkit.converter.text.StringTextConverter;
import com.rtfparserkit.parser.RtfStreamSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.viewSectionsPagerAdapter;
import open.furaffinity.client.pages.loginCheck;
import open.furaffinity.client.sqlite.historyContract.historyItemEntry;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.utilities.fabCircular;

import static open.furaffinity.client.utilities.sendPm.sendPM;

public class view extends appFragment {
    @SuppressWarnings("FieldCanBeLocal")
    private ConstraintLayout constraintLayout;

    private TextView submissionTitle;
    private PhotoView submissionImage;
    private PDFView submissionPDF;
    private ConstraintLayout submissionInfo;
    private LinearLayout submissionUserLinearLayout;
    private ImageView submissionUserIcon;
    private TextView submissionUser;
    private ViewPager viewPager;
    private TabLayout tabs;

    private MediaPlayer submissionMediaPlayer;
    private Handler submissionMediaPlayerHandler = new Handler();

    private ConstraintLayout submissionMediaPlayerConstraintLayout;
    private ImageView submissionMediaPlayerSubmissionImage;
    private TextView submissionMediaPlayerCurrentTime;
    private TextView submissionMediaPlayerRemainingTime;
    private SeekBar submissionMediaPlayerSeekBar;
    private Button submissionMediaPlayerRewind;
    private Button submissionMediaPlayerPlayPause;
    private Button submissionMediaPlayerRepeat;
    private Button submissionMediaPlayerFastForward;

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            int currentTime = submissionMediaPlayer.getCurrentPosition();
            int remainingTime = submissionMediaPlayer.getDuration() - currentTime;

            if(!submissionMediaPlayer.isPlaying() && !submissionMediaPlayerPlayPause.getText().equals("Play")){
                submissionMediaPlayerPlayPause.setText("Play");
            }

            submissionMediaPlayerCurrentTime.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes((long)currentTime), TimeUnit.MILLISECONDS.toSeconds((long)currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)currentTime))));
            submissionMediaPlayerRemainingTime.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes((long)remainingTime), TimeUnit.MILLISECONDS.toSeconds((long)remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)remainingTime))));
            submissionMediaPlayerSeekBar.setProgress(currentTime);
            submissionMediaPlayerHandler.postDelayed(this, 100);
        }
    };

    private fabCircular fab;
    private FloatingActionButton submissionFavorite;
    private FloatingActionButton submissionDownload;
    private FloatingActionButton sendNote;
    private FloatingActionButton shareLink;
    private FloatingActionButton imageInfoSwitch;

    private loginCheck loginCheck;
    private open.furaffinity.client.pages.view page;
    private boolean isLoading = false;
    private final abstractPage.pageListener pageListener = new abstractPage.pageListener() {
        @Override
        public void requestSucceeded(abstractPage abstractPage) {
            submissionTitle.setText(((open.furaffinity.client.pages.view) abstractPage).getSubmissionTitle());

            switch(((open.furaffinity.client.pages.view) abstractPage).getSubmissionMimeType()){
                case "text/plain":
                    new AsyncTask<Void, Void, InputStream>() {
                        @Override
                        protected InputStream doInBackground(Void... voids) {
                            try {
                                InputStream inputStream = new URL(((open.furaffinity.client.pages.view) abstractPage).getDownload()).openStream();
                                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                                Document document = new Document();
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                PdfWriter.getInstance(document, byteArrayOutputStream);
                                document.open();

                                Font font = new Font();
                                font.setStyle(Font.NORMAL);
                                font.setSize(11);

                                String line;
                                while ((line = bufferedReader.readLine()) != null) {
                                    Paragraph paragraph = new Paragraph(line + "\n", font);
                                    paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
                                    document.add(paragraph);
                                }

                                document.close();

                                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                            } catch (IOException | DocumentException e) {
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(InputStream inputStream) {
                            if(inputStream != null){
                                submissionPDF.fromStream(inputStream).fitEachPage(true).load();
                                submissionImage.setVisibility(View.GONE);
                                submissionPDF.setVisibility(View.VISIBLE);
                            } else {
                                Glide.with(view.this).load(((open.furaffinity.client.pages.view) abstractPage).getSubmissionImgLink()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(submissionImage);
                                Toast.makeText(getActivity(), "Failed to load pdf", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                    break;
                case "text/rtf":
                    new AsyncTask<Void, Void, InputStream>() {
                        @Override
                        protected InputStream doInBackground(Void... voids) {
                            try {
                                InputStream inputStream = new URL(((open.furaffinity.client.pages.view) abstractPage).getDownload()).openStream();
                                StringTextConverter stringTextConverter = new StringTextConverter();
                                stringTextConverter.convert(new RtfStreamSource(inputStream));
                                String extractedText = stringTextConverter.getText();

                                InputStream extractedTextInputStream = new ByteArrayInputStream(extractedText.getBytes(StandardCharsets.UTF_8));
                                InputStreamReader inputStreamReader = new InputStreamReader(extractedTextInputStream);
                                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                                Document document = new Document();
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                PdfWriter.getInstance(document, byteArrayOutputStream);
                                document.open();

                                Font font = new Font();
                                font.setStyle(Font.NORMAL);
                                font.setSize(11);
                                
                                String line;
                                while ((line = bufferedReader.readLine()) != null) {
                                    Paragraph paragraph = new Paragraph(line + "\n", font);
                                    paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
                                    document.add(paragraph);
                                }

                                document.close();

                                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                            } catch (IOException | DocumentException e) {
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(InputStream inputStream) {
                            if(inputStream != null){
                                submissionPDF.fromStream(inputStream).load();
                                submissionImage.setVisibility(View.GONE);
                                submissionPDF.setVisibility(View.VISIBLE);
                            } else {
                                Glide.with(view.this).load(((open.furaffinity.client.pages.view) abstractPage).getSubmissionImgLink()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(submissionImage);
                                Toast.makeText(getActivity(), "Failed to load pdf", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                    break;
                case "application/msword":
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                case "application/vnd.oasis.opendocument.text":
                    Glide.with(view.this).load(((open.furaffinity.client.pages.view) abstractPage).getSubmissionImgLink()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(submissionImage);
                    break;
                case "application/pdf":
                    new AsyncTask<Void, Void, InputStream>() {
                        @Override
                        protected InputStream doInBackground(Void... voids) {
                            try {
                                return new URL(((open.furaffinity.client.pages.view) abstractPage).getDownload()).openStream();
                            } catch (IOException e) {
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(InputStream inputStream) {
                            if(inputStream != null){
                                submissionPDF.fromStream(inputStream).fitEachPage(true).load();
                                submissionImage.setVisibility(View.GONE);
                                submissionPDF.setVisibility(View.VISIBLE);
                            } else {
                                Glide.with(view.this).load(((open.furaffinity.client.pages.view) abstractPage).getSubmissionImgLink()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(submissionImage);
                                Toast.makeText(getActivity(), "Failed to load pdf", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                    break;
                case "audio/mpeg":
                case "audio/x-wav":
                case "audio/midi":
                    Glide.with(view.this).load(((open.furaffinity.client.pages.view) abstractPage).getSubmissionImgLink()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(submissionMediaPlayerSubmissionImage);

                    if(submissionMediaPlayer != null){
                        submissionMediaPlayer.stop();
                    }
                    submissionMediaPlayer = new MediaPlayer();
                    submissionMediaPlayer = MediaPlayer.create(getContext(), Uri.parse(((open.furaffinity.client.pages.view) abstractPage).getDownload()));

                    submissionMediaPlayerSeekBar.setMax(submissionMediaPlayer.getDuration());
                    submissionMediaPlayerHandler.postDelayed(UpdateSongTime, 100);

                    submissionImage.setVisibility(View.GONE);
                    submissionMediaPlayerConstraintLayout.setVisibility(View.VISIBLE);
                    break;
                default:
                    Glide.with(view.this).load(((open.furaffinity.client.pages.view) abstractPage).getSubmissionImgLink()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(submissionImage);
                    break;
            }

            Glide.with(view.this).load(((open.furaffinity.client.pages.view) abstractPage).getSubmissionUserIcon()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(submissionUserIcon);
            submissionUser.setText(((open.furaffinity.client.pages.view) abstractPage).getSubmissionUser());

            if (((open.furaffinity.client.pages.view) abstractPage).getIsFav()) {
                submissionFavorite.setImageResource(R.drawable.ic_menu_favorite);
            } else {
                submissionFavorite.setImageResource(R.drawable.ic_menu_unfavorite);
            }

            saveHistory();
            setupViewPager(((open.furaffinity.client.pages.view) abstractPage));

            fab.setVisibility(View.VISIBLE);
            isLoading = false;
        }

        @Override
        public void requestFailed(abstractPage abstractPage) {
            fab.setVisibility(View.GONE);
            isLoading = false;
            Toast.makeText(getActivity(), "Failed to load data for view", Toast.LENGTH_SHORT).show();
        }
    };

    private void saveHistory() {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(requireActivity().getString(R.string.trackHistorySetting), settings.trackHistoryDefault)) {
            historyDBHelper dbHelper = new historyDBHelper(requireActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //Delete previous versions from history
            String selection = historyItemEntry.COLUMN_NAME_URL + " LIKE ?";
            String[] selectionArgs = {page.getPagePath()};
            db.delete(historyItemEntry.TABLE_NAME_VIEW, selection , selectionArgs);

            //Insert into history
            ContentValues values = new ContentValues();
            values.put(historyItemEntry.COLUMN_NAME_USER, page.getSubmissionUser());
            values.put(historyItemEntry.COLUMN_NAME_TITLE, page.getSubmissionTitle());
            values.put(historyItemEntry.COLUMN_NAME_URL, page.getPagePath());
            values.put(historyItemEntry.COLUMN_NAME_DATETIME, (new Date()).getTime());
            db.insert(historyItemEntry.TABLE_NAME_VIEW, null, values);

            //Limit history to 512 entries
            db.execSQL("DELETE FROM " + historyItemEntry.TABLE_NAME_VIEW + " WHERE rowid < (SELECT min(rowid) FROM (SELECT rowid FROM viewHistory ORDER BY rowid DESC LIMIT 512))");

            db.close();
        }

        ((mainActivity)requireActivity()).drawerFragmentPush(this.getClass().getName(), page.getPagePath());
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_view;
    }

    protected void getElements(View rootView) {
        constraintLayout = rootView.findViewById(R.id.constraintLayout);

        submissionTitle = rootView.findViewById(R.id.submissionTitle);
        submissionImage = rootView.findViewById(R.id.submissionImage);
        submissionPDF = rootView.findViewById(R.id.submissionPDF);
        submissionInfo = rootView.findViewById(R.id.submissionInfo);
        submissionUserLinearLayout = rootView.findViewById(R.id.submissionUserLinearLayout);
        submissionUserIcon = rootView.findViewById(R.id.submissionUserIcon);
        submissionUser = rootView.findViewById(R.id.submissionUser);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
        fab = rootView.findViewById(R.id.fab);

        submissionMediaPlayerConstraintLayout = rootView.findViewById(R.id.submissionMediaPlayerConstraintLayout);
        submissionMediaPlayerSubmissionImage = rootView.findViewById(R.id.submissionMediaPlayerSubmissionImage);
        submissionMediaPlayerCurrentTime = rootView.findViewById(R.id.submissionMediaPlayerCurrentTime);
        submissionMediaPlayerRemainingTime = rootView.findViewById(R.id.submissionMediaPlayerRemainingTime);
        submissionMediaPlayerSeekBar = rootView.findViewById(R.id.submissionMediaPlayerSeekBar);
        submissionMediaPlayerRewind = rootView.findViewById(R.id.submissionMediaPlayerRewind);
        submissionMediaPlayerPlayPause = rootView.findViewById(R.id.submissionMediaPlayerPlayPause);
        submissionMediaPlayerRepeat = rootView.findViewById(R.id.submissionMediaPlayerRepeat);
        submissionMediaPlayerFastForward = rootView.findViewById(R.id.submissionMediaPlayerFastForward);

        submissionFavorite = new FloatingActionButton(requireContext());
        submissionDownload = new FloatingActionButton(requireContext());
        sendNote = new FloatingActionButton(requireContext());
        shareLink = new FloatingActionButton(requireContext());
        imageInfoSwitch = new FloatingActionButton(requireContext());

        submissionFavorite.setImageResource(R.drawable.ic_menu_favorite);
        submissionDownload.setImageResource(R.drawable.ic_menu_download);
        sendNote.setImageResource(R.drawable.ic_menu_newmessage);
        shareLink.setImageResource(R.drawable.ic_menu_send);
        imageInfoSwitch.setImageResource(R.drawable.ic_menu_about);

        //noinspection deprecation
        submissionFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        submissionDownload.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        sendNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        shareLink.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        imageInfoSwitch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(submissionFavorite);
        constraintLayout.addView(submissionDownload);
        constraintLayout.addView(sendNote);
        constraintLayout.addView(shareLink);
        constraintLayout.addView(imageInfoSwitch);

        submissionFavorite.setVisibility(View.GONE);
        sendNote.setVisibility(View.GONE);

        fab.addButton(submissionDownload, 1.5f, 270);
        fab.addButton(shareLink, 2.6f, 270);
        fab.addButton(imageInfoSwitch, 3.7f, 270);
        fab.setVisibility(View.GONE);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;

            loginCheck = new loginCheck(loginCheck);
            loginCheck.execute();

            page = new open.furaffinity.client.pages.view(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    private void setupViewPager(open.furaffinity.client.pages.view page) {
        viewSectionsPagerAdapter sectionsPagerAdapter = new viewSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    protected void initPages() {
        String pagePath = ((mainActivity) requireActivity()).getViewPath();

        loginCheck = new loginCheck(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                fab.removeButton(submissionFavorite);
                fab.removeButton(sendNote);

                submissionFavorite.setVisibility(View.GONE);
                sendNote.setVisibility(View.GONE);

                if (((loginCheck) abstractPage).getIsLoggedIn()) {
                    fab.addButton(submissionFavorite, 1.5f, 180);
                    fab.addButton(sendNote, 1.5f, 225);
                }
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                fab.removeButton(submissionFavorite);
                fab.removeButton(sendNote);

                submissionFavorite.setVisibility(View.GONE);
                sendNote.setVisibility(View.GONE);

                Toast.makeText(getActivity(), "Failed to load data for loginCheck", Toast.LENGTH_SHORT).show();
            }
        });

        if (page == null) {
            page = new open.furaffinity.client.pages.view(getActivity(), pageListener, pagePath);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void updateUIElementListeners(View rootView) {
        submissionUserLinearLayout.setOnClickListener(v -> ((mainActivity) requireActivity()).setUserPath(page.getSubmissionUserPage()));

        submissionImage.setOnSingleFlingListener(new OnSingleFlingListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int SWIPE_THRESHOLD = 100;
                int SWIPE_VELOCITY_THRESHOLD = 100;
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }

            public void onSwipeRight() {
                if (page.getNext() != null) {
                    ((mainActivity) requireActivity()).setViewPath(page.getNext());
                }
            }

            public void onSwipeLeft() {
                if (page.getPrev() != null) {
                    ((mainActivity) requireActivity()).setViewPath(page.getPrev());
                }
            }
        });

        submissionMediaPlayerSubmissionImage.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    int SWIPE_THRESHOLD = 100;
                    int SWIPE_VELOCITY_THRESHOLD = 100;
                    boolean result = false;
                    try {
                        float diffY = e2.getY() - e1.getY();
                        float diffX = e2.getX() - e1.getX();
                        if (Math.abs(diffX) > Math.abs(diffY)) {
                            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffX > 0) {
                                    onSwipeRight();
                                } else {
                                    onSwipeLeft();
                                }
                                result = true;
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    return result;
                }

                public void onSwipeRight() {
                    if (page.getNext() != null) {
                        ((mainActivity) requireActivity()).setViewPath(page.getNext());
                    }
                }

                public void onSwipeLeft() {
                    if (page.getPrev() != null) {
                        ((mainActivity) requireActivity()).setViewPath(page.getPrev());
                    }
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        submissionDownload.setOnClickListener(v -> {
            open.furaffinity.client.utilities.downloadContent.downloadSubmission(requireActivity(), requireContext(), page);
        });

        submissionFavorite.setOnClickListener(v -> new open.furaffinity.client.submitPages.submitGetRequest(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                fetchPageData();
                Toast.makeText(getActivity(), "Successfully updated favorites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to update favorites", Toast.LENGTH_SHORT).show();
            }
        }, page.getFavUnFav()).execute());

        sendNote.setOnClickListener(v -> sendPM(getActivity(), getChildFragmentManager(), page.getNote()));

        shareLink.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, open.furaffinity.client.utilities.webClient.getBaseUrl() + page.getPagePath());
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });

        imageInfoSwitch.setOnClickListener(v -> {
            if(submissionInfo.getVisibility() == View.GONE){
                submissionImage.setVisibility(View.GONE);
                submissionPDF.setVisibility(View.GONE);
                submissionMediaPlayerConstraintLayout.setVisibility(View.GONE);
                submissionInfo.setVisibility(View.VISIBLE);
            } else {
                submissionInfo.setVisibility(View.GONE);
                switch(page.getSubmissionMimeType()){
                    case "text/plain":
                    case "text/rtf":
                    case "application/pdf":
                        submissionPDF.setVisibility(View.VISIBLE);
                        break;
                    case "audio/mpeg":
                    case "audio/x-wav":
                    case "audio/midi":
                        submissionMediaPlayerConstraintLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        submissionImage.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        submissionMediaPlayerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                submissionMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        submissionMediaPlayerRewind.setOnClickListener(v -> {
            int reverseTime = 5000;
            int toTime = submissionMediaPlayer.getCurrentPosition() - reverseTime;

            if(toTime >= 0) {
                submissionMediaPlayer.seekTo(toTime);
            } else {
                submissionMediaPlayer.seekTo(0);
            }
        });

        submissionMediaPlayerPlayPause.setOnClickListener(v -> {
            if(submissionMediaPlayer.isPlaying()) {
                submissionMediaPlayerPlayPause.setText("Play");
                submissionMediaPlayer.pause();
            } else{
                submissionMediaPlayerPlayPause.setText("Pause");
                submissionMediaPlayer.start();
            }
        });

        submissionMediaPlayerRepeat.setOnClickListener(v -> {
            if(submissionMediaPlayer.isLooping()) {
                submissionMediaPlayerRepeat.setText("Once");
                submissionMediaPlayer.setLooping(false);
            } else {
                submissionMediaPlayerRepeat.setText("Repeat");
                submissionMediaPlayer.setLooping(true);
            }
        });

        submissionMediaPlayerFastForward.setOnClickListener(v -> {
            int forwardTime = 5000;
            int maxTime = submissionMediaPlayer.getDuration();
            int toTime = forwardTime + submissionMediaPlayer.getCurrentPosition();

            if(toTime <= maxTime) {
                submissionMediaPlayer.seekTo(toTime);
            } else {
                submissionMediaPlayer.seekTo(maxTime);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("viewPath", page.getPagePath());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("viewPath")) {
            page = new open.furaffinity.client.pages.view(getActivity(), pageListener, savedInstanceState.getString("viewPath"));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}