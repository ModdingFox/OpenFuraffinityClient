package open.furaffinity.client.sqlite;

import android.provider.BaseColumns;

public final class SearchContract {
    private SearchContract() {
    }

    public static class searchItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "savedSearches";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_NOTIFICATIONSTATE = "notificationState";
        public static final String COLUMN_NAME_MOSTRECENTITEM = "mostRecentItem";

        public static final String COLUMN_NAME_Q = "q";
        public static final String COLUMN_NAME_ORDERBY = "orderBy";
        public static final String COLUMN_NAME_ORDERDIRECTION = "orderDirection";
        public static final String COLUMN_NAME_RANGE = "range";
        public static final String COLUMN_NAME_RATINGGENERAL = "ratingGeneral";
        public static final String COLUMN_NAME_RATINGMATURE = "ratingMature";
        public static final String COLUMN_NAME_RATINGADULT = "ratingAdult";
        public static final String COLUMN_NAME_TYPEART = "typeArt";
        public static final String COLUMN_NAME_TYPEMUSIC = "typeMusic";
        public static final String COLUMN_NAME_TYPEFLASH = "typeFlash";
        public static final String COLUMN_NAME_TYPESTORY = "typeStory";
        public static final String COLUMN_NAME_TYPEPHOTO = "typePhoto";
        public static final String COLUMN_NAME_TYPEPOETRY = "typePoetry";
        public static final String COLUMN_NAME_MODE = "mode";
    }
}