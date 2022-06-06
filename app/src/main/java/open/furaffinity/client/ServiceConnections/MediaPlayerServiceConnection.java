package open.furaffinity.client.ServiceConnections;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import open.furaffinity.client.services.MediaPlayer;

public class MediaPlayerServiceConnection implements ServiceConnection {
    private MediaPlayer.mediaPlayerBinder binder;

    @Override public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (MediaPlayer.mediaPlayerBinder) service;
    }

    @Override public void onServiceDisconnected(ComponentName name) {

    }

    public MediaPlayer.mediaPlayerBinder getBinder() {
        return binder;
    }
}
