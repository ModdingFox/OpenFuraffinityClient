package open.furaffinity.client.sqlite;

import android.provider.BaseColumns;

public final class BackContract {
    private BackContract() {
    }

    public static class backItemEntry implements BaseColumns {
        public static final String TABLE_NAME_BACK_HISTORY = "backHistory";
        public static final String COLUMN_NAME_FRAGMENT_NAME = "fragmentName";
        public static final String COLUMN_NAME_FRAGMENT_DATA = "fragmentData";
        public static final String COLUMN_NAME_DATETIME = "dateTime";
    }
}