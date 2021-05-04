package com.example.finaltsunamiapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private static JSONObject data;
    private static String fullWeatherData;
    private static AlertDialog.Builder tsunamiWarning;
    private static Double latitude;
    private static Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        LocationTracker userLocation = new LocationTracker(this);
        Location location = userLocation.getLocation();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        System.out.println(location + "," + latitude + "," + longitude);

        //LocationManager usersLocation = (LocationManager) getSystemService(LOCATION_SERVICE);
        //usersLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        //Location myLocation = usersLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //System.out.println(myLocation);
        //latitude = myLocation.getLatitude();
        //longitude = myLocation.getLongitude();
        //System.out.println(latitude + ", " + longitude);


        tsunamiWarning = new AlertDialog.Builder(this);
        tsunamiWarning.setMessage("THERE IS AN INCOMING TSUNAMI IN YOUR NEARBY AREA! PLEASE EVACUATE THE AREA OR GET TO HIGH GROUND!");
        tsunamiWarning.setTitle("WARNING!");
        tsunamiWarning.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        tsunamiWarning.setNegativeButton("GO TO LOCAL WEBSITE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //tsunamiWarning = new Dialog(this);
        System.out.println("Warning Message Created");
        getJSON();

    }


        public static void getJSON() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected Void doInBackground(Void... params) {
                    try {

                        URL url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+
                                latitude + "&lon=" + longitude +
                                "&exclude=minutely,hourly,daily&appid=9e308f59a5f1b84598aa5782c05968a8");

                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        StringBuffer json = new StringBuffer(1024);
                        String tmp;

                        while((tmp = reader.readLine()) != null) {
                            json.append(tmp).append("\n");
                        }
                        reader.close();

                        data = new JSONObject(json.toString());

                        if(data.getInt("cod") != 200) {
                            System.out.println("Cancelled");
                            return null;
                        }
                    }
                    catch (Exception e) {
                        System.out.println("Exception "+ e.getMessage());
                        return null;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void Void) {
                    if(data!=null) {
                        Log.d("my weather received", data.toString());
                        fullWeatherData = data.toString();
                        System.out.println("String Weather Data: " + fullWeatherData);
                        //weatherLon = Double.parseDouble(fullWeatherData);
                        //System.out.println(weatherLon);
                        //weatherLat = Double.parseDouble(fullWeatherData);
                        //System.out.println(weatherLat);
                        if (fullWeatherData == null){
                            fullWeatherData = "empty";
                            System.out.println("Empty String? " + fullWeatherData);
                        }

                        //if (fullWeatherData.contains("Sydney")){
                        if (fullWeatherData != null){
                            System.out.println("Weather Data is NOT NULL");
                            //if (fullWeatherData.contains("Tsunami")) {
                            if (fullWeatherData.contains("London")) {
                                AlertDialog tsunamiAlert = tsunamiWarning.create();
                                tsunamiAlert.show();
                                System.out.println("Alert worked?");
                            }
                            //Snackbar tsunamiWarning = Snackbar.make(findViewById(), warningMessage, Snackbar.LENGTH_SHORT);
                            //tsunamiWarning.show();
                        }
                    }
                }
            }.execute();
        }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

    }

    public static boolean isLocationEnabled(Context context)
    {
        //...............
        return true;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    public void onProviderDisabled(@NonNull String provider){

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

    }
}