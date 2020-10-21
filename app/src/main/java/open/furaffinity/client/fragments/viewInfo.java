package open.furaffinity.client.fragments;

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_info, container, false);

        TextView submissionComments = rootView.findViewById(R.id.submissionComments);
        TextView submissionFavorites = rootView.findViewById(R.id.submissionFavorites);
        TextView submissionViews = rootView.findViewById(R.id.submissionViews);
        TextView submissionRating = rootView.findViewById(R.id.submissionRating);
        TextView submissionCategory = rootView.findViewById(R.id.submissionCategory);
        TextView submissionSpecies = rootView.findViewById(R.id.submissionSpecies);
        TextView submissionGender = rootView.findViewById(R.id.submissionGender);
        TextView submissionDate = rootView.findViewById(R.id.submissionDate);
        TextView submissionSize = rootView.findViewById(R.id.submissionSize);

        submissionComments.setText(getArguments().getString(messageIds.submissionComments_MESSAGE));
        submissionFavorites.setText(getArguments().getString(messageIds.submissionFavorites_MESSAGE));
        submissionViews.setText(getArguments().getString(messageIds.submissionViews_MESSAGE));
        submissionRating.setText(getArguments().getString(messageIds.submissionRating_MESSAGE));
        submissionCategory.setText(getArguments().getString(messageIds.submissionCategory_MESSAGE));
        submissionSpecies.setText(getArguments().getString(messageIds.submissionSpecies_MESSAGE));
        submissionGender.setText(getArguments().getString(messageIds.submissionGender_MESSAGE));
        submissionDate.setText(getArguments().getString(messageIds.submissionDate_MESSAGE));
        submissionSize.setText(getArguments().getString(messageIds.submissionSize_MESSAGE));

        return rootView;
    }
}