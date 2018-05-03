package com.startup.naveen.foodtrack;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager lm;
    Marker mark = null;
    LatLng prev,cur;
    String addr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Firebase.setAndroidContext(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "network provider", Toast.LENGTH_LONG).show();
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String user = getIntent().getExtras().getString("customer");
                    final String hotel = getIntent().getExtras().getString("hotel");
                    Firebase fm2=new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/"+hotel+"/location");
                    fm2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String val = dataSnapshot.getValue(String.class);
                            String pos[]=val.split(",");
                            double lat=Double.parseDouble(pos[0]);
                            double lon=Double.parseDouble(pos[1]);
                            LatLng ll = new LatLng(lat, lon);
                            mMap.addMarker(new MarkerOptions().position(ll).title(hotel).icon(BitmapDescriptorFactory.fromResource(R.mipmap.hotel)));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                    Firebase fm = new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/"+hotel+"/customers/"+user);
                    String ins = String.valueOf(lat) + "," + String.valueOf(lon);
                    Firebase fm1 = fm.child("location");
                    fm1.setValue(ins);
                    LatLng ll = new LatLng(lat, lon);
                    Toast.makeText(getApplicationContext(), "lat=" + String.valueOf(lat) + " long=" + String.valueOf(lon), Toast.LENGTH_LONG).show();
                    Geocoder g = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = g.getFromLocation(lat, lon, 1);
                        String val = addressList.get(0).getAddressLine(1)+","+addressList.get(0).getLocality();
                        if(mark!=null)
                        {
                            cur=ll;
                            mark.remove();
                            Polyline line = mMap.addPolyline(new PolylineOptions()
                                    .add(prev,cur)
                                    .width(10)
                                    .color(Color.BLUE));
                            mMap.addMarker(new MarkerOptions().position(prev).title(addr).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                        }
                        addr=val;
                        prev=ll;
                        mark=mMap.addMarker(new MarkerOptions().position(ll).title(val).icon(BitmapDescriptorFactory.fromResource(R.mipmap.man)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 14.2f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } else if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "gps provider", Toast.LENGTH_LONG).show();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    LatLng ll = new LatLng(lat, lon);
                    Geocoder g = new Geocoder(getApplicationContext());
                    try {
                        List<android.location.Address> addressList = g.getFromLocation(lat, lon, 1);
                        String val = addressList.get(0).getLocality();
                       // mMap.addMarker(new MarkerOptions().position(ll).title(val).icon(BitmapDescriptorFactory.fromResource(R.mipmap.man)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 18.2f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(true);
        Toast.makeText(getApplicationContext(),"tracking....",Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        GoogleMap m1=googleMap;
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //m1.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //m1.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
