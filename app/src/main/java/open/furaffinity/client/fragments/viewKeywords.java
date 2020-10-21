package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.stringListAdapter;
import open.furaffinity.client.utilities.messageIds;

public class viewKeywords extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter = new stringListAdapter(getArguments().getStringArrayList(messageIds.SubmissionTags_MESSAGE));
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }
}