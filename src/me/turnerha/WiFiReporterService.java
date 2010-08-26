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

public class WiFiReporterService extends Service implements LocationListener {
	private static final long minTime = 1000 * 60; // 60 seconds
	private static final float minDistance = 2;
	private LocationManager locationManager_;
	private WifiManager wifiManager_;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(Log.mainTag, "Service created");

		wifiManager_ = (WifiManager) getSystemService(WIFI_SERVICE);

		locationManager_ = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager_.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
		locationManager_.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, this);

		Log.i(Log.mainTag, "Added location listeners");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(Log.mainTag, "Service destroyed");
		locationManager_.removeUpdates(this);

		Log.i(Log.mainTag, "Removed location listeners");

	}

	@Override
	public IBinder onBind(Intent arg) {
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i(Log.mainTag, "Provider '" + location.getProvider()
				+ "' reported location of " + location);

		WifiInfo connInfo = wifiManager_.getConnectionInfo();
		Submission s = new Submission(location, connInfo);
		s.submit();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(Log.mainTag, "Provider '" + provider + "' disabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(Log.mainTag, "Provider '" + provider + "' enabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(Log.mainTag, "Provider '" + provider + "' changed status");
	}

}
