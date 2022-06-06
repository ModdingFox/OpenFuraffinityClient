package open.furaffinity.client.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import open.furaffinity.client.sqlite.searchContract.searchItemEntry;

public class searchDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "savedSearch.db";

    private static final String SQL_CREATE_HISTORY_TABLE_START = "CREATE TABLE IF NOT EXISTS ";
    private static final String SQL_CREATE_HISTORY_TABLE_END =
        "(" + searchItemEntry.COLUMN_NAME_NAME + " TEXT," +
            searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE + " INTEGER," +
            searchItemEntry.COLUMN_NAME_MOSTRECENTITEM + " TEXT," + searchItemEntry.COLUMN_NAME_Q +
            " TEXT," + searchItemEntry.COLUMN_NAME_ORDERBY + " TEXT," +
            searchItemEntry.COLUMN_NAME_ORDERDIRECTION + " TEXT," +
            searchItemEntry.COLUMN_NAME_RANGE + " INTEGER," +
            searchItemEntry.COLUMN_NAME_RATINGGENERAL + " INTEGER," +
            searchItemEntry.COLUMN_NAME_RATINGMATURE + " INTEGER," +
            searchItemEntry.COLUMN_NAME_RATINGADULT + " INTEGER," +
            searchItemEntry.COLUMN_NAME_TYPEART + " INTEGER," +
            searchItemEntry.COLUMN_NAME_TYPEMUSIC + " INTEGER," +
            searchItemEntry.COLUMN_NAME_TYPEFLASH + " INTEGER," +
            searchItemEntry.COLUMN_NAME_TYPESTORY + " INTEGER," +
            searchItemEntry.COLUMN_NAME_TYPEPHOTO + " INTEGER," +
            searchItemEntry.COLUMN_NAME_TYPEPOETRY + " INTEGER," +
            searchItemEntry.COLUMN_NAME_MODE + " TEXT" + ")";

    private static final String SQL_DROP_VIEW_HISTORY_TABLE_START = "DROP TABLE ";

    public searchDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HISTORY_TABLE_START + searchItemEntry.TABLE_NAME +
            SQL_CREATE_HISTORY_TABLE_END);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_VIEW_HISTORY_TABLE_START + searchItemEntry.TABLE_NAME);
        db.execSQL(SQL_CREATE_HISTORY_TABLE_START + searchItemEntry.TABLE_NAME +
            SQL_CREATE_HISTORY_TABLE_END);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
