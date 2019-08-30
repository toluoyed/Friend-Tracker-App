package com.example.android.hw3p2;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Location mLocation;
    double latitude;
    double longitude;
    double latitude1;
    double longitude1;
    long timestamp;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference ref;
    private LocationReq lq;
    private final int Rad = 6371;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();
        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();
        timestamp = System.currentTimeMillis();

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Information");

        storeInfo();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void storeInfo(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        LocationReq locationReq = new LocationReq(latitude, longitude, timestamp);


        databaseReference.child(user.getUid()).setValue(locationReq);

        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();

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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        lq = new LocationReq();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Information");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    lq = ds.getValue(LocationReq.class);
                    latitude1 = lq.getLatitude();
                    longitude1 = lq.getLongitude();

                    double diffLat = Math.toRadians(latitude1 - latitude);
                    double diffLon = Math.toRadians(longitude1 - longitude);
                    double a = Math.sin(diffLat/2)* Math.sin(diffLat/2) + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude))* Math.sin(diffLon/2) * Math.sin(diffLon/2);

                    double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
                    double dist = Rad*c;

                    if (dist >= -1.60934 || dist <= 1.60934 ) {

                        // Add a marker in Sydney and move the camera
                        LatLng sydney = new LatLng(latitude1, longitude1);
                        mMap.addMarker(new MarkerOptions().position(sydney).title(firebaseAuth.getCurrentUser().getDisplayName()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(latitude1, longitude1);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Im here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/



    }


    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.signOut();
    }
}
