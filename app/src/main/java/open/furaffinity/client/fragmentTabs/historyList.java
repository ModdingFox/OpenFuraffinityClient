package open.furaffinity.client.fragmentTabs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Toast;

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
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.adapter.historyListAdapter;
import open.furaffinity.client.fragmentDrawersOld.journal;
import open.furaffinity.client.fragmentDrawersOld.user;
import open.furaffinity.client.fragmentDrawersOld.view;
import open.furaffinity.client.sqlite.historyContract.historyItemEntry;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.utilities.messageIds;

public class historyList extends appFragment {
    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;
    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView.Adapter<historyListAdapter.ViewHolder> mAdapter;

    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private int currentView = 0;

    @Override
    protected int getLayout() {
        return R.layout.fragment_recycler_view;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    protected void initPages() {
        if(getArguments() != null) {
            currentView = getArguments().getInt(messageIds.historyListPage_MESSAGE);
        } else {
            Toast.makeText(getActivity(), "Missing current view type", Toast.LENGTH_SHORT).show();
        }

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new historyListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    protected void fetchPageData() {
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
                routableClass = journal.class.getName();
                break;
            case 1:
                tableName = historyItemEntry.TABLE_NAME_USER;
                routableClass = user.class.getName();
                break;
            case 2:
                tableName = historyItemEntry.TABLE_NAME_VIEW;
                routableClass = view.class.getName();
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

        cursor.close();
        db.close();
    }

    @Override
    protected void updateUIElements() {

    }

    @Override
    protected void updateUIElementListeners(View rootView) {

    }
}
