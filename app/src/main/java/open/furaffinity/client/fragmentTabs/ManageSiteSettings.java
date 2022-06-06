package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.pages.ControlsSiteSettings;
import open.furaffinity.client.submitPages.SubmitControlsSiteSettings;
import open.furaffinity.client.utilities.FabCircular;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class ManageSiteSettings extends AbstractAppFragment {
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

    private FabCircular fab;

    private ControlsSiteSettings page;

    private boolean isLoading = false;

    @Override protected int getLayout() {
        return R.layout.fragment_manage_site_settings;
    }

    protected void getElements(View rootView) {
        disable_avatars_yes = rootView.findViewById(R.id.disable_avatars_yes);
        disable_avatars_no = rootView.findViewById(R.id.disable_avatars_no);
        switch_date_format_full = rootView.findViewById(R.id.switch_date_format_full);
        switch_date_format_fuzzy = rootView.findViewById(R.id.switch_date_format_fuzzy);
        select_preferred_perpage = rootView.findViewById(R.id.select_preferred_perpage);
        select_newsubmissions_direction =
            rootView.findViewById(R.id.select_newsubmissions_direction);
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

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            page = new ControlsSiteSettings(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    protected void initPages() {
        page = new ControlsSiteSettings(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                if (page.getDisableAvatars()) {
                    disable_avatars_yes.setChecked(true);
                }
                else {
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

                UiControls.spinnerSetAdapter(requireContext(), select_preferred_perpage,
                    page.getPerPage(), page.getPerPageCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), select_newsubmissions_direction,
                    page.getNewSubmissionsDirection(), page.getNewSubmissionsDirectionCurrent(),
                    true, false);
                UiControls.spinnerSetAdapter(requireContext(), select_thumbnail_size,
                    page.getThumbnailSize(), page.getThumbnailSizeCurrent(), true, false);

                switch (page.getGalleryNavigation()) {
                    case "minigallery":
                        gallery_navigation_minigallery.setChecked(true);
                        break;
                    case "links":
                        gallery_navigation_links.setChecked(true);
                        break;
                }

                UiControls.spinnerSetAdapter(requireContext(), hide_favorites,
                    page.getHideFavorites(), page.getHideFavoritesCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), no_guests, page.getNoGuests(),
                    page.getNoGuestsCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), no_search_engines,
                    page.getNoSearchEngines(), page.getNoSearchEnginesCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), no_notes, page.getNoNotes(),
                    page.getNoNotesCurrent(), true, false);

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data site settings",
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUiElementListeners(View rootView) {
        fab.setOnClickListener(
            v -> new SubmitControlsSiteSettings(getActivity(),
                new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Successfully updated site settings",
                            Toast.LENGTH_SHORT).show();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to updated site settings",
                            Toast.LENGTH_SHORT).show();
                    }
                }, disable_avatars_yes.isChecked(), disable_avatars_no.isChecked(),
                switch_date_format_full.isChecked(), switch_date_format_fuzzy.isChecked(),
                ((KvPair) select_preferred_perpage.getSelectedItem()).getKey(),
                ((KvPair) select_newsubmissions_direction.getSelectedItem()).getKey(),
                ((KvPair) select_thumbnail_size.getSelectedItem()).getKey(),
                gallery_navigation_minigallery.isChecked(), gallery_navigation_links.isChecked(),
                ((KvPair) hide_favorites.getSelectedItem()).getKey(),
                ((KvPair) no_guests.getSelectedItem()).getKey(),
                ((KvPair) no_search_engines.getSelectedItem()).getKey(),
                ((KvPair) no_notes.getSelectedItem()).getKey()).execute());
    }
}
