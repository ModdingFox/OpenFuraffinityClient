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
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.fragmentDrawers.Settings;

public abstract class AbstractAppFragment extends Fragment {
    protected static final String TAG = AbstractAppFragment.class.getName();

    protected abstract int getLayout();

    protected abstract void getElements(View rootView);

    protected abstract void initPages();

    protected abstract void fetchPageData();

    protected abstract void updateUiElements();

    protected abstract void updateUiElementListeners(View rootView);

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = requireActivity();
        final SharedPreferences sharedPref =
            context.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(this.getString(R.string.trackBackHistorySetting),
            Settings.trackBackHistoryDefault)) {
            final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override public void handleOnBackPressed() {
                    ((MainActivity) requireActivity()).drawerFragmentPop();
                }
            };
            this.requireActivity().getOnBackPressedDispatcher()
                .addCallback(this.requireActivity(), callback);
        }

        final View rootView = inflater.inflate(getLayout(), container, false);
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUiElements();
        updateUiElementListeners(rootView);
        return rootView;
    }
}
