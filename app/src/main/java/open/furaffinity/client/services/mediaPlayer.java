package open.furaffinity.client.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.media.app.NotificationCompat;

import open.furaffinity.client.R;

public class mediaPlayer extends Service {
    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSessionCompat;
    private NotificationCompat.MediaStyle notificationCompatMediaStyle;

    public class mediaPlayerBinder extends Binder{
        public void playURL(String url, String artist, String title){
            if(mediaPlayer != null) {
                mediaPlayer.stop();
            }

            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(url));
            mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, artist)
                .putString(MediaMetadata.METADATA_KEY_TITLE, title)
                .build()
            );

            androidx.core.app.NotificationCompat.Builder mBuilder = new androidx.core.app.NotificationCompat
                    .Builder(mediaPlayer.this, getString(R.string.musicPlayerService))
                    .setStyle(notificationCompatMediaStyle)
                    .setSmallIcon(R.drawable.ic_music);

            NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = getString(R.string.musicPlayerService);
                NotificationChannel channel = new NotificationChannel(channelId, getString(R.string.musicPlayerService) + "_Service_channel", NotificationManager.IMPORTANCE_LOW);
                mNotificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }

            mNotificationManager.notify(0, mBuilder.build());
        }

        public void seekTo(int msec){
            mediaPlayer.seekTo(msec);
        }

        public void rewind(){
            int reverseTime = 5000;
            int toTime = mediaPlayer.getCurrentPosition() - reverseTime;

            if(toTime >= 0) {
                mediaPlayer.seekTo(toTime);
            } else {
                mediaPlayer.seekTo(0);
            }
        }

        public void playPause(){
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else{
                mediaPlayer.start();
            }
        }

        public void repeat(){
            if(mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(false);
            } else {
                mediaPlayer.setLooping(true);
            }
        }

        public void fastForward(){
            int forwardTime = 5000;
            int maxTime = mediaPlayer.getDuration();
            int toTime = forwardTime + mediaPlayer.getCurrentPosition();

            if(toTime <= maxTime) {
                mediaPlayer.seekTo(toTime);
            } else {
                mediaPlayer.seekTo(maxTime);
            }
        }

        public int getCurrentPosition(){
            return mediaPlayer.getCurrentPosition();
        }

        public int getDuration(){
            return mediaPlayer.getDuration();
        }

        public boolean isPlaying(){
            return mediaPlayer.isPlaying();
        }

        public boolean isLooping(){
            return mediaPlayer.isLooping();
        }
    }

    public mediaPlayerBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();

        mediaSessionCompat = new MediaSessionCompat(this, getString(R.string.musicPlayerService));
        notificationCompatMediaStyle = new NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken());

        binder = new mediaPlayerBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
