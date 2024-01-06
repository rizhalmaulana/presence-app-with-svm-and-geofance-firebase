package ac.id.ubpkarawang.sigeoo.Screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioAttributes;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import ac.id.ubpkarawang.sigeoo.BuildConfig;
import ac.id.ubpkarawang.sigeoo.Helpers.LoadingDialog;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.MyLatLng;
import ac.id.ubpkarawang.sigeoo.Model.Utama.AbsenMasukItem;
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

    private boolean isConnected = false;
    boolean isOnArea = false;

    private static final int REQUEST_CAMERA = 123;
    private static final int REQUEST_LOC = 1001;
    private static final String TAG = "MapsActivity";

    private FirebaseUser firebaseUser;
    double latitude, longitude;
    private Uri mediaPath;
    public String jamMasuk;
    public String keteranganAbsen;

    BottomSheetDialog bottomSheetDialog;
    ImageView img_masuk, img_pulang, img_sigeoo, img_close, img_back;
    MaterialButton bs_absen, btn_maps_proses;
    LinearLayout lrAbsenMasuk, lrAbsenPulang;
    RelativeLayout rlLoadSuccess;
    View sheetAbsen;
    TextView txtKetLokasi, txtKetNama, txtKetAkurasi, txtStatus;
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
        img_back = findViewById(R.id.back_maps);
        bottomSheetDialog = new BottomSheetDialog(MapsActivity.this, R.style.BottomSheetDialogTheme);
        loadingDialog = new LoadingDialog(MapsActivity.this);
        rlLoadSuccess = findViewById(R.id.view_success);

        sheetAbsen = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_absen, findViewById(R.id.bottom_sheet_masuk));
        img_masuk = sheetAbsen.findViewById(R.id.img_sheet_absen_masuk);
        img_pulang = sheetAbsen.findViewById(R.id.img_sheet_absen_pulang);
        img_sigeoo = sheetAbsen.findViewById(R.id.img_sheet_sigeoo);
        img_close = sheetAbsen.findViewById(R.id.img_sheet_close);
        bs_absen = sheetAbsen.findViewById(R.id.btn_sheet_absen);
        lrAbsenMasuk = sheetAbsen.findViewById(R.id.lr_sheet_absen_masuk);
        lrAbsenPulang = sheetAbsen.findViewById(R.id.lr_sheet_absen_pulang);
        txtKetLokasi = sheetAbsen.findViewById(R.id.text_sheet_ket);
        txtKetNama = sheetAbsen.findViewById(R.id.text_sheet_nama);
        txtKetAkurasi = sheetAbsen.findViewById(R.id.text_sheet_akurasi);
        txtStatus = sheetAbsen.findViewById(R.id.text_sheet_status);

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

        Log.d(TAG, "Fake GPS: " + isMockLocationActive());

        img_back.setOnClickListener(v -> alertDialogCancel());
    }

    // <Info> Setup GPS, Geofance, Staf Marker, Check Connection </Info>
    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean checkConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (checkConnected) {
            isConnected = true;
        } else {
            isConnected = false;
        }

        return isConnected;
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

    private void addCircleArea() {
        if (geoQuery != null) {
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
    // <Info> Setup GPS, Geofance, Staf Marker, Check Connection </Info>

    private void openSnackBar(String msg, String act) {
        snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
        snackbar.setAction(act, view -> {
            if (act.equalsIgnoreCase("Lanjutkan")) {
                isOnArea = true;
                btn_maps_proses.setVisibility(View.VISIBLE);
            } else if (act.equalsIgnoreCase("Oke")) {
                isOnArea = true;
                btn_maps_proses.setVisibility(View.VISIBLE);
            } else if (act.equalsIgnoreCase("Tutup")) {
                isOnArea = false;
                btn_maps_proses.setVisibility(View.GONE);
            }
        }).show();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void verifikasiWajah(double lat, double longitude, boolean isOnArea) {
        if (isOnArea) {
            if (lat != 0 && longitude != 0) {
                btn_maps_proses.setOnClickListener(viewProses -> {
                    bottomSheetDialog.setContentView(sheetAbsen);
                    bottomSheetDialog.setCanceledOnTouchOutside(false);
                    bottomSheetDialog.setCancelable(false);
                    bottomSheetDialog.show();

                    txtKetLokasi.setText("Lat: " + lat + ", Long: " + longitude);

                    img_masuk.setImageDrawable(getResources().getDrawable(R.drawable.gallery));
                    img_pulang.setImageDrawable(getResources().getDrawable(R.drawable.gallery));
                    img_sigeoo.setImageDrawable(getResources().getDrawable(R.drawable.worklocation));

                    bs_absen.setEnabled(false);
                    bs_absen.setBackgroundColor(getResources().getColor(R.color.white));
                    bs_absen.setTextColor(getResources().getColor(R.color.colorPrimary));

                    img_close.setOnClickListener(view -> alertDialogCancel());
                    lrAbsenMasuk.setOnClickListener(viewAbsen -> bukaKamera("Masuk"));
                });
            }
        }
    }

    private void bukaKamera(String paramStatus) {
        keteranganAbsen = paramStatus;
        Intent pictureAbsenMasuk = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureAbsenMasuk.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureAbsenMasuk, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Request Code: " + requestCode);

        switch (requestCode) {
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

                        //Verifikasi absen masuk
                        deteksiSVMPython();

                        bs_absen.setOnClickListener(view -> {
                            if (isMockLocationActive()) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                                alertDialogBuilder
                                        .setTitle("Peringatan")
                                        .setMessage("Kamu menggunakan lokasi palsu! Silahkan matikan di pengaturan.");
                                alertDialogBuilder
                                        .setIcon(R.drawable.warning)
                                        .setCancelable(false)
                                        .setPositiveButton("Oke", (dialogInterface, i) -> {
                                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }

                            alertDialogQuestion("Konfirmasi", "Apakah data yang tercantum sudah sesuai?");
                        });

                    } catch (Exception e) {
                        Log.d(TAG, "Foto exception: " + e.getMessage());
                    }
                }
        }
    }

    private boolean isMockLocationActive() {
        boolean isMockLocation;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
                isMockLocation = (Objects.requireNonNull(opsManager).checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED);
            } else {
                isMockLocation = !android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "mock_location").equals("0");

            }
            Log.d(TAG, "mocklocation: " + isMockLocation);

        } catch (Exception e) {
            return false;
        }
        return isMockLocation;
    }

    // Request Deteksi Wajah (SVM) ke BE python
    private void deteksiSVMPython() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            jamMasuk = null;
        } else {
            jamMasuk = extras.getString("jam_masuk");
        }

        Log.d(TAG, "Data Jam Masuk: " + jamMasuk);

        Date time = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        String formatTime = currentTime.format(time);

        try {
            Date time1 = currentTime.parse(formatTime);
            Date time2 = currentTime.parse(jamMasuk);

            long elapsed = time1.getTime() - time2.getTime();
            int hours = (int) ((elapsed / (1000 * 60 * 60)) % 24);

            Log.d(TAG, String.valueOf(elapsed));
            Log.d(TAG, "Perbandingan: " + hours);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        File file = FileUtils.getFile(this, mediaPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse(String.valueOf(mediaPath)), file);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("data_gambar", file.getName(), requestBody);

        Log.d(TAG, "Part Image: " + partImage);

        loadingDialog.startLoading();
        mobileServiceSvm.postImage(partImage).enqueue(new Callback<SvmItem>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<SvmItem> call, @NonNull Response<SvmItem> response) {
                if (response.isSuccessful()) {
                    SvmItem body = response.body();
                    if (body.isState()) {
                        Log.d(TAG, "Proses SVM: Gambar terkirim");
                        if (body.getData() != null) {
                            String prediksiNama = body.getData().get(0).getName();
                            String prediksiAccuracy = body.getData().get(0).getAccuracy();

                            double percentPredict = Double.parseDouble(prediksiAccuracy);
                            NumberFormat format = NumberFormat.getPercentInstance(Locale.US);
                            String percentPrediksi = format.format(percentPredict);

                            txtKetNama.setText(prediksiNama);
                            txtKetAkurasi.setText(percentPrediksi);

                            if (formatTime.compareTo(jamMasuk) > 0) {
                                txtStatus.setTextColor(getResources().getColor(R.color.red_pink));
                                txtStatus.setText("Telat");
                            } else if (formatTime.compareTo(jamMasuk) < 0) {
                                txtStatus.setTextColor(getResources().getColor(R.color.teal_200));
                                txtStatus.setText("Tidak Telat");
                            } else if (formatTime.compareTo(jamMasuk) == 0) {
                                txtStatus.setTextColor(getResources().getColor(R.color.teal_200));
                                txtStatus.setText("Tepat Waktu");
                            }
                        }
                    } else {
                        Log.d(TAG, "Proses SVM: state false");
                    }
                } else {
                    Log.d(TAG, "Response: " + response.message());
                    Log.d(TAG, "Proses SVM: tidak sukses");
                }

                loadingDialog.dismissLoading();
            }

            @Override
            public void onFailure(Call<SvmItem> call, Throwable t) {
                Log.d(TAG, "Proses SVM: gagal response");
                loadingDialog.dismissLoading();
            }
        });
    }

    // Post Data Absensi Staf to DB Local
    private void absenStaf() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

        double latitudeUser = lastLocation.getLatitude();
        double longitudeUser = lastLocation.getLongitude();

        String currentDate = date.format(new Date());
        String currentTime = time.format(new Date());

        String ketAkurasi = txtKetAkurasi.getText().toString().trim();
        String statusAbsen = txtStatus.getText().toString().trim();

        if (keteranganAbsen == null) {
            Log.d(TAG, "Status Absen: Null");
        }

        Map<String, String> map = new HashMap<>();
        map.put("id_staf", firebaseUser.getUid());
        map.put("tgl_absen", currentDate);
        map.put("jam_absen", currentTime);
        map.put("lat_absen", String.valueOf(latitudeUser));
        map.put("long_absen", String.valueOf(longitudeUser));
        map.put("akurasi_absen", ketAkurasi);
        map.put("keterangan_absen", keteranganAbsen);
        map.put("status_absen", statusAbsen);

        Log.d(TAG, "Mapping: " + map);

        loadingDialog.startLoading();

        if (!checkConnection()) {
            // Simpan data ke local storage
        } else {
            mobileService.postAbsenMasuk(map).enqueue(new Callback<AbsenMasukItem>() {
                @Override
                public void onResponse(Call<AbsenMasukItem> call, Response<AbsenMasukItem> response) {
                    if (response.isSuccessful()) {
                        AbsenMasukItem body = response.body();
                        if (body.isState()) {
                            Log.d(TAG, "Proses Absen: data terkirim");
                            if (body.getData() != null) {
                                Log.d(TAG, "Proses Absen: " + body.getData());
                                sendNotification("Notifikasi Absensi", "Horeey proses absensi berhasil. Terima kasih ^_^");

                                loadingDialog.dismissLoading();
                                Toast.makeText(getApplicationContext(), body.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            loadingDialog.dismissLoading();
                            Toast.makeText(getApplicationContext(), body.getMessage(), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MapsActivity.this, MainActivity.class));
                            finish();

                        } else {
                            Log.d(TAG, "Proses Absen: state false");
                            loadingDialog.dismissLoading();
                        }
                    } else {
                        Log.d(TAG, "Response: " + response.message());
                        Log.d(TAG, "Proses Absen: tidak sukses");
                    }

                    loadingDialog.dismissLoading();
                }

                @Override
                public void onFailure(Call<AbsenMasukItem> call, Throwable t) {
                    Log.d(TAG, "Proses Absen: gagal response");
                    loadingDialog.dismissLoading();
                }
            });
        }
    }

    private void sendNotification(String title, String content) {
        String NOTIFICATION_CHANNEL_ID = "sigeoo_multiple_location";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notifikasi",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Deskripsi Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.notification),
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build());
            notificationChannel.setShowBadge(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(this, MapsActivity.class);

        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setVibrate(new long[]{0, 100})
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.info)
                .setStyle(inboxStyle)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(new Random().nextInt(), notification);
    }

    private void alertDialogQuestion(String status, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle(status)
                .setMessage(message);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Lanjut", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    absenStaf();
                })
                .setNegativeButton("Kembali", (dialog, which) -> dialog.cancel());


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertDialogCancel() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Informasi")
                .setMessage("Apa anda yakin ingin membatalkan proses absen?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yakin", (dialogInterface, i) -> {
                    bottomSheetDialog.dismiss();
                    isOnArea = false;

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("Kembali", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // <Info> Implement Method OnMapReadyCallback, GeoQueryEventListener, LoadLocationListener </Info>
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
    public void onKeyEntered(String key, GeoLocation location) {
        checkConnection();

        latitude = location.latitude;
        longitude = location.longitude;

        String msg = "Kamu sudah di dalam area kampus";
        String act = "Lanjutkan";

        // Buka SnackBar
        openSnackBar(msg, act);
        verifikasiWajah(latitude, longitude, isOnArea);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        checkConnection();

        latitude = location.latitude;
        longitude = location.longitude;

        String msg = "Kamu sedang bergerak di area kampus";
        String act = "Oke";

        // Buka SnackBar
        openSnackBar(msg, act);
        verifikasiWajah(latitude, longitude, isOnArea);
    }

    @Override
    public void onKeyExited(String key) {
        checkConnection();

        String msg = "Kamu sedang di luar area kampus";
        String act = "Tutup";

        // Buka SnackBar
        openSnackBar(msg, act);
    }

    @Override
    public void onGeoQueryReady() {
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.d(TAG, "Maps Activity: " + error.getMessage());
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
    // <Info> Implement Method OnMapReadyCallback, GeoQueryEventListener, LoadLocationListener </Info>

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop Running");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkConnection();
        if (fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }
}