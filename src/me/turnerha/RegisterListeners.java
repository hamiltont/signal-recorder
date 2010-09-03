package me.turnerha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

/**
 * Simply starts the GpsCorrelator and registers it for various sensors
 * 
 * @author hamiltont
 * 
 */
public class RegisterListeners extends BroadcastReceiver {
	boolean called = false;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (called)
			Log.e("We should never have been called again");
		called = true;

		// We should never be called again
		// context.unregisterReceiver(this);

		GpsCorrelator cr = new GpsCorrelator(context);

		// We never want location updates from these - we simply use the
		// getLastKnownLoc internally. We just want to be notified if GPS turns
		// on/off
		// I wish there was a broadcast receiver for this . . .
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		lm.addGpsStatusListener(cr);

		// lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// Long.MAX_VALUE, Float.MAX_VALUE, cr);

		Log.i("Registered for the location updates");
	}
}
