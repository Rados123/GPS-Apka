package com.example.gps_apka;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;


import java.io.IOException;
import java.util.List;

public class ShowSavedLocationsList extends AppCompatActivity {
    TextView tv_savedlocations;
    List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_locations_list);
        tv_savedlocations = findViewById(R.id.tv_savedlocations);
        MyApplication myApplication = (MyApplication)getApplicationContext();
        savedLocations = myApplication.getMyLocations();

        String finalstring = "";
        Geocoder geocoder = new Geocoder(ShowSavedLocationsList.this);
        try {
            for(Location location: savedLocations){
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                String locale = "Odwiedzona lokalizacja miała następujące współrzędne: \n" + "Szerokość:" + location.getLatitude() +" Długość:" + location.getLongitude() + " Dokładność: " + location.getAccuracy() + "\nAdres: " + addresses.get(0).getAddressLine(0);
                //tv_savedlocations.setText(("Szerokość:" + location.getLatitude() +" Długość:" + location.getLongitude()));
                finalstring = finalstring + locale + "\n\n";
            }
        } catch (Exception e ) {
            for(Location location: savedLocations){
                String locale = "Szerokość:" + location.getLatitude() +" Długość:" + location.getLongitude();
                //tv_savedlocations.setText(("Szerokość:" + location.getLatitude() +" Długość:" + location.getLongitude()));
                finalstring = finalstring + locale + "\n\n";
            }
        }
        tv_savedlocations.setText(finalstring);
/*
        for(Location location: savedLocations){
                String locale = "Szerokość:" + location.getLatitude() +" Długość:" + location.getLongitude();
                //tv_savedlocations.setText(("Szerokość:" + location.getLatitude() +" Długość:" + location.getLongitude()));
                finalstring = finalstring + locale + "\n\n";
        }
        tv_savedlocations.setText(finalstring);
/*
        String finalstring = "";
        for(Location location: savedLocations){
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                String locale = addresses.get(0).getAddressLine(0);
                tv_savedlocations.setText(locale);
                finalstring = finalstring + locale + "\n\n";

            } catch (IOException e) {

                tv_savedlocations.setText(("Szerokość:" + location.getLatitude() +" Długość:" + location.getLongitude()));
            }
        }
        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e ) {
            tv_address.setText("Nie udalo sie uzyskac adresu");
        }
 */
        //pobiera i uklada zapisane lokalizacje

    }
}