package open.furaffinity.client.sqlite;

import android.provider.BaseColumns;

public final class browseContract {
    private browseContract() {
    }

    public static class browseItemEntry implements BaseColumns {
        //public static final String TABLE_NAME = "";

        //public static final String COLUMN_NAME_NAME = "name";
        //public static final String COLUMN_NAME_NOTIFICATIONSTATE = "notificationState";
        //public static final String COLUMN_NAME_MOSTRECENTITEM = "mostRecentItem";

        public static final String COLUMN_NAME_CAT = "cat";
        public static final String COLUMN_NAME_ATYPE = "atype";
        public static final String COLUMN_NAME_SPECIES = "species";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_PERPAGE = "perpage";
        public static final String COLUMN_NAME_RATINGGENERAL = "ratingGeneral";
        public static final String COLUMN_NAME_RATINGMATURE = "ratingMature";
        public static final String COLUMN_NAME_RATINGADULT = "ratingAdult";
    }
}