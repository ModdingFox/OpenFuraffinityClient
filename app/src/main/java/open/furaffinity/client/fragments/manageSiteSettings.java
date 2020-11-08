package open.furaffinity.client.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class manageSiteSettings extends Fragment {
    private static String TAG = manageSiteSettings.class.getName();

    private RadioButton disable_avatars_yes;
    private RadioButton disable_avatars_no;
    private RadioButton switch_date_format_full;
    private RadioButton switch_date_format_fuzzy;
    private Spinner select_preferred_perpage;
    private Spinner select_newsubmissions_direction;
    private Spinner select_thumbnail_size;
    private RadioButton gallery_navigation_minigallery;
    private RadioButton gallery_navigation_links;
    private Spinner hide_favorites;
    private Spinner no_guests;
    private Spinner no_search_engines;
    private Spinner no_notes;

    private fabCircular fab;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.controlsSiteSettings page;

    private void getElements(View rootView) {
        disable_avatars_yes = rootView.findViewById(R.id.disable_avatars_yes);
        disable_avatars_no = rootView.findViewById(R.id.disable_avatars_no);
        switch_date_format_full = rootView.findViewById(R.id.switch_date_format_full);
        switch_date_format_fuzzy = rootView.findViewById(R.id.switch_date_format_fuzzy);
        select_preferred_perpage = rootView.findViewById(R.id.select_preferred_perpage);
        select_newsubmissions_direction = rootView.findViewById(R.id.select_newsubmissions_direction);
        select_thumbnail_size = rootView.findViewById(R.id.select_thumbnail_size);
        gallery_navigation_minigallery = rootView.findViewById(R.id.gallery_navigation_minigallery);
        gallery_navigation_links = rootView.findViewById(R.id.gallery_navigation_links);
        hide_favorites = rootView.findViewById(R.id.hide_favorites);
        no_guests = rootView.findViewById(R.id.no_guests);
        no_search_engines = rootView.findViewById(R.id.no_search_engines);
        no_notes = rootView.findViewById(R.id.no_notes);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_save);
    }

    private void initClientAndPage() {
        webClient = new webClient(requireContext());
        page = new open.furaffinity.client.pages.controlsSiteSettings();
    }

    private void fetchPageData() {
        page = new open.furaffinity.client.pages.controlsSiteSettings();
        try {
            page.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "loadPage: ", e);
        }
    }

    private void updateUIElements() {
    }

    private void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + open.furaffinity.client.pages.controlsSiteSettings.getPagePath(), params);
                            return null;
                        }
                    }.execute(webClient).get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not update site settings: ", e);
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_site_settings, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
