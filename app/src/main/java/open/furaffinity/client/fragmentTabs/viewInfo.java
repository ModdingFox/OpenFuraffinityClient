package open.furaffinity.client.fragmentTabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.messageIds;

public class viewInfo extends Fragment {
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

    private void getElements(View rootView) {
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

    private void fetchPageData() {
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

    private void updateUIElements() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_info, container, false);
        getElements(rootView);
        fetchPageData();
        updateUIElements();
        return rootView;
    }
}