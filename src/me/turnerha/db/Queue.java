package me.turnerha.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

public class Queue {

	private static final String NetworkQueueTableName = "networkQueue";
	private static final String NetworkQueueRSSIColumn = "rssi";
	private static final String NetworkQueueLatitudeColumn = "lat";
	private static final String NetworkQueueLongitudeColumn = "lon";
	private static final String NetworkQueueLocationAccuracyColumn = "accuracy";

	public static void queueNetwork(int rssi, Location timePlaceValues,
			Context context) {
		if ((context == null) || (timePlaceValues == null))
			return;

		OpenHelper oh = new OpenHelper(context);
		SQLiteDatabase db = null;
		try {
			db = oh.getWritableDatabase();
		} catch (SQLException sle) {
			Log.i("WiFiReporter", "Unable to open a writable database");
			return;
		}

		ContentValues values = new ContentValues(4);
		values.put(NetworkQueueRSSIColumn, rssi);
		values.put(NetworkQueueLatitudeColumn, timePlaceValues.getLatitude());
		values.put(NetworkQueueLongitudeColumn, timePlaceValues.getLongitude());
		values.put(NetworkQueueLocationAccuracyColumn, timePlaceValues
				.getAccuracy());
		long row = db.insert(NetworkQueueTableName, null, values);

		if (row == -1) {
			Log.i("WiFiReporter",
					"Unable to insert successfully into the database");
		}
		
		db.close();
	}
}
