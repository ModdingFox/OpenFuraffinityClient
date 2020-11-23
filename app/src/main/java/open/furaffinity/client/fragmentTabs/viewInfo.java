package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.TextView;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.messageIds;

public class viewInfo extends open.furaffinity.client.abstractClasses.tabFragment {
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

    @Override
    protected int getLayout() {
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

    @Override
    protected void initPages() {
    }

    protected void fetchPageData() {
        if(getArguments() != null) {
            submissionCommentsString = getArguments().getString(messageIds.submissionComments_MESSAGE);
            submissionFavoritesString = getArguments().getString(messageIds.submissionFavorites_MESSAGE);
            submissionViewsString = getArguments().getString(messageIds.submissionViews_MESSAGE);
            submissionRatingString = getArguments().getString(messageIds.submissionRating_MESSAGE);
            submissionCategoryString = getArguments().getString(messageIds.submissionCategory_MESSAGE);
            submissionSpeciesString = getArguments().getString(messageIds.submissionSpecies_MESSAGE);
            submissionGenderString = getArguments().getString(messageIds.submissionGender_MESSAGE);
            submissionDateString = getArguments().getString(messageIds.submissionDate_MESSAGE);
            submissionSizeString = getArguments().getString(messageIds.submissionSize_MESSAGE);
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

    @Override
    protected void updateUIElements() {

    }

    @Override
    protected void updateUIElementListeners(View rootView) {

    }
}