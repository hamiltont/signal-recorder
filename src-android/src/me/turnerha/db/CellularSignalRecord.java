package me.turnerha.db;

import android.content.Context;
import android.location.Location;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

public class CellularSignalRecord {
	SignalStrength signal_;
	double lat_, lon_, accuracy_;
	long recordId_ = -1;

	/**
	 * By accepting high-level objects instead of low-level parameters, this
	 * method can centralize what low-level parameters we are interested in
	 * gathering.
	 */
	public CellularSignalRecord(SignalStrength signal, Location loc) {
		signal_ = signal;
		lat_ = loc.getLatitude();
		lon_ = loc.getLongitude();
		accuracy_ = loc.getAccuracy();
	}

	/**
	 * This is used by the database code only. Used to populate the object from
	 * a low-level source.
	 */
	protected CellularSignalRecord(int rssi, double lat, double lon,
			double accuracy, long rowId) {

		lat_ = lat;
		lon_ = lon;
		accuracy_ = accuracy;
		recordId_ = rowId;
	}

	protected int getRssi() {
		return 0;
	}

	protected long getRowId() {
		if (recordId_ == -1)
			throw new IllegalStateException("The record id was not set");
		return recordId_;
	}

	protected double getLon() {
		return lon_;
	}

	protected double getLat() {
		return lat_;
	}

	protected double getAccuracy() {
		return accuracy_;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("CellularSignalRecord [row:");
		sb.append(recordId_).append(", lat:").append(lat_).append(", lon:")
				.append(lon_).append("]");
		return sb.toString();
	}
}
