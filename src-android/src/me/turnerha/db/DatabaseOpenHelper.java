package me.turnerha.db;

import me.turnerha.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class should only be used by other classes in the me.turnerha.db package
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static final int version = 1;
	private static final String dbName = "upload-queue.db";

	protected static final String TABLE_NAME_Cell_Network = "cellNetRecords";
	protected static final String TABLE_NAME_WiFi_Network = "wifiRecords";

	/** Create statement for the cellular network table */
	private static final String CREATE_TABLE_Cellular = "CREATE TABLE      "
			+ TABLE_NAME_Cell_Network + "                                  "
			+ "(                                                           "
			+ "    _id      INTEGER PRIMARY KEY,                           "
			+ "    rssi     INTEGER,                                       "
			+ "    lat      REAL,                                          "
			+ "    lon      REAL,                                          "
			+ "    accuracy REAL                                           "
			+ ");                                                          ";

	/**
	 * Note that whoever calls this method should also be the one calling
	 * db.open and db.close. If you don't know how to properly manage the
	 * database lifecycle, please use the methods in CellularUploadQueue that
	 * accept a Context parameter - they will manage the database for you
	 */
	public DatabaseOpenHelper(Context c) {
		super(c, dbName, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v("Creating the DB");
		db.execSQL(CREATE_TABLE_Cellular);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
