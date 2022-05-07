package ac.id.ubpkarawang.sigeoo.Screens.utama;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Screens.MapsActivity;
import ac.id.ubpkarawang.sigeoo.Screens.PeriksaActivity;
import ac.id.ubpkarawang.sigeoo.Utils.ConstantKey;

public class UtamaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LocationListener {

    BottomSheetDialog bottomSheetDialog;
    ImageView img_masuk;
    MaterialButton bs_absen_masuk;
    LinearLayout lrAbsenMasuk, lrLokasi, lrLogout, lrMasuk, lrViewMenuUtama;
    RelativeLayout rlViewLoadUtama, rlViewTopbar;
    SwipeRefreshLayout refresh_utama;
    CardView cvPeriksa;
    TextView txtLokasi;

    Geocoder geocoder;

    private static final String TAG = "UtamaFragment";
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CHECK_SETTING = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_utama, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lrLokasi = view.findViewById(R.id.lr_lokasi);
        lrLogout = view.findViewById(R.id.lr_logout);
        lrMasuk = view.findViewById(R.id.lr_masuk);
        cvPeriksa = view.findViewById(R.id.cv_periksa);
        txtLokasi = view.findViewById(R.id.txt_lokasi_sekarang);

        rlViewLoadUtama = view.findViewById(R.id.view_load_utama);
        rlViewTopbar = view.findViewById(R.id.rl_view_topbar);
        lrViewMenuUtama = view.findViewById(R.id.view_menu_utama);

        refresh_utama = view.findViewById(R.id.layout_refresh_utama);
        refresh_utama.setOnRefreshListener(this);

        lrLokasi.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), MapsActivity.class)));
        lrLogout.setOnClickListener(view2 -> showDialog());
        cvPeriksa.setOnClickListener(view3 -> startActivity(new Intent(getActivity(), PeriksaActivity.class)));

        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.ThemeOverlay_MaterialComponents_BottomSheetDialog);
        View sheetMasuk = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_absen, view.findViewById(R.id.bottom_sheet_masuk));

        img_masuk = sheetMasuk.findViewById(R.id.img_absen_masuk);
        bs_absen_masuk = sheetMasuk.findViewById(R.id.btn_bs_absen_masuk);
        lrAbsenMasuk = sheetMasuk.findViewById(R.id.lr_bs_absen_masuk);

        lrMasuk.setOnClickListener(view4 -> {
            bottomSheetDialog.setContentView(sheetMasuk);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.show();

            img_masuk.setImageDrawable(getResources().getDrawable(R.drawable.gallery));

            bs_absen_masuk.setEnabled(false);
            bs_absen_masuk.setBackgroundColor(getResources().getColor(R.color.white));
            bs_absen_masuk.setTextColor(getResources().getColor(R.color.colorPrimary));

            lrAbsenMasuk.setOnClickListener(view5 -> {
                Intent pictureAbsenMasuk = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pictureAbsenMasuk, ConstantKey.CAMERA_REQUEST_CODE);
            });
        });
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager;
        boolean isEnabled;

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isEnabled;
    }

    private void getCurrentLocation() {
        Log.d(TAG, "Lokasi: Get Current Lokasi");

        rlViewLoadUtama.setVisibility(View.VISIBLE);
        lrViewMenuUtama.setVisibility(View.GONE);
        rlViewTopbar.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {

                    Log.d(TAG, "Lokasi: GPS " + isGPSEnabled());

                    locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setInterval(5000);
                    locationRequest.setFastestInterval(2000);

                    LocationServices.getFusedLocationProviderClient(requireContext())
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    if (locationResult.getLocations().size() > 0) {
                                        Log.d(TAG, "Lokasi: Size " + locationResult.getLocations().size());

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        geocoder = new Geocoder(getContext(), Locale.getDefault());

                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                            txtLokasi.setText(addresses.get(0).getAddressLine(0));

                                            rlViewLoadUtama.setVisibility(View.GONE);
                                            lrViewMenuUtama.setVisibility(View.VISIBLE);
                                            rlViewTopbar.setVisibility(View.VISIBLE);

                                        } catch (IOException exception) {
                                            Log.d(TAG, "Lokasi: Exception " + exception.getLocalizedMessage());

                                            rlViewLoadUtama.setVisibility(View.GONE);
                                            lrViewMenuUtama.setVisibility(View.VISIBLE);
                                            rlViewTopbar.setVisibility(View.VISIBLE);

                                            exception.printStackTrace();
                                        }
                                    }
                                }
                            }, Looper.getMainLooper());
                } else {
                    Log.d(TAG, "Lokasi: GPS Not Active");
                    turnOnGPS();
                }

            } else {
                Log.d(TAG, "Lokasi: Izin ditolak");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_SETTING);
            }
        }
    }

    private void turnOnGPS() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        Log.d(TAG, "Lokasi: GPS Turn On");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(requireContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Toast.makeText(getContext(), "GPS berhasil diaktifkan.", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {
                Log.d(TAG, "Lokasi: Exception " + e.getStatusCode());

                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTING);

                        } catch (IntentSender.SendIntentException sendIntentException) {
                            Log.d(TAG, "Lokasi: Exception " + sendIntentException.getMessage());
                        }

                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Lokasi: Device Not Have Location");
                        break;
                }
            }
        });

    }

    private void buildLocationManager() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setSmallestDisplacement(20f);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                if (mMap != null) {
                    lastLocation = locationResult.getLastLocation();
                }
            }
        };
    }

    private void absenMasuk() {
        Toast.makeText(requireActivity(), "Gambar Terkirim..", Toast.LENGTH_SHORT).show();
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder
                .setTitle("Informasi")
                .setMessage("Apa anda yakin ingin keluar?");
        alertDialogBuilder
                .setIcon(R.drawable.error)
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, which) -> getActivity().finish())
                .setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTING) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(requireContext(), "Lokasi berhasil diaktifkan", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(requireContext(), "Lokasi diperlukan, silahkan aktifkan!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == ConstantKey.CAMERA_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Bitmap photoAbsen = (Bitmap) data.getExtras().get("data");
                    img_masuk.setImageBitmap(photoAbsen);

                    bs_absen_masuk.setEnabled(true);
                    bs_absen_masuk.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    bs_absen_masuk.setTextColor(getResources().getColor(R.color.white));

                    bs_absen_masuk.setOnClickListener(view -> absenMasuk());

                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(requireActivity(), "Izin Kamera diperlukan, silahkan izinkan!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(() -> {
            if (isGPSEnabled()) {
                getCurrentLocation();
            } else {
                turnOnGPS();
            }
            refresh_utama.setRefreshing(false);
        }, 3000);
    }

    @Override
    public void onStop() {
        buildLocationManager();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            if (isGPSEnabled()) {
                getCurrentLocation();
            } else {
                turnOnGPS();
            }
            refresh_utama.setRefreshing(false);
        }, 3000);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            String address = addresses.get(0).getAddressLine(0);
            txtLokasi.setText(address);

            rlViewLoadUtama.setVisibility(View.GONE);
            lrViewMenuUtama.setVisibility(View.VISIBLE);
            rlViewTopbar.setVisibility(View.VISIBLE);

        } catch (Exception ex) {
            Log.d(TAG, "Lokasi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }
}