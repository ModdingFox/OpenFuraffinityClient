package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import open.furaffinity.client.R;

public class profile extends Fragment {

    private FloatingActionButton fab;

    private void getElements(View rootView) {
        fab = rootView.findViewById(R.id.fabProfile);
    }

    private void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(view ->
        {
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        getElements(rootView);
        updateUIElementListeners(rootView);
        return rootView;
    }
}