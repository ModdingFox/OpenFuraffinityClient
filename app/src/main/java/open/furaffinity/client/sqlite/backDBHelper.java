package open.furaffinity.client.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import open.furaffinity.client.sqlite.backContract.backItemEntry;

public class backDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "backHistory.db";

    private static final String SQL_CREATE_BACK_HISTORY_TABLE_START = "CREATE TABLE IF NOT EXISTS ";
    private static final String SQL_CREATE_BACK_HISTORY_TABLE_END = "(" + backItemEntry.COLUMN_NAME_FRAGMENT_NAME + " TEXT," + backItemEntry.COLUMN_NAME_FRAGMENT_DATA + " TEXT," + backItemEntry.COLUMN_NAME_DATETIME + " DATETIME" + ")";

    private static final String SQL_DROP_BACK_HISTORY_TABLE_START = "DROP TABLE ";

    public backDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_BACK_HISTORY_TABLE_START + backItemEntry.TABLE_NAME_BACK_HISTORY + SQL_CREATE_BACK_HISTORY_TABLE_END);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_BACK_HISTORY_TABLE_START + backItemEntry.TABLE_NAME_BACK_HISTORY);
        db.execSQL(SQL_CREATE_BACK_HISTORY_TABLE_START + backItemEntry.TABLE_NAME_BACK_HISTORY + SQL_CREATE_BACK_HISTORY_TABLE_END);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
