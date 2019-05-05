package example.yuratoxa.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    MainActivity activity = this;
    final String UNITS = "metric";
    final String APPID = "e629281ca671e33a2ec57254d2e30e12";
    String lang = "en";
    DataAdapter forecastAdapter;
    List<WeatherList> allWeatherLists;
    final int REQUEST_GPS = 1;
    double lon, lat = 0;
    AlertDialog settingsAlert, onProviderDisabledAlert;
    SharedPreferences sharedPreferences;
    String savedFileName = "last_forecast.data";
    ThemeManager themeManager;

    static final String TAG = "my_tag";

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        themeManager = new ThemeManager(this);
        themeManager.setRightTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log.d(TAG, "theme name " + themeManager.getCurrentThemeName());

        RecyclerView recyclerView = findViewById(R.id.list);
        // создаем адаптер
        forecastAdapter = new DataAdapter(this, allWeatherLists);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(forecastAdapter);

        setFirstData();
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
            requestMyPermissions();
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
    }

    void requestMyPermissions() {

        Log.d(TAG, "onCreate: request permission");
        Toast.makeText(this, R.string.app_needs_permission, Toast.LENGTH_LONG).show();

        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        menu.findItem(R.id.bar_settings).setIntent(new Intent(this,
                MyPreferencesActivity.class));
        return true;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: called");
        if (!themeManager.isRightTheme())
            recreate();
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
        try {
            FileInputStream file = new FileInputStream(getFileStreamPath(savedFileName));
            ObjectInputStream objectInput = new ObjectInputStream(file);
            WeatherDay dayFromStorage = (WeatherDay) objectInput.readObject();
            allWeatherLists = dayFromStorage.getWeatherList();
            forecastAdapter.setAllWeatherLists(allWeatherLists);
            forecastAdapter.notifyDataSetChanged();
            objectInput.close();
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lon = location.getLongitude();

            NetworkService.getInstance()
                    .getJSONApi()
                    .getDataFiveDays(UNITS, APPID, lang, lat, lon)
                    .enqueue(responseCallback);
        }

        @Override
        public void onProviderDisabled(String provider) {
            onProviderDisabledAlert.show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), R.string.provider_enabled, Toast.LENGTH_LONG).show();
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

            try {
                FileOutputStream fos = getApplicationContext().openFileOutput(savedFileName, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(weatherDay);
                os.close();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(@NonNull Call<WeatherDay> call, @NonNull Throwable t) {
            t.printStackTrace();
            Toast.makeText(activity, R.string.error_downloading, Toast.LENGTH_LONG).show();
        }
    };

}



