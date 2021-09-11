package ac.id.ubpkarawang.sigeoo.Screens;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ac.id.ubpkarawang.sigeoo.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GeoQueryEventListener {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private DatabaseReference myLocationRef;
    private GeoFire geoFire;
    private List<LatLng> workArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        buildLocationManager();
                        buildLoactionCallback();
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MapsActivity.this);

                        initArea();
                        settingGeofire();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), "Izin akses lokasi ditolak!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initArea() {
        workArea = new ArrayList<>();
        //UBP
        workArea.add(new LatLng(-6.323614159740404, 107.3013096579756));

        //HOME
        workArea.add(new LatLng(-6.406384727950907, 107.45135639781826));

        FirebaseDatabase.getInstance()
                .getReference("WorkArea")
                .push()
                .setValue(workArea)
                .addOnCompleteListener(task -> {
                    Toast.makeText(this, "Lokasi diperbaharui", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void settingGeofire() {
        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(myLocationRef);
    }

    private void buildLoactionCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                if (mMap != null) {
                    geoFire.setLocation("Kamu", new GeoLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()), (key, error) -> {
                        if (currentUser != null) currentUser.remove();
                        currentUser = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()))
                                .title("Kamu"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUser.getPosition(), 12.0f));
                    });
                }
            }
        };
    }

    private void buildLocationManager() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setSmallestDisplacement(10f);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        //Circle geoFire
        for (LatLng latLng : workArea) {
            mMap.addCircle(new CircleOptions().center(latLng)
                    .radius(200) //200m
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF)
                    .strokeWidth(2.0f)
            );

            //Geoquery
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.2f);
            geoQuery.addGeoQueryEventListener(MapsActivity.this);
        }
    }

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        sendNotification("Staf", String.format("sudah memasuki area kampus", key));
    }

    @Override
    public void onKeyExited(String key) {
        sendNotification("Staf", String.format("sudah keluar dari area kampus", key));
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        sendNotification("Staf", String.format("sedang bergerak didalam area kampus", key));
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Toast.makeText(this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String title, String content) {
        String NOTIFICATION_CHANNEL_ID = "sigeoo_multiple_location";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notifikasi",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Deskripsi Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.sigeoo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sigeoo));

        Notification notification = builder.build();
        notificationManager.notify(new Random().nextInt(), notification);
    }
}