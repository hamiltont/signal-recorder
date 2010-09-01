package me.turnerha;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;

public class TempMain extends Activity {

	
	protected void onStart() {
		super.onStart();
		
		Context context = this.getApplicationContext();
		
		CellularRecorder cr = new CellularRecorder(context);

		// We never want location updates from these - we simply use the
		// getLastKnownLoc internally. We just want to be notified if GPS turns
		// on/off
		// I wish there was a broadcast receiver for this . . .
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Long.MAX_VALUE,
				Float.MAX_VALUE, cr);

		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				Long.MAX_VALUE, Float.MAX_VALUE, cr);

		Log.i("Registered for the location updates");

	}
}
