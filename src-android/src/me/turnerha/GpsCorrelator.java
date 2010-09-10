package me.turnerha;

import me.turnerha.db.CellularSignalRecord;
import me.turnerha.db.CellularUploadQueue;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

/**
 * Handles correlating the location signal from the gps provider and the
 * cellular signal strength. When GPS is turned on, the call to
 * getLastKnownLocation is actually pretty useful
 * 
 * This is pretty interesting. We should act differently with the GPS versus the
 * Network when interacting with the phone state listener. The GPS, once someone
 * is using it - actually changes state relatively frequently and we can rely on
 * the lastKnownLocation call on the gps provider to give us decent enough data
 * to correlate the GPS and the SignalStrength. However, the Network location
 * provider updates very infrequently, so we don't want to leave the
 * PhoneStateListener turned on or we will kill the phone's resources polling
 * the Phone state when then
 */
public class GpsCorrelator implements GpsStatus.Listener {
	Context context_;
	TelephonyManager telephonyManager_;
	LocationManager locationManager_;

	// Number of minutes before a signal
	// reading an a location are
	// considered not associated
	private static final int TIMEOUT = 5;

	public GpsCorrelator(Context c) {
		context_ = c.getApplicationContext();

		telephonyManager_ = (TelephonyManager) c
				.getSystemService(Context.TELEPHONY_SERVICE);
		locationManager_ = (LocationManager) c
				.getSystemService(Context.LOCATION_SERVICE);

		// NOTE:: You cannot rely on the using the
		// LocationManager.isProviderEnabled to see if the GPS System is turned
		// on. Just because a provider is enabled does not in fact mean that
		// that provider is currently working.

		// NOTE CONTINUED:: Rely only on the GpsStatusChanged
		// events. If there was a way to query the GPS system status then we
		// could use that here to handle the case where the GPS system is
		// already on when we are installed, but I don't know of a way

	}

	@Override
	public void onGpsStatusChanged(int event) {
		switch (event) {
		case GpsStatus.GPS_EVENT_STARTED:
			Log.i("GPS System Started. Waiting for first fix");
			break;
		case GpsStatus.GPS_EVENT_STOPPED:
			Log.i("GPS system stopped, stopping phone state listener");
			telephonyManager_.listen(phoneStateListener_,
					PhoneStateListener.LISTEN_NONE);
			break;
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			Log.i("Received first fix, starting phone state listener");
			telephonyManager_.listen(phoneStateListener_,
					PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			break;
		}

	}

	private PhoneStateListener phoneStateListener_ = new PhoneStateListener() {

		/**
		 * Gets the most recently known GPS location. If it was recorded
		 * recently enough, then this location and the signal strength are
		 * associated and considered a single record
		 */
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			Log.v("Signal strength changed");

			Location gpsLoc = locationManager_
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			long current = System.currentTimeMillis();
			Log.v("The time difference b/w location and signal sensors is ",
					(current - gpsLoc.getTime()) / 1000, " seconds");

			if ((current - gpsLoc.getTime()) < 1000 * 60 * TIMEOUT) {
				Log.v("This is good enough, we will save as a record");

				CellularSignalRecord csr = new CellularSignalRecord(
						signalStrength, gpsLoc);
				CellularUploadQueue.enqueue(csr, context_);

				Log.v("Done saving record");
			} else
				Log.v("This is not good enough, we will not save");

		}
	};

}
