package open.furaffinity.client.sqlite;

import android.provider.BaseColumns;

public final class HistoryContract {
    private HistoryContract() {
    }

    public static class historyItemEntry implements BaseColumns {
        public static final String TABLE_NAME_JOURNAL = "journalHistory";
        public static final String TABLE_NAME_USER = "userHistory";
        public static final String TABLE_NAME_VIEW = "viewHistory";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_DATETIME = "dateTime";
    }
}