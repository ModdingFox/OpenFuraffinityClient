package open.furaffinity.client.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import open.furaffinity.client.sqlite.historyContract.historyItemEntry;

public class historyDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "history.db";

    private static final String SQL_CREATE_HISTORY_TABLE_START = "CREATE TABLE IF NOT EXISTS ";
    private static final String SQL_CREATE_HISTORY_TABLE_END = "(" + historyItemEntry.COLUMN_NAME_USER + " TEXT," + historyItemEntry.COLUMN_NAME_TITLE + " TEXT," + historyItemEntry.COLUMN_NAME_URL + " TEXT," + historyItemEntry.COLUMN_NAME_DATETIME + " DATETIME" + ")";

    private static final String SQL_DROP_VIEW_HISTORY_TABLE_START = "DROP TABLE ";

    public historyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HISTORY_TABLE_START + historyItemEntry.TABLE_NAME_JOURNAL + SQL_CREATE_HISTORY_TABLE_END);
        db.execSQL(SQL_CREATE_HISTORY_TABLE_START + historyItemEntry.TABLE_NAME_USER + SQL_CREATE_HISTORY_TABLE_END);
        db.execSQL(SQL_CREATE_HISTORY_TABLE_START + historyItemEntry.TABLE_NAME_VIEW + SQL_CREATE_HISTORY_TABLE_END);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_VIEW_HISTORY_TABLE_START + historyItemEntry.TABLE_NAME_JOURNAL);
        db.execSQL(SQL_DROP_VIEW_HISTORY_TABLE_START + historyItemEntry.TABLE_NAME_USER);
        db.execSQL(SQL_DROP_VIEW_HISTORY_TABLE_START + historyItemEntry.TABLE_NAME_VIEW);
        db.execSQL(SQL_CREATE_HISTORY_TABLE_START + historyItemEntry.TABLE_NAME_JOURNAL + SQL_CREATE_HISTORY_TABLE_END);
        db.execSQL(SQL_CREATE_HISTORY_TABLE_START + historyItemEntry.TABLE_NAME_USER + SQL_CREATE_HISTORY_TABLE_END);
        db.execSQL(SQL_CREATE_HISTORY_TABLE_START + historyItemEntry.TABLE_NAME_VIEW + SQL_CREATE_HISTORY_TABLE_END);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
