package me.turnerha.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {

	private static final int version = 1;
	private static final String dbName = "store.db";

	public OpenHelper(Context c) {
		super(c, dbName, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db
				.execSQL("CREATE TABLE networkQueue (rssi INTEGER, lat REAL, lon REAL, accuracy REAL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
