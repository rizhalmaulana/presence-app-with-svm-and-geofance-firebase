package ac.id.ubpkarawang.sigeoo.Screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ac.id.ubpkarawang.sigeoo.Helpers.LoadingDialog;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.MyLatLng;
import ac.id.ubpkarawang.sigeoo.Model.Utama.SvmItem;
import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Utils.ApiUtils;
import ac.id.ubpkarawang.sigeoo.Utils.FileUtils;
import ac.id.ubpkarawang.sigeoo.Utils.LoadLocationListener;
import ac.id.ubpkarawang.sigeoo.Utils.MobileService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GeoQueryEventListener, LoadLocationListener {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private GeoFire geoFire;
    private List<LatLng> workArea;
    private GeoQuery geoQuery;

    private Location lastLocation;
    private LoadLocationListener listener;

    private static final int REQUEST_CAMERA = 123;
    private static final int REQUEST_LOC = 1001;
    private static final String TAG = "MapsActivity";

    private FirebaseUser firebaseUser;
    private Uri mediaPath;

    BottomSheetDialog bottomSheetDialog;
    ImageView img_masuk, img_pulang, img_sigeoo, img_close;
    MaterialButton bs_absen, btn_maps_proses;
    LinearLayout lrAbsenMasuk, lrAbsenPulang;
    View sheetAbsen;
    TextView txtKetLokasi;
    Snackbar snackbar;
    MobileService mobileService, mobileServiceSvm;
    LoadingDialog loadingDialog;
    File filesDir, imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        mobileService = ApiUtils.MobileService(getApplicationContext());
        mobileServiceSvm = ApiUtils.servicePython(getApplicationContext());

        btn_maps_proses = findViewById(R.id.btn_maps_absen);
        bottomSheetDialog = new BottomSheetDialog(MapsActivity.this, R.style.BottomSheetDialogTheme);
        loadingDialog = new LoadingDialog(MapsActivity.this);

        sheetAbsen = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_absen, findViewById(R.id.bottom_sheet_masuk));
        img_masuk = sheetAbsen.findViewById(R.id.img_sheet_absen_masuk);
        img_pulang = sheetAbsen.findViewById(R.id.img_sheet_absen_pulang);
        img_sigeoo = sheetAbsen.findViewById(R.id.img_sheet_sigeoo);
        img_close = sheetAbsen.findViewById(R.id.img_sheet_close);
        bs_absen = sheetAbsen.findViewById(R.id.btn_sheet_absen);
        lrAbsenMasuk = sheetAbsen.findViewById(R.id.lr_sheet_absen_masuk);
        lrAbsenPulang = sheetAbsen.findViewById(R.id.lr_sheet_absen_pulang);
        txtKetLokasi = sheetAbsen.findViewById(R.id.text_sheet_ket);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                checkConnection();
                turnOnGPS();
                buildLocationManager();
                buildLocationCallback();
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

                initArea();
                settingGeofire();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "Izin akses lokasi ditolak!", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setRationaleTitle("Aktifkan kamera")
                .setRationaleMessage(R.string.izin_kamera)
                .setGotoSettingButtonText("Bla Bla")
                .setDeniedTitle("Izin Ditolak")
                .setDeniedMessage(R.string.izin_kamera_ditolak)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
                .check();
    }

    private void checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            Log.d(TAG, "Status koneksi: Hidup");
        } else {

            Log.d(TAG, "Status koneksi: Mati");

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void turnOnGPS() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Log.d(TAG, "Maps Activity: Lokasi is active");
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(MapsActivity.this, REQUEST_LOC);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            Log.d(TAG, "Maps Activity: " + sendIntentException.getMessage());
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void initArea() {
        DatabaseReference UBPKarawang = FirebaseDatabase.getInstance()
                .getReference("AreaKerja")
                .child("UBPKarawang");

        listener = this;

        UBPKarawang.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // Update data area
                List<MyLatLng> latLngList = new ArrayList<>();
                for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                    MyLatLng latLng = locationSnapshot.getValue(MyLatLng.class);
                    latLngList.add(latLng);
                }
                listener.onLoadLocationSuccess(latLngList);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void addStafMarker() {
        checkConnection();

        geoFire.setLocation(firebaseUser.getUid(), new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), (key, error) -> {
            if (currentUser != null) currentUser.remove();
            currentUser = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                    .title(firebaseUser.getDisplayName()));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                    .zoom(19f)
                    .bearing(30)
                    .tilt(25)
                    .build();

//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUser.getPosition(), 15.0f));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });
    }

    private void settingGeofire() {
        DatabaseReference myLocationRef = FirebaseDatabase.getInstance().getReference("LokasiStaf");
        geoFire = new GeoFire(myLocationRef);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                if (mMap != null) {
                    Log.d(TAG, "Akurasi Lokasi: " + locationResult.getLastLocation().getAccuracy());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.d(TAG, "Akurasi Lokasi Vertikal: " + locationResult.getLastLocation().getVerticalAccuracyMeters());
                    }

                    lastLocation = locationResult.getLastLocation();
                    addStafMarker();
                }
            }
        };
    }

    private void buildLocationManager() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setSmallestDisplacement(20f);
    }

    private void absenMasuk() {
        File file = FileUtils.getFile(this, mediaPath);

        RequestBody requestBody = RequestBody.create(MediaType.parse(String.valueOf(mediaPath)), file);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("data_gambar", file.getName(), requestBody);

        Log.d(TAG, "Part Image: " + partImage);

        loadingDialog.startLoading();
        mobileServiceSvm.postImage(partImage).enqueue(new Callback<SvmItem>() {
            @Override
            public void onResponse(Call<SvmItem> call, Response<SvmItem> response) {
                if (response.isSuccessful()) {
                    SvmItem body = response.body();
                    if (body.isState()) {
                        Log.d(TAG, "Proses SVM: Gambar terkirim");
                    } else {
                        Log.d(TAG, "Proses SVM: null");
                    }
                } else {
                    Log.d(TAG, "Response: " + response.message());
                    Log.d(TAG, "Proses SVM: Tidak sukses");
                }
                loadingDialog.dismissLoading();
            }

            @Override
            public void onFailure(Call<SvmItem> call, Throwable t) {
                Log.d(TAG, "Proses SVM: Gagal response");
                loadingDialog.dismissLoading();
            }
        });
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

        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.sigeoo)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(new Random().nextInt(), notification);
    }

    private void addCircleArea() {
        if (geoQuery != null){
            geoQuery.removeGeoQueryEventListener(this);
            geoQuery.removeAllListeners();
        }

        for (LatLng latLng : workArea) {
            mMap.addCircle(new CircleOptions().center(latLng)
                    .radius(25) //10m
                    .strokeColor(Color.argb(1, 139, 217, 254))
                    .fillColor(0x22FF0000)
                    .strokeWidth(2.0f)
            );

            // Geoquery
            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.025f);
            geoQuery.addGeoQueryEventListener(MapsActivity.this);
        }
    }

    private void showDialogClose() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Konfirmasi")
                .setMessage("Apa anda yakin ingin membatalkan proses absen?");
        alertDialogBuilder
                .setIcon(R.drawable.warning)
                .setCancelable(false)
                .setPositiveButton("Yakin", (dialogInterface, i) -> {
                    bottomSheetDialog.dismiss();

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("Kembali", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void openSnackBar() {
        snackbar = Snackbar.make(findViewById(android.R.id.content), "Kamu sudah di dalam area kampus", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        snackbar.setAction("Lanjutkan", view -> btn_maps_proses.setVisibility(View.VISIBLE));

        snackbar.show();
    }

    private void bukaKamera() {
        Intent pictureAbsenMasuk = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureAbsenMasuk.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureAbsenMasuk, REQUEST_CAMERA);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        // Circle geoFire
        addCircleArea();
    }

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        checkConnection();

        sendNotification(firebaseUser.getDisplayName(), String.format("sudah memasuki area kampus", key));

        double latitude = location.latitude;
        double longitude = location.longitude;

        // Buka SnackBar
        openSnackBar();

        btn_maps_proses.setOnClickListener(viewProses -> {
            bottomSheetDialog.setContentView(sheetAbsen);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.show();

            btn_maps_proses.setVisibility(View.GONE);

            txtKetLokasi.setText("Lat: " + latitude + ", Long: " + longitude);

            img_masuk.setImageDrawable(getResources().getDrawable(R.drawable.gallery));
            img_pulang.setImageDrawable(getResources().getDrawable(R.drawable.gallery));
            img_sigeoo.setImageDrawable(getResources().getDrawable(R.drawable.worklocation));

            bs_absen.setEnabled(false);
            bs_absen.setBackgroundColor(getResources().getColor(R.color.white));
            bs_absen.setTextColor(getResources().getColor(R.color.colorPrimary));

            lrAbsenMasuk.setOnClickListener(viewAbsen -> bukaKamera());
        });

        img_close.setOnClickListener(view -> showDialogClose());
    }

    @Override
    public void onKeyExited(String key) {
        checkConnection();

        sendNotification(firebaseUser.getDisplayName(), String.format("sudah keluar dari area kampus", key));

        bs_absen.setEnabled(false);
        bs_absen.setBackgroundColor(getResources().getColor(R.color.white));
        bs_absen.setTextColor(getResources().getColor(R.color.colorPrimary));

        Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        btn_maps_proses.startAnimation(fadeOut);
        btn_maps_proses.setVisibility(View.GONE);

        if (snackbar.isShown()) {
            snackbar.dismiss();
        }

        if (bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        checkConnection();

        double latitude = location.latitude;
        double longitude = location.longitude;

        txtKetLokasi.setText("Lat: " + latitude + ", Long: " + longitude);

        sendNotification(firebaseUser.getDisplayName(), String.format("sedang bergerak didalam area kampus", key));
    }

    @Override
    public void onGeoQueryReady() {
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.d(TAG, "Maps Activity: " + error.getMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Request Code: " + requestCode);

        switch (requestCode){
            case REQUEST_LOC:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Maps Activity: Lokasi diaktifkan");
                }

            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap varBitmap = (Bitmap) extras.get("data");

                    Log.d(TAG, "Value Bitmap: " + varBitmap.toString());

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
                    String timeStamp = formatter.format(new Date());

                    filesDir = getApplicationContext().getFilesDir();
                    imageFile = new File(filesDir, timeStamp + ".jpg");

                    try {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap scaleBitmap = Bitmap.createScaledBitmap(varBitmap, 1980, 2640, true);
                        scaleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), scaleBitmap, timeStamp, null);

                        mediaPath = Uri.parse(path);
                        Log.d(TAG, "Image URI: " + mediaPath);

                        img_masuk.setImageBitmap(varBitmap);

                        bs_absen.setEnabled(true);
                        bs_absen.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        bs_absen.setTextColor(getResources().getColor(R.color.white));

                        lrAbsenMasuk.setEnabled(false);

                        bs_absen.setOnClickListener(view -> absenMasuk());

                    } catch (Exception e) {
                        Log.d(TAG, "Foto exception: " + e.getMessage());
                    }
                }
        }
    }

    @Override
    public void onLoadLocationSuccess(List<MyLatLng> latLngs) {
        checkConnection();

        workArea = new ArrayList<>();
        for (MyLatLng myLatLng : latLngs) {
            LatLng convert = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
            workArea.add(convert);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapsActivity.this);

        if (mMap != null) {
            mMap.clear();

            // Tambah marker staf
            addStafMarker();

            // Tambah lokasi area kampus
            addCircleArea();
        }
    }

    @Override
    public void onLoadLocationFailed(String message) {
        Log.d(TAG, "Maps Lokasi: " + message);
    }
}