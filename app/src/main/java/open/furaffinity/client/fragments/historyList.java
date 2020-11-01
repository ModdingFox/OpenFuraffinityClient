package open.furaffinity.client.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.historyListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.sqlite.historyContract.historyItemEntry;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.utilities.messageIds;

public class historyList extends Fragment {
    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private int currentView = 0;

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void fetchPageData() {
        historyDBHelper dbHelper = new historyDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                historyItemEntry.COLUMN_NAME_USER,
                historyItemEntry.COLUMN_NAME_TITLE,
                historyItemEntry.COLUMN_NAME_URL,
                historyItemEntry.COLUMN_NAME_DATETIME
        };

        String sortOrder = "rowid DESC";

        String tableName = "";
        String routableClass = "";

        switch (currentView) {
            case 0:
                tableName = historyItemEntry.TABLE_NAME_JOURNAL;
                routableClass = open.furaffinity.client.fragments.journal.class.getName();
                break;
            case 1:
                tableName = historyItemEntry.TABLE_NAME_USER;
                routableClass = open.furaffinity.client.fragments.user.class.getName();
                break;
            case 2:
                tableName = historyItemEntry.TABLE_NAME_VIEW;
                routableClass = open.furaffinity.client.fragments.view.class.getName();
                break;
        }

        Cursor cursor = db.query(
                tableName,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        while (cursor.moveToNext()) {
            HashMap<String, String> newItem = new HashMap<>();
            String itemUser = cursor.getString(cursor.getColumnIndexOrThrow(historyItemEntry.COLUMN_NAME_USER));
            String itemTitle = cursor.getString(cursor.getColumnIndexOrThrow(historyItemEntry.COLUMN_NAME_TITLE));
            String itemURL = cursor.getString(cursor.getColumnIndexOrThrow(historyItemEntry.COLUMN_NAME_URL));
            long itemDateTime = cursor.getLong(cursor.getColumnIndexOrThrow(historyItemEntry.COLUMN_NAME_DATETIME));

            LocalDateTime itemLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(itemDateTime), TimeZone.getDefault().toZoneId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

            newItem.put("class", routableClass);
            newItem.put("path", itemURL);

            if (itemTitle == null || itemTitle.equals("")) {
                newItem.put("item", itemUser + " viewed on " + formatter.format(itemLocalDateTime));
            } else {
                newItem.put("item", itemTitle + " by " + itemUser + " viewed on " + formatter.format(itemLocalDateTime));
            }
            mDataSet.add(newItem);
        }

        db.close();
    }

    private void updateUIElements() {
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new historyListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        currentView = getArguments().getInt(messageIds.historyListPage_MESSAGE);
        getElements(rootView);
        fetchPageData();
        updateUIElements();
        return rootView;
    }
}
