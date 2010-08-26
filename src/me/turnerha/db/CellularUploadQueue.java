package me.turnerha.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

public class CellularUploadQueue {

	private static final String TABLE_NAME = DatabaseOpenHelper.TABLE_NAME_Cell_Network;
	private static final String COLUMN_Rssi = "rssi";
	private static final String COLUMN_Latitude = "lat";
	private static final String COLUMN_Longitude = "lon";
	private static final String COLUMN_Accuracy = "accuracy";

	public static void enqueue(int rssi, Location timePlaceValues,
			Context context) {
		if ((context == null) || (timePlaceValues == null))
			return;

		DatabaseOpenHelper oh = new DatabaseOpenHelper(context);
		SQLiteDatabase db = null;
		try {
			db = oh.getWritableDatabase();
		} catch (SQLException sle) {
			Log.i("WiFiReporter", "Unable to open a writable database");
			return;
		}

		ContentValues values = new ContentValues(4);
		values.put(COLUMN_Rssi, rssi);
		values.put(COLUMN_Latitude, timePlaceValues.getLatitude());
		values.put(COLUMN_Longitude, timePlaceValues.getLongitude());
		values.put(COLUMN_Accuracy, timePlaceValues.getAccuracy());
		long row = db.insert(TABLE_NAME, null, values);

		if (row == -1) {
			Log.i("WiFiReporter",
					"Unable to insert successfully into the database");
		}

		db.close();
	}

	/** Returns the number of measurements waiting to be uploaded */
	public static int size(Context context) {
		if (context == null)
			throw new IllegalArgumentException(
					"Context parameter cannot be null");

		DatabaseOpenHelper openHelper = new DatabaseOpenHelper(context);
		SQLiteDatabase db = null;
		db = openHelper.getReadableDatabase();

		Cursor c = db.query(TABLE_NAME, new String[] { COLUMN_Rssi }, null,
				null, null, null, null);
		int count = c.getCount();
		c.close();
		db.close();

		return count;
	}
}
