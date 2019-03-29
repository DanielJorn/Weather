package example.yuratoxa.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {

    List<Forecast> currentForecasts;
    MainActivity activity = this;
    final String UNITS = "metric";
    final String APPID = "e629281ca671e33a2ec57254d2e30e12";
    final String LANG = "ua";
    DataAdapter forecastAdapter;
    List<WeatherList> allWeatherLists;
    final int REQUEST_GPS = 1;
    int log, lat = 0;
    AlertDialog settingsAlert, onProviderDisabledAlert;

    static final String TAG = "my tag";

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.settings_alert_title)
                .setMessage(R.string.settings_alert_message)
                .setIcon(R.drawable.loading)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialog, id) -> startActivity
                        (new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        settingsAlert = builder.create();
        builder.setMessage(R.string.on_provider_disabled);
        onProviderDisabledAlert = builder.create();

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(this, "Turn on an internet connection", Toast.LENGTH_LONG).show();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "onCreate: request permission");
            Toast.makeText(this, "This app needs a location permission." +
                            " Please allow it in Settings -> App -> *app name* -> Permissions.",
                    Toast.LENGTH_LONG).show();

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            Log.d(TAG, "onCreate: permission granted");
            // Permission has already been granted
            if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                settingsAlert.show();
            else
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1000,
                        locationListener);
        }

        //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);


        //TODO провірка, чи ввімкнутий провайдер. Якщо ні - алерт діалог що перекида в налаштування.
        setFirstData();

        RecyclerView recyclerView = findViewById(R.id.list);
        // создаем адаптер
        forecastAdapter = new DataAdapter(this, allWeatherLists);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(forecastAdapter);
    }

    @Override
    protected void onResume() {
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            settingsAlert.show();
        super.onResume();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GPS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            0, 1000, locationListener);
                else settingsAlert.show();
        }
    }

    private void setFirstData() {
        if (currentForecasts == null) {
            currentForecasts = new ArrayList<>();
            for (int i = 0; i < 5; i++)
                currentForecasts.add(new Forecast(this));
        }
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            NetworkService.getInstance()
                    .getJSONApi()
                    .getDataFiveDays(UNITS, APPID, LANG, lat, log)
                    .enqueue(responseCallback);
        }

        @Override
        public void onProviderDisabled(String provider) {
            onProviderDisabledAlert.show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    NetworkService.getInstance()
                            .getJSONApi()
                            .getDataFiveDays(UNITS, APPID, LANG, lat, log)
                            .enqueue(responseCallback);
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    Callback<WeatherDay> responseCallback = new Callback<WeatherDay>() {
        @Override
        public void onResponse
                (@NonNull Call<WeatherDay> call, @NonNull Response<WeatherDay> response) {
            WeatherDay weatherDay = response.body();
            allWeatherLists = weatherDay.getWeatherList();
            forecastAdapter.setAllWeatherLists(allWeatherLists);
            forecastAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(@NonNull Call<WeatherDay> call, @NonNull Throwable t) {
            t.printStackTrace();
            Toast.makeText(activity, R.string.error_downloading, Toast.LENGTH_LONG).show();
        }
    };

}



