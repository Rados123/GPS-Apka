package com.example.gps_apka;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ShowSavedLocationsList extends AppCompatActivity {
    ListView lv_savedlocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_saved_locations_list);

        lv_savedlocations = findViewById(R.id.lv_savedLocations);

        MyApplication myApplication = (MyApplication)getApplicationContext();
        List<Location> savedLocations = myApplication.getMyLocations();

        //pobiera i uklada zapisane lokalizacje
        lv_savedlocations.setAdapter(new ArrayAdapter<Location>( this, android.R.layout.simple_list_item_1, savedLocations));
    }
}