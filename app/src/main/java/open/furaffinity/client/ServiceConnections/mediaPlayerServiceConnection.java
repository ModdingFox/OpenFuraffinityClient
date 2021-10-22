package open.furaffinity.client.ServiceConnections;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class mediaPlayerServiceConnection implements ServiceConnection {
    private open.furaffinity.client.services.mediaPlayer.mediaPlayerBinder binder = null;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (open.furaffinity.client.services.mediaPlayer.mediaPlayerBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public open.furaffinity.client.services.mediaPlayer.mediaPlayerBinder getBinder() {
        return binder;
    }
}
