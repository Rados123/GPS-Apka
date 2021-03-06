package com.example.gps_apka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 10;
    public static final int FASTEST_UPDATE_INTERVAL = 3;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    //powiazanie UI
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_countofCrumbs;
    Button btn_newWayPoint, btn_shwoWayPoint, btn_showmap;
    Switch sw_locationsupdates, sw_gps;

    //plik konfiguracyjny dla API google
    LocationRequest locationrequest;
    LocationCallback locationCallBack;

    //obecna lokalizacja
    Location currentLocation;
    //lista lokalizacji
    List<Location> savedLocations;

    //API googla na tym sie wszystko opiera
    FusedLocationProviderClient fusedLocationProviderClient;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
        btn_newWayPoint = findViewById(R.id.btn_newWayPoint);
        btn_shwoWayPoint = findViewById(R.id.btn_shwoWayPoint);
        tv_countofCrumbs = findViewById(R.id.tv_countofCrumbs);
        btn_showmap = findViewById(R.id.btn_showmap);

        // kofiguracja locationrequest
        LocationRequest locationrequest = LocationRequest.create();
        //jak cz??sto sprawdza lokacje, wycaigamy mno??nik do zmiennej do gory ??eby ??atwo zmienic
        locationrequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        //jak czesto sprawdza lokacje na najwyzszym wykorzystaniu aplikacji
        locationrequest.setFastestInterval(1000 * FASTEST_UPDATE_INTERVAL);
        locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //super.onLocationResult(locationResult);
                if (locationrequest == null) {
                    return;
                }
                //zapisanie lokalizacji
                //updateUIValues(locationResult.getLastLocation());
                for (Location location: locationResult.getLocations()) {
                    updateUIValues(location);
                }
            }
        };

        btn_newWayPoint.setOnClickListener(view -> {
            //dostajemy lokalizacj?? GPS
            //dodajemy do zapisanej listy, wymagana zmiana w manife??cie
            MyApplication myApplication = (MyApplication) getApplicationContext();
            savedLocations = myApplication.getMyLocations();
            savedLocations.add(currentLocation);
            if (savedLocations != null) {
                tv_countofCrumbs.setText(Integer.toString(savedLocations.size()));
            }
        });

        btn_shwoWayPoint.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ShowSavedLocationsList.class);
            startActivity(i);
        });

        btn_showmap.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);
        });

        sw_gps.setOnClickListener(view -> {
            if (sw_gps.isChecked()) {
                //zwiekszamy pobor pradu i dokladnosc GPS
                locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                tv_sensor.setText("GPS");
            } else {
                locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                tv_sensor.setText("Sie?? kom??rkowa i Wi-Fi");
            }
        });

        //tworzymy 2 odrebne metody do
        sw_locationsupdates.setOnClickListener(view -> {
            if (sw_locationsupdates.isChecked()) {
                startLocationUpdates();

            } else {
                stopLocationUpdates();

            }
        });

        //u??ycie metody do zebrania danych
        updateGPS();
    } //koniec metody on create

    private void stopLocationUpdates() {
        tv_updates.setText("Lokacja nie jest aktualizowana na bie????co");
        tv_lat.setText("Brak danych");
        tv_lon.setText("Brak danych");
        tv_speed.setText("Brak danych");
        tv_address.setText("Brak danych");
        tv_accuracy.setText("Brak danych");
        tv_altitude.setText("Brak danych");
        //tv_sensor.setText("Brak danych");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {

        tv_updates.setText("Lokacja jest aktualizowana na bie????co");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationrequest, locationCallBack,null );
            updateGPS();

        }
        //fusedLocationProviderClient.requestLocationUpdates(locationrequest, locationCallBack, null);
        //updateGPS();
    }

    //metoda
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "Wymagane jest udzielenie zgody aby aplikacja dzia??a????", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        //uzyskanie pozwolen od uzytkownika
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //zgoda wyrazona przez usera
            //pokazanie lokalizacji
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    //skoro dostalismy zgode to pobieramy lokalizacje i ja wysylamy do metody updateUIValues i do zmiennej currentlocation
                    currentLocation = location;
                    updateUIValues(location);
                }
            });
        } else {
            // nie mamy zgody, sprawdzamy wersje androida
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }

    }
    //przekazanie informacji do UI
    @SuppressLint("SetTextI18n")
    private void updateUIValues(Location location) {
        // aktualizujemy tekst view
        //currentLocation = location;
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        //nie wszystkie telefony moga pokazywac wysokosc, szybkosc
        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            tv_altitude.setText("Nie dost??pne w tym modelu telefonu");
        }
        if (location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        } else {
            tv_speed.setText("Nie dost??pne w tym modelu telefonu");
        }

        // Uzyskiwanie adresu od google szczeg????owa informacja w get(0).
        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e ) {
            tv_address.setText("Nie udalo sie uzyskac adresu");
        }

        MyApplication myApplication = (MyApplication)getApplicationContext();
        savedLocations = myApplication.getMyLocations();
        //pokaz liste lokalizacji
        tv_countofCrumbs.setText(Integer.toString( savedLocations.size()));

    }

}