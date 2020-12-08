package open.furaffinity.client.abstractClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class appFragment extends Fragment {
    protected static final String TAG = appFragment.class.getName();

    protected abstract int getLayout();

    protected abstract void getElements(View rootView);

    protected abstract void initPages();

    protected abstract void fetchPageData();

    protected abstract void updateUIElements();

    protected abstract void updateUIElementListeners(View rootView);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayout(), container, false);
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
