package me.turnerha;

import me.turnerha.db.CellularSignalRecord;
import me.turnerha.db.CellularUploadQueue;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

/**
 * This is pretty interesting. We should act differently with the GPS versus the
 * Network when interacting with the phone state listener. The GPS, once someone
 * is using it - actually changes state relatively frequently and we can rely on
 * the lastKnownLocation call on the gps provider to give us decent enough data
 * to correlate the GPS and the SignalStrength. However, the Network location
 * provider updates very infrequently, so we don't want to leave the
 * PhoneStateListener turned on, we will kill the phone's resources.
 */
public class CellularRecorder implements LocationListener {
	Context context_;
	TelephonyManager telephonyManager_;
	LocationManager locationManager_;

	// Number of minutes before a signal
	// reading an a location are
	// considered not associated
	private static final int TIMEOUT = 5;

	public CellularRecorder(Context c) {
		context_ = c.getApplicationContext();

		telephonyManager_ = (TelephonyManager) c
				.getSystemService(Context.TELEPHONY_SERVICE);
		locationManager_ = (LocationManager) c
				.getSystemService(Context.LOCATION_SERVICE);

		// Covers the case where one of the providers is already on
		if (locationManager_.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| locationManager_
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			telephonyManager_.listen(phoneStateListener_,
					PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			Log.v("A provider was enabled, starting signal listener");
		}

	}

	/** Unused */
	@Override
	public void onLocationChanged(Location location) {
	}

	/** Unused */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.v("A provider enabled. Starting signal listener");
		telephonyManager_.listen(phoneStateListener_,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// Covers the case where one provider is enabled and then the other
		// disabled at a later time
		if (locationManager_.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| locationManager_
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Log.v("A provider was disabled, but we still have data avail");
			return;
		}

		telephonyManager_.listen(phoneStateListener_,
				PhoneStateListener.LISTEN_NONE);
		Log.v("All providers disabled. Removing phone listener");
	}

	private PhoneStateListener phoneStateListener_ = new PhoneStateListener() {

		/**
		 * Gets the most recently known location. If it was recorded recently
		 * enough, then this location and the signal strength are associated and
		 * considered a single record
		 */
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			Log.v("Signal strength changed");

			Location gpsLoc = locationManager_
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location cellLoc = locationManager_
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			Location bestLoc = null;
			if (gpsLoc != null)
				bestLoc = gpsLoc;

			// If cell is not null then we should check if it's better than the
			// current bestLoc. It is if bestLoc is null or the cellLoc is more
			// recent
			if ((cellLoc != null)
					&& ((bestLoc == null) || (cellLoc.getTime() > bestLoc
							.getTime())))
				bestLoc = cellLoc;

			Log.v("Best known latest loc:", bestLoc);

			long current = System.currentTimeMillis();
			Log.v("The time difference b/w location and signal sensors is ",
					(current - bestLoc.getTime()) / 1000, " seconds");

			if ((current - bestLoc.getTime()) < 1000 * 60 * TIMEOUT) {
				Log.v("This is good enough, we will save as a record");

				CellularSignalRecord csr = new CellularSignalRecord(
						signalStrength, bestLoc);
				CellularUploadQueue.enqueue(csr, context_);

				Log.v("Done saving record");
			} else
				Log.v("This is not good enough, we will not save");

		}
	};

}
