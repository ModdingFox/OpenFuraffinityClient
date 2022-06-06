package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.TextView;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.utilities.MessageIds;

public class ViewInfo extends AbstractAppFragment {
    private TextView submissionComments;
    private TextView submissionFavorites;
    private TextView submissionViews;
    private TextView submissionRating;
    private TextView submissionCategory;
    private TextView submissionSpecies;
    private TextView submissionGender;
    private TextView submissionDate;
    private TextView submissionSize;

    private String submissionCommentsString;
    private String submissionFavoritesString;
    private String submissionViewsString;
    private String submissionRatingString;
    private String submissionCategoryString;
    private String submissionSpeciesString;
    private String submissionGenderString;
    private String submissionDateString;
    private String submissionSizeString;

    @Override protected int getLayout() {
        return R.layout.fragment_view_info;
    }

    protected void getElements(View rootView) {
        submissionComments = rootView.findViewById(R.id.submissionComments);
        submissionFavorites = rootView.findViewById(R.id.submissionFavorites);
        submissionViews = rootView.findViewById(R.id.submissionViews);
        submissionRating = rootView.findViewById(R.id.submissionRating);
        submissionCategory = rootView.findViewById(R.id.submissionCategory);
        submissionSpecies = rootView.findViewById(R.id.submissionSpecies);
        submissionGender = rootView.findViewById(R.id.submissionGender);
        submissionDate = rootView.findViewById(R.id.submissionDate);
        submissionSize = rootView.findViewById(R.id.submissionSize);
    }

    @Override protected void initPages() {
    }

    protected void fetchPageData() {
        if (getArguments() != null) {
            submissionCommentsString =
                getArguments().getString(MessageIds.submissionComments_MESSAGE);
            submissionFavoritesString =
                getArguments().getString(MessageIds.submissionFavorites_MESSAGE);
            submissionViewsString = getArguments().getString(MessageIds.submissionViews_MESSAGE);
            submissionRatingString = getArguments().getString(MessageIds.submissionRating_MESSAGE);
            submissionCategoryString =
                getArguments().getString(MessageIds.submissionCategory_MESSAGE);
            submissionSpeciesString =
                getArguments().getString(MessageIds.submissionSpecies_MESSAGE);
            submissionGenderString = getArguments().getString(MessageIds.submissionGender_MESSAGE);
            submissionDateString = getArguments().getString(MessageIds.submissionDate_MESSAGE);
            submissionSizeString = getArguments().getString(MessageIds.submissionSize_MESSAGE);
        }

        submissionComments.setText(submissionCommentsString);
        submissionFavorites.setText(submissionFavoritesString);
        submissionViews.setText(submissionViewsString);
        submissionRating.setText(submissionRatingString);
        submissionCategory.setText(submissionCategoryString);
        submissionSpecies.setText(submissionSpeciesString);
        submissionGender.setText(submissionGenderString);
        submissionDate.setText(submissionDateString);
        submissionSize.setText(submissionSizeString);
    }

    @Override protected void updateUiElements() {

    }

    @Override protected void updateUiElementListeners(View rootView) {

    }
}