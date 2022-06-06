package open.furaffinity.client.abstractClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.fragmentDrawers.settings;

public abstract class appFragment extends Fragment {
    protected static final String TAG = appFragment.class.getName();

    protected abstract int getLayout();

    protected abstract void getElements(View rootView);

    protected abstract void initPages();

    protected abstract void fetchPageData();

    protected abstract void updateUIElements();

    protected abstract void updateUIElementListeners(View rootView);

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = requireActivity();
        SharedPreferences sharedPref =
            context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(this.getString(R.string.trackBackHistorySetting),
            settings.trackBackHistoryDefault)) {
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override public void handleOnBackPressed() {
                    ((mainActivity) requireActivity()).drawerFragmentPop();
                }
            };
            this.requireActivity().getOnBackPressedDispatcher()
                .addCallback(this.requireActivity(), callback);
        }

        View rootView = inflater.inflate(getLayout(), container, false);
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
