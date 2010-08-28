package me.turnerha.db;

import android.location.Location;

public class CellularSignalRecord {
	int rssi_;
	double lat_, lon_, accuracy_;
	long recordId_ = -1;

	/**
	 * By accepting high-level objects instead of low-level parameters, this
	 * method can centralize what low-level parameters we are interested in
	 * gathering.
	 */
	public CellularSignalRecord(int rssi, Location loc) {
		rssi_ = rssi;
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
		rssi_ = rssi;
		lat_ = lat;
		lon_ = lon;
		accuracy_ = accuracy;
		recordId_ = rowId;
	}

	public int getRssi() {
		return rssi_;
	}

	protected long getRowId() {
		if (recordId_ == -1)
			throw new IllegalStateException("The record id was not set");
		return recordId_;
	}

	public double getLon() {
		return lon_;
	}

	public double getLat() {
		return lat_;
	}

	public double getAccuracy() {
		return accuracy_;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("CellularSignalRecord [row:");
		sb.append(recordId_).append(", rssi:").append(rssi_).append(", lat:")
				.append(lat_).append(", lon:").append(lon_).append("]");
		return sb.toString();
	}
}
