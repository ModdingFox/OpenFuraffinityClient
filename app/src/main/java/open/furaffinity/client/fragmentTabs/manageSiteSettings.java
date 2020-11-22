package open.furaffinity.client.fragmentTabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsSiteSettings;
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
    private controlsSiteSettings page;

    private boolean isLoading = false;

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
        fab.setVisibility(View.GONE);
    }

    private void fetchPageData() {
        if(!isLoading) {
            isLoading = true;
            page = new controlsSiteSettings(page);
            page.execute();
        }
    }

    private void initPages() {
        webClient = new webClient(requireContext());

        page = new controlsSiteSettings(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (page.getDisableAvatars()) {
                    disable_avatars_yes.setChecked(true);
                } else {
                    disable_avatars_no.setChecked(true);
                }

                switch (page.getDateFormat()) {
                    case "full":
                        switch_date_format_full.setChecked(true);
                        break;
                    case "fuzzy":
                        switch_date_format_fuzzy.setChecked(true);
                        break;
                }

                uiControls.spinnerSetAdapter(requireContext(), select_preferred_perpage, page.getPerPage(), page.getPerPageCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), select_newsubmissions_direction, page.getNewSubmissionsDirection(), page.getNewSubmissionsDirectionCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), select_thumbnail_size, page.getThumbnailSize(), page.getThumbnailSizeCurrent(), true, false);

                switch (page.getGalleryNavigation()) {
                    case "minigallery":
                        gallery_navigation_minigallery.setChecked(true);
                        break;
                    case "links":
                        gallery_navigation_links.setChecked(true);
                        break;
                }

                uiControls.spinnerSetAdapter(requireContext(), hide_favorites, page.getHideFavorites(), page.getHideFavoritesCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), no_guests, page.getNoGuests(), page.getNoGuestsCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), no_search_engines, page.getNoSearchEngines(), page.getNoSearchEnginesCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), no_notes, page.getNoNotes(), page.getNoNotesCurrent(), true, false);

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data site settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("do", "update");

                if (disable_avatars_yes.isChecked()) {
                    params.put("disable_avatars", "1");
                }

                if (disable_avatars_no.isChecked()) {
                    params.put("disable_avatars", "0");
                }

                if (switch_date_format_full.isChecked()) {
                    params.put("date_format", "full");
                }

                if (switch_date_format_fuzzy.isChecked()) {
                    params.put("date_format", "fuzzy");
                }

                params.put("perpage", ((kvPair) select_preferred_perpage.getSelectedItem()).getKey());
                params.put("newsubmissions_direction", ((kvPair) select_newsubmissions_direction.getSelectedItem()).getKey());
                params.put("thumbnail_size", ((kvPair) select_thumbnail_size.getSelectedItem()).getKey());

                if (gallery_navigation_minigallery.isChecked()) {
                    params.put("gallery_navigation", "minigallery");
                }

                if (gallery_navigation_links.isChecked()) {
                    params.put("gallery_navigation", "links");
                }

                params.put("hide_favorites", ((kvPair) hide_favorites.getSelectedItem()).getKey());
                params.put("no_guests", ((kvPair) no_guests.getSelectedItem()).getKey());
                params.put("no_search_engines", ((kvPair) no_search_engines.getSelectedItem()).getKey());
                params.put("no_notes", ((kvPair) no_notes.getSelectedItem()).getKey());
                params.put("save_settings", "Save Settings");

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsSiteSettings.getPagePath(), params);
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
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
