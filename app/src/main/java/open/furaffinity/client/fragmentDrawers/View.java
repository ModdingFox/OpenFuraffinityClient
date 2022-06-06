package open.furaffinity.client.fragmentDrawers;

import static open.furaffinity.client.utilities.SendPm.sendPM;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.rtfparserkit.converter.text.StringTextConverter;
import com.rtfparserkit.parser.RtfStreamSource;
import open.furaffinity.client.R;
import open.furaffinity.client.ServiceConnections.MediaPlayerServiceConnection;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.adapter.ViewSectionsPagerAdapter;
import open.furaffinity.client.pages.LoginCheck;
import open.furaffinity.client.services.MediaPlayer;
import open.furaffinity.client.sqlite.HistoryContract.historyItemEntry;
import open.furaffinity.client.sqlite.HistoryDBHelper;
import open.furaffinity.client.submitPages.SubmitGetRequest;
import open.furaffinity.client.utilities.DownloadContent;
import open.furaffinity.client.utilities.FabCircular;
import open.furaffinity.client.utilities.WebClient;

public class View extends AbstractAppFragment {
    private TextView submissionTitle;
    private PhotoView submissionImage;
    private PDFView submissionPdf;
    private ConstraintLayout submissionInfo;
    private LinearLayout submissionUserLinearLayout;
    private ImageView submissionUserIcon;
    private TextView submissionUser;
    private ViewPager viewPager;
    private TabLayout tabs;

    private final MediaPlayerServiceConnection mediaPlayerServiceConnection =
        new MediaPlayerServiceConnection();
    private final Handler submissionMediaPlayerHandler = new Handler();

    private ConstraintLayout submissionMediaPlayerConstraintLayout;
    private ImageView submissionMediaPlayerSubmissionImage;
    private TextView submissionMediaPlayerCurrentTime;
    private TextView submissionMediaPlayerRemainingTime;
    private SeekBar submissionMediaPlayerSeekBar;
    private Button submissionMediaPlayerRewind;
    private Button submissionMediaPlayerPlayPause;
    private Button submissionMediaPlayerRepeat;
    private Button submissionMediaPlayerFastForward;

    private final Runnable UpdateSongTime = new Runnable() {
        public void run() {
            final int currentTime = mediaPlayerServiceConnection.getBinder().getCurrentPosition();
            final int remainingTime =
                mediaPlayerServiceConnection.getBinder().getDuration() - currentTime;

            submissionMediaPlayerCurrentTime.setText(
                String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) currentTime) -
                        TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes((long) currentTime))));
            submissionMediaPlayerRemainingTime.setText(
                String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) remainingTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) remainingTime) -
                        TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes((long) remainingTime))));
            submissionMediaPlayerSeekBar.setProgress(currentTime);
            submissionMediaPlayerHandler.postDelayed(this, 100);
        }
    };

    private FabCircular fab;
    private FloatingActionButton submissionFavorite;
    private FloatingActionButton submissionDownload;
    private FloatingActionButton sendNote;
    private FloatingActionButton shareLink;
    private FloatingActionButton imageInfoSwitch;

    private LoginCheck loginCheck;
    private open.furaffinity.client.pages.View page;
    private boolean isLoading;
    private final AbstractPage.PageListener pageListener = new AbstractPage.PageListener() {
        @Override public void requestSucceeded(AbstractPage abstractPage) {
            submissionTitle.setText(
                ((open.furaffinity.client.pages.View) abstractPage).getSubmissionTitle());

            switch (((open.furaffinity.client.pages.View) abstractPage).getSubmissionMimeType()) {
                case "text/plain":
                    new AsyncTask<Void, Void, InputStream>() {
                        @Override protected InputStream doInBackground(Void... voids) {
                            try {
                                InputStream inputStream = new URL(
                                    ((open.furaffinity.client.pages.View) abstractPage).getDownload()).openStream();
                                InputStreamReader inputStreamReader =
                                    new InputStreamReader(inputStream);
                                BufferedReader bufferedReader =
                                    new BufferedReader(inputStreamReader);

                                Document document = new Document();
                                ByteArrayOutputStream byteArrayOutputStream =
                                    new ByteArrayOutputStream();
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

                                return new ByteArrayInputStream(
                                    byteArrayOutputStream.toByteArray());
                            } catch (IOException | DocumentException e) {
                                return null;
                            }
                        }

                        @Override protected void onPostExecute(InputStream inputStream) {
                            if (inputStream != null) {
                                submissionPdf.fromStream(inputStream).fitEachPage(true).load();
                                submissionImage.setVisibility(android.view.View.GONE);
                                submissionPdf.setVisibility(android.view.View.VISIBLE);
                            }
                            else {
                                Glide.with(View.this).load(
                                        ((open.furaffinity.client.pages.View) abstractPage).getSubmissionImgLink())
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .placeholder(R.drawable.loading).into(submissionImage);
                                Toast.makeText(getActivity(), "Failed to load pdf",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                    break;
                case "text/rtf":
                    new AsyncTask<Void, Void, InputStream>() {
                        @Override protected InputStream doInBackground(Void... voids) {
                            try {
                                InputStream inputStream = new URL(
                                    ((open.furaffinity.client.pages.View) abstractPage).getDownload()).openStream();
                                StringTextConverter stringTextConverter = new StringTextConverter();
                                stringTextConverter.convert(new RtfStreamSource(inputStream));
                                String extractedText = stringTextConverter.getText();

                                InputStream extractedTextInputStream = new ByteArrayInputStream(
                                    extractedText.getBytes(StandardCharsets.UTF_8));
                                InputStreamReader inputStreamReader =
                                    new InputStreamReader(extractedTextInputStream);
                                BufferedReader bufferedReader =
                                    new BufferedReader(inputStreamReader);

                                Document document = new Document();
                                ByteArrayOutputStream byteArrayOutputStream =
                                    new ByteArrayOutputStream();
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

                                return new ByteArrayInputStream(
                                    byteArrayOutputStream.toByteArray());
                            } catch (IOException | DocumentException e) {
                                return null;
                            }
                        }

                        @Override protected void onPostExecute(InputStream inputStream) {
                            if (inputStream != null) {
                                submissionPdf.fromStream(inputStream).load();
                                submissionImage.setVisibility(android.view.View.GONE);
                                submissionPdf.setVisibility(android.view.View.VISIBLE);
                            }
                            else {
                                Glide.with(View.this).load(
                                        ((open.furaffinity.client.pages.View) abstractPage).getSubmissionImgLink())
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .placeholder(R.drawable.loading).into(submissionImage);
                                Toast.makeText(getActivity(), "Failed to load pdf",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                    break;
                case "application/msword":
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                case "application/vnd.oasis.opendocument.text":
                    Glide.with(View.this).load(
                            ((open.furaffinity.client.pages.View) abstractPage).getSubmissionImgLink())
                        .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                        .into(submissionImage);
                    break;
                case "application/pdf":
                    new AsyncTask<Void, Void, InputStream>() {
                        @Override protected InputStream doInBackground(Void... voids) {
                            try {
                                return new URL(
                                    ((open.furaffinity.client.pages.View) abstractPage).getDownload()).openStream();
                            } catch (IOException e) {
                                return null;
                            }
                        }

                        @Override protected void onPostExecute(InputStream inputStream) {
                            if (inputStream != null) {
                                submissionPdf.fromStream(inputStream).fitEachPage(true).load();
                                submissionImage.setVisibility(android.view.View.GONE);
                                submissionPdf.setVisibility(android.view.View.VISIBLE);
                            }
                            else {
                                Glide.with(View.this).load(
                                        ((open.furaffinity.client.pages.View) abstractPage).getSubmissionImgLink())
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .placeholder(R.drawable.loading).into(submissionImage);
                                Toast.makeText(getActivity(), "Failed to load pdf",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                    break;
                case "audio/mpeg":
                case "audio/x-wav":
                case "audio/midi":
                    Glide.with(View.this).load(
                            ((open.furaffinity.client.pages.View) abstractPage).getSubmissionImgLink())
                        .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                        .into(submissionMediaPlayerSubmissionImage);

                    mediaPlayerServiceConnection.getBinder()
                        .playURL(((open.furaffinity.client.pages.View) abstractPage).getDownload(),
                            ((open.furaffinity.client.pages.View) abstractPage).getSubmissionUser(),
                            ((open.furaffinity.client.pages.View) abstractPage).getSubmissionTitle());

                    submissionMediaPlayerSeekBar.setMax(
                        mediaPlayerServiceConnection.getBinder().getDuration());
                    submissionMediaPlayerHandler.postDelayed(UpdateSongTime, 100);

                    submissionImage.setVisibility(android.view.View.GONE);
                    submissionMediaPlayerConstraintLayout.setVisibility(android.view.View.VISIBLE);
                    break;
                default:
                    Glide.with(View.this).load(
                            ((open.furaffinity.client.pages.View) abstractPage).getSubmissionImgLink())
                        .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                        .into(submissionImage);
                    break;
            }

            Glide.with(View.this)
                .load(((open.furaffinity.client.pages.View) abstractPage).getSubmissionUserIcon())
                .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                .into(submissionUserIcon);
            submissionUser.setText(
                ((open.furaffinity.client.pages.View) abstractPage).getSubmissionUser());

            if (((open.furaffinity.client.pages.View) abstractPage).getIsFav()) {
                submissionFavorite.setImageResource(R.drawable.ic_menu_favorite);
            }
            else {
                submissionFavorite.setImageResource(R.drawable.ic_menu_unfavorite);
            }

            saveHistory();
            setupViewPager((open.furaffinity.client.pages.View) abstractPage);

            fab.setVisibility(android.view.View.VISIBLE);
            isLoading = false;
        }

        @Override public void requestFailed(AbstractPage abstractPage) {
            fab.setVisibility(android.view.View.GONE);
            isLoading = false;
            Toast.makeText(getActivity(), "Failed to load data for view", Toast.LENGTH_SHORT)
                .show();
        }
    };

    private void saveHistory() {
        final SharedPreferences sharedPref =
            requireActivity().getSharedPreferences(getString(R.string.settingsFile),
                Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(requireActivity().getString(R.string.trackHistorySetting),
            Settings.trackHistoryDefault)) {
            final HistoryDBHelper dbHelper = new HistoryDBHelper(requireActivity());
            final SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

            // Delete previous versions from history
            final String selection = historyItemEntry.COLUMN_NAME_URL + " LIKE ?";
            final String[] selectionArgs = {page.getPagePath()};
            sqliteDatabase.delete(historyItemEntry.TABLE_NAME_VIEW, selection, selectionArgs);

            // Insert into history
            final ContentValues values = new ContentValues();
            values.put(historyItemEntry.COLUMN_NAME_USER, page.getSubmissionUser());
            values.put(historyItemEntry.COLUMN_NAME_TITLE, page.getSubmissionTitle());
            values.put(historyItemEntry.COLUMN_NAME_URL, page.getPagePath());
            values.put(historyItemEntry.COLUMN_NAME_DATETIME, (new Date()).getTime());
            sqliteDatabase.insert(historyItemEntry.TABLE_NAME_VIEW, null, values);

            // Limit history to 512 entries
            sqliteDatabase.execSQL("DELETE FROM " + historyItemEntry.TABLE_NAME_VIEW
                + " WHERE rowid < (SELECT min(rowid) FROM (SELECT rowid FROM viewHistory ORDER BY "
                + "rowid DESC LIMIT 512))");

            sqliteDatabase.close();
        }

        ((MainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(),
            page.getPagePath());
    }

    @Override protected int getLayout() {
        return R.layout.fragment_view;
    }

    protected void getElements(android.view.View rootView) {
        final ConstraintLayout constraintLayout = rootView.findViewById(R.id.constraintLayout);

        submissionTitle = rootView.findViewById(R.id.submissionTitle);
        submissionImage = rootView.findViewById(R.id.submissionImage);
        submissionPdf = rootView.findViewById(R.id.submissionPDF);
        submissionInfo = rootView.findViewById(R.id.submissionInfo);
        submissionUserLinearLayout = rootView.findViewById(R.id.submissionUserLinearLayout);
        submissionUserIcon = rootView.findViewById(R.id.submissionUserIcon);
        submissionUser = rootView.findViewById(R.id.submissionUser);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
        fab = rootView.findViewById(R.id.fab);

        submissionMediaPlayerConstraintLayout =
            rootView.findViewById(R.id.submissionMediaPlayerConstraintLayout);
        submissionMediaPlayerSubmissionImage =
            rootView.findViewById(R.id.submissionMediaPlayerSubmissionImage);
        submissionMediaPlayerCurrentTime =
            rootView.findViewById(R.id.submissionMediaPlayerCurrentTime);
        submissionMediaPlayerRemainingTime =
            rootView.findViewById(R.id.submissionMediaPlayerRemainingTime);
        submissionMediaPlayerSeekBar = rootView.findViewById(R.id.submissionMediaPlayerSeekBar);
        submissionMediaPlayerRewind = rootView.findViewById(R.id.submissionMediaPlayerRewind);
        submissionMediaPlayerPlayPause = rootView.findViewById(R.id.submissionMediaPlayerPlayPause);
        submissionMediaPlayerRepeat = rootView.findViewById(R.id.submissionMediaPlayerRepeat);
        submissionMediaPlayerFastForward =
            rootView.findViewById(R.id.submissionMediaPlayerFastForward);

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

        submissionFavorite.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        submissionDownload.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        sendNote.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        shareLink.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        imageInfoSwitch.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(submissionFavorite);
        constraintLayout.addView(submissionDownload);
        constraintLayout.addView(sendNote);
        constraintLayout.addView(shareLink);
        constraintLayout.addView(imageInfoSwitch);

        submissionFavorite.setVisibility(android.view.View.GONE);
        sendNote.setVisibility(android.view.View.GONE);

        fab.addButton(submissionDownload, 1.5f, 270);
        fab.addButton(shareLink, 2.6f, 270);
        fab.addButton(imageInfoSwitch, 3.7f, 270);
        fab.setVisibility(android.view.View.GONE);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;

            loginCheck = new LoginCheck(loginCheck);
            loginCheck.execute();

            page = new open.furaffinity.client.pages.View(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void setupViewPager(open.furaffinity.client.pages.View page) {
        final ViewSectionsPagerAdapter sectionsPagerAdapter =
            new ViewSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    protected void initPages() {
        final String pagePath = ((MainActivity) requireActivity()).getViewPath();

        loginCheck = new LoginCheck(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                fab.removeButton(submissionFavorite);
                fab.removeButton(sendNote);

                submissionFavorite.setVisibility(android.view.View.GONE);
                sendNote.setVisibility(android.view.View.GONE);

                if (((LoginCheck) abstractPage).getIsLoggedIn()) {
                    fab.addButton(submissionFavorite, 1.5f, 180);
                    fab.addButton(sendNote, 1.5f, 225);
                }
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                fab.removeButton(submissionFavorite);
                fab.removeButton(sendNote);

                submissionFavorite.setVisibility(android.view.View.GONE);
                sendNote.setVisibility(android.view.View.GONE);

                Toast.makeText(getActivity(), "Failed to load data for loginCheck",
                    Toast.LENGTH_SHORT).show();
            }
        });

        if (page == null) {
            page = new open.furaffinity.client.pages.View(getActivity(), pageListener, pagePath);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void updateUiElementListeners(android.view.View rootView) {
        submissionUserLinearLayout.setOnClickListener(
            view -> {
                ((MainActivity) requireActivity()).setUserPath(page.getSubmissionUserPage());
            });

        submissionImage.setOnSingleFlingListener(new OnSingleFlingListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                final int SWIPE_THRESHOLD = 100;
                final int SWIPE_VELOCITY_THRESHOLD = 100;
                boolean result = false;
                try {
                    final float diffY = e2.getY() - e1.getY();
                    final float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD
                            && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            }
                            else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }

            public void onSwipeRight() {
                if (page.getNext() != null) {
                    ((MainActivity) requireActivity()).setViewPath(page.getNext());
                }
            }

            public void onSwipeLeft() {
                if (page.getPrev() != null) {
                    ((MainActivity) requireActivity()).setViewPath(page.getPrev());
                }
            }
        });

        submissionMediaPlayerSubmissionImage.setOnTouchListener(new android.view.View.OnTouchListener() {
            final GestureDetector gestureDetector =
                new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        int SWIPE_THRESHOLD = 100;
                        int SWIPE_VELOCITY_THRESHOLD = 100;
                        boolean result = false;
                        try {
                            float diffY = e2.getY() - e1.getY();
                            float diffX = e2.getX() - e1.getX();
                            if (Math.abs(diffX) > Math.abs(diffY)) {
                                if (Math.abs(diffX) > SWIPE_THRESHOLD &&
                                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                    if (diffX > 0) {
                                        onSwipeRight();
                                    }
                                    else {
                                        onSwipeLeft();
                                    }
                                    result = true;
                                }
                            }
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        return result;
                    }

                    public void onSwipeRight() {
                        if (page.getNext() != null) {
                            ((MainActivity) requireActivity()).setViewPath(page.getNext());
                        }
                    }

                    public void onSwipeLeft() {
                        if (page.getPrev() != null) {
                            ((MainActivity) requireActivity()).setViewPath(page.getPrev());
                        }
                    }
                });

            @Override public boolean onTouch(android.view.View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        submissionDownload.setOnClickListener(view -> {
            DownloadContent.downloadSubmission(requireActivity(),
                requireContext(), page);
        });

        submissionFavorite.setOnClickListener(
            view -> {
                new SubmitGetRequest(getActivity(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            fetchPageData();
                            Toast.makeText(getActivity(), "Successfully updated favorites",
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to update favorites",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, page.getFavUnFav()).execute();
            });

        sendNote.setOnClickListener(
            view -> {
                sendPM(getActivity(), getChildFragmentManager(), page.getNote());
            });

        shareLink.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,
                WebClient.getBaseUrl() + page.getPagePath());
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });

        imageInfoSwitch.setOnClickListener(view -> {
            if (submissionInfo.getVisibility() == android.view.View.GONE) {
                submissionImage.setVisibility(android.view.View.GONE);
                submissionPdf.setVisibility(android.view.View.GONE);
                submissionMediaPlayerConstraintLayout.setVisibility(android.view.View.GONE);
                submissionInfo.setVisibility(android.view.View.VISIBLE);
            }
            else {
                submissionInfo.setVisibility(android.view.View.GONE);
                switch (page.getSubmissionMimeType()) {
                    case "text/plain":
                    case "text/rtf":
                    case "application/pdf":
                        submissionPdf.setVisibility(android.view.View.VISIBLE);
                        break;
                    case "audio/mpeg":
                    case "audio/x-wav":
                    case "audio/midi":
                        submissionMediaPlayerConstraintLayout.setVisibility(android.view.View.VISIBLE);
                        break;
                    default:
                        submissionImage.setVisibility(android.view.View.VISIBLE);
                        break;
                }
            }
        });

        submissionMediaPlayerSeekBar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayerServiceConnection.getBinder().seekTo(seekBar.getProgress());
                }
            });

        submissionMediaPlayerRewind.setOnClickListener(view -> {
            mediaPlayerServiceConnection.getBinder().rewind();
        });

        submissionMediaPlayerPlayPause.setOnClickListener(view -> {
            mediaPlayerServiceConnection.getBinder().playPause();

            if (mediaPlayerServiceConnection.getBinder().isPlaying()) {
                submissionMediaPlayerPlayPause.setText("Pause");
            }
            else {
                submissionMediaPlayerPlayPause.setText("Play");
            }
        });

        submissionMediaPlayerRepeat.setOnClickListener(view -> {
            mediaPlayerServiceConnection.getBinder().repeat();

            if (mediaPlayerServiceConnection.getBinder().isLooping()) {
                submissionMediaPlayerRepeat.setText("Repeat");
            }
            else {
                submissionMediaPlayerRepeat.setText("Once");
            }
        });

        submissionMediaPlayerFastForward.setOnClickListener(view -> {
            mediaPlayerServiceConnection.getBinder().fastForward();
        });
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("viewPath", page.getPagePath());
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent =
            new Intent(getContext(), MediaPlayer.class);
        getActivity().bindService(intent, mediaPlayerServiceConnection, Context.BIND_AUTO_CREATE);

        if (savedInstanceState != null && savedInstanceState.containsKey("viewPath")) {
            page = new open.furaffinity.client.pages.View(getActivity(), pageListener,
                savedInstanceState.getString("viewPath"));
        }
    }

    @Override public void onStop() {
        super.onStop();
    }
}