package com.example.gps_apka;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    //tworzenie klasy która trzyma przeszłe lokalizacje
    //singleton to klasa która trzyma tylko jeden obiekt

    private  static MyApplication singleton;

    private List<Location> myLocations;
    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    //tworzenie singletona
    public MyApplication getInstance() {
        return singleton;
    }
    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
    }
}
