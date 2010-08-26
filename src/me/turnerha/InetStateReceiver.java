package me.turnerha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class InetStateReceiver extends BroadcastReceiver {
	private static final String tag = InetStateReceiver.class.getSimpleName();

	/** Turns on / off the queue uploading services */
	@Override
	public void onReceive(Context context, Intent intent) {
		boolean connection = intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

		final Intent cellularService = new Intent(context,
				CellularUploadService.class);

		if (connection) { // Turn upload on

			// Check that we are cleared to use data in the background
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			Log.v(tag, "Background Data Setting is "
					+ cm.getBackgroundDataSetting());
			if (cm.getBackgroundDataSetting() == false)
				return;

			// This is to prevent the overhead of turning on a service which
			// immediately turns itself back off (commented out for testing)
			// if (CellularUploadQueue.size(context) == 0)
			// return;

			Log.i(tag, "Starting cellular upload service");
			context.startService(cellularService);

		} else { // Turn upload off
			Log.i(tag, "Stopping cellular upload service");
			context.stopService(cellularService);
		}

	}
}
