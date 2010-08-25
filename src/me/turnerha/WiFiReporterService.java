package me.turnerha;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class WiFiReporterService extends Service implements LocationListener {
	private static final long minTime = 1000 * 60; // 60 seconds
	private static final float minDistance = 2;
	private LocationManager locationManager_;
	private WifiManager wifiManager_;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(Constants.tag, "Service created");

		wifiManager_ = (WifiManager) getSystemService(WIFI_SERVICE);

		locationManager_ = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager_.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
		locationManager_.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, this);

		Log.i(Constants.tag, "Added location listeners");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(Constants.tag, "Service destroyed");
		locationManager_.removeUpdates(this);

		Log.i(Constants.tag, "Removed location listeners");

	}

	@Override
	public IBinder onBind(Intent arg) {
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i(Constants.tag, "Provider '" + location.getProvider()
				+ "' reported location of " + location);

		WifiInfo connInfo = wifiManager_.getConnectionInfo();
		Submission s = new Submission(location, connInfo);
		s.submit();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(Constants.tag, "Provider '" + provider + "' disabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(Constants.tag, "Provider '" + provider + "' enabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(Constants.tag, "Provider '" + provider + "' changed status");
	}

}
