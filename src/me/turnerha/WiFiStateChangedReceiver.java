package me.turnerha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class WiFiStateChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final Bundle extras = intent.getExtras();
		if (extras.containsKey(WifiManager.EXTRA_WIFI_STATE) == false) {
			Log.e(Constants.tag, "Received wifi state "
					+ "intent did not contain extra that it should!");
			return;
		}
		
		

		final int wifiState = extras.getInt(WifiManager.EXTRA_WIFI_STATE);
		final Intent wifiService = new Intent(context,
				WiFiReporterService.class);

		switch (wifiState) {

		case WifiManager.WIFI_STATE_DISABLED:
			context.stopService(wifiService);
			break;

		case WifiManager.WIFI_STATE_ENABLED:
			context.startService(wifiService);
			break;
			
		}
	}

}
