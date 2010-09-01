package me.turnerha.db;

import me.turnerha.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CellularUploadQueue {

	private static final String TABLE_NAME = DatabaseOpenHelper.TABLE_NAME_Cell_Network;
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_Rssi = "rssi";
	private static final String COLUMN_Latitude = "lat";
	private static final String COLUMN_Longitude = "lon";
	private static final String COLUMN_Accuracy = "accuracy";
	private static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_Rssi,
			COLUMN_Latitude, COLUMN_Longitude, COLUMN_Accuracy };

	public static void enqueue(CellularSignalRecord record, Context context) {
		if ((context == null) || (record == null))
			return;

		Log.v("Enqueuing a record:", record);

		DatabaseOpenHelper oh = new DatabaseOpenHelper(context);
		SQLiteDatabase db = null;
		db = oh.getWritableDatabase();

		enqueue(record, db);

		db.close();
	}

	/**
	 * If you are using a DB multiple times back to back, this method is useful
	 * for allowing you to open the DB once, and then perform tons of methods
	 * before closing it.
	 * 
	 * @param record
	 * @param db
	 *            an open writable database. This method will not close the
	 *            database, only any Cursors that it creates
	 */
	public static void enqueue(CellularSignalRecord record, SQLiteDatabase db) {
		ContentValues values = new ContentValues(4);
		values.put(COLUMN_Rssi, record.getRssi());
		values.put(COLUMN_Latitude, record.getLat());
		values.put(COLUMN_Longitude, record.getLon());
		values.put(COLUMN_Accuracy, record.getAccuracy());
		long row = db.insert(TABLE_NAME, null, values);

		if (row == -1) {
			Log.i("Unable to insert successfully into the database");
		}

		Log.v("Successfuly enqueued:", record);
	}

	/**
	 * Displays, but does not remove, the item at the head of the queue.
	 * 
	 * @return A {@link CellularSignalRecord} representing the latest item in
	 *         the queue, or null if the queue is empty
	 * 
	 */
	public static CellularSignalRecord getNext(Context context) {
		Log.v("Getting next record");

		DatabaseOpenHelper oh = new DatabaseOpenHelper(context);
		SQLiteDatabase db = oh.getReadableDatabase();

		CellularSignalRecord record = getNext(db);
		db.close();
		return record;
	}

	/**
	 * If you are using a DB multiple times back to back, this method is useful
	 * for allowing you to control opening and closing the DB, and performing
	 * many method calls rapidly in the middle.
	 * 
	 * @param record
	 * @param db
	 *            an open readable database. This method will not close the
	 *            database, only any Cursors that it creates
	 */
	public static CellularSignalRecord getNext(SQLiteDatabase db) {

		if (db.isOpen() == false)
			throw new IllegalStateException(
					"The db must have already been opened with getReadableDatabase");

		Cursor c = db.query(TABLE_NAME, ALL_COLUMNS, null, null, null, null,
				COLUMN_ID + " ASC");
		if (c.moveToFirst() == false) {
			Log.i("There are no objects left in the database!");
			Log.i("Returning null");
			return null;
		}

		long id = c.getLong(c.getColumnIndex(COLUMN_ID));
		int rssi = c.getInt(c.getColumnIndex(COLUMN_Rssi));
		double lat = c.getDouble(c.getColumnIndex(COLUMN_Latitude));
		double lon = c.getDouble(c.getColumnIndex(COLUMN_Longitude));
		double acc = c.getDouble(c.getColumnIndex(COLUMN_Accuracy));

		c.close();

		CellularSignalRecord cm = new CellularSignalRecord(rssi, lat, lon, acc,
				id);

		Log.v("Got next record:", cm);
		return cm;
	}

	public static void remove(Context context, CellularSignalRecord cm) {
		Log.v("Removing record:", cm);

		DatabaseOpenHelper oh = new DatabaseOpenHelper(context);
		SQLiteDatabase db = null;
		db = oh.getWritableDatabase();

		remove(db, cm);
		db.close();
	}

	/**
	 * If you are using a DB multiple times back to back, this method is useful
	 * for allowing you to open the DB once, and then perform tons of methods
	 * before closing it.
	 * 
	 * @param db
	 *            an open writable database. This method will not close the
	 *            database, only any Cursors that it creates
	 */
	public static void remove(SQLiteDatabase db, CellularSignalRecord cm) {
		StringBuilder sb = new StringBuilder(COLUMN_ID);
		sb.append("='").append(cm.getRowId()).append("'");

		int rows = db.delete(TABLE_NAME, sb.toString(), null);
		if (rows != 1) {
			Log.w("More than one row deleted in 'remove'!");
			Log.w(rows, " rows removed!");
		}

		Log.v("Removed record:", cm);
	}

	/** Returns the number of measurements waiting to be uploaded */
	public static int size(Context context) {
		if (context == null)
			throw new IllegalArgumentException(
					"Context parameter cannot be null");

		DatabaseOpenHelper openHelper = new DatabaseOpenHelper(context);
		SQLiteDatabase db = null;
		db = openHelper.getReadableDatabase();
		int count = size(db);
		db.close();

		return count;
	}

	/**
	 * If you are using a DB multiple times back to back, this method is useful
	 * for allowing you to open the DB once, and then perform tons of methods
	 * before closing it.
	 * 
	 * @param db
	 *            an open readable database. This method will not close the
	 *            database, only any Cursors that it creates
	 */
	public static int size(SQLiteDatabase db) {
		Cursor c = db.query(TABLE_NAME, new String[] { COLUMN_Rssi }, null,
				null, null, null, null);
		int count = c.getCount();
		c.close();
		return count;
	}

}
