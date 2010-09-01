package me.turnerha;

import me.turnerha.db.CellularUploadQueue;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InetStateReceiver extends BroadcastReceiver {

	/** Turns on / off the queue uploading services */
	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkInfo ni = (NetworkInfo) intent
				.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

		Log.v("NetworkInfo state is ", ni.getState());
		boolean connection;
		if (ni.getState() == NetworkInfo.State.CONNECTED)
			connection = true;
		else if (ni.getState() == NetworkInfo.State.DISCONNECTED)
			connection = false;
		else
			return;

		final Intent cellularService = new Intent(context,
				CellularUploadService.class);

		if (connection) { // Turn upload on
			// TODO - Believe it or not, this call to getSystemService can take
			// upwards of a second and block the main thread for that long! We
			// should figure out a better way to handle this ...

			// Check that we are cleared to use data in the background
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			Log.v("Background Data Setting is ", cm.getBackgroundDataSetting());
			if (cm.getBackgroundDataSetting() == false)
				return;

			// This is to prevent the overhead of turning on a service which
			// immediately turns itself back off
			if (CellularUploadQueue.size(context) == 0) {
				//Log.i("There are no records to upload.");
				//Log.i("For now we will insert some records for testing purposes");
				//Log.i("Later on we will simply not start the service");
				
				//TestPopulateCellQueue a = new TestPopulateCellQueue();
				//a.run(context);
				
				return;
			}

			Log.i("Starting cellular upload service");
			context.startService(cellularService);

		} else { // Turn upload off
			Log.i("Stopping cellular upload service");
			context.stopService(cellularService);
		}

	}
}
