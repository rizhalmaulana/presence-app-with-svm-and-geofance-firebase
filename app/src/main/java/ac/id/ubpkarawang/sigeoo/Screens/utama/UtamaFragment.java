package ac.id.ubpkarawang.sigeoo.Screens.utama;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Screens.MapsActivity;
import ac.id.ubpkarawang.sigeoo.Screens.PeriksaActivity;
import ac.id.ubpkarawang.sigeoo.Utils.ConstantKey;

public class UtamaFragment extends Fragment implements LocationListener {

    private static final String TAG = "UtamaFragment";

    BottomSheetDialog bottomSheetDialog;
    ImageView img_masuk;
    CardView cvPeriksa;
    MaterialButton bs_absen_masuk;
    LinearLayout lrMaps, lrKeluar, lrMasuk, lrAbsenMasuk;
    TextView lokasi_sekarang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_utama, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lrMaps = view.findViewById(R.id.lr_lokasi);
        lrKeluar = view.findViewById(R.id.lr_logout);
        lrMasuk = view.findViewById(R.id.lr_masuk);
        cvPeriksa = view.findViewById(R.id.cv_periksa);
        lokasi_sekarang = view.findViewById(R.id.txt_lokasi_sekarang);

        lrMaps.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), MapsActivity.class)));
        lrKeluar.setOnClickListener(view2 -> showDialog());
        cvPeriksa.setOnClickListener(view3 -> startActivity(new Intent(getActivity(), PeriksaActivity.class)));

        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.ThemeOverlay_MaterialComponents_BottomSheetDialog);
        View sheetMasuk = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_absen, view.findViewById(R.id.bottom_sheet_masuk));

        img_masuk = sheetMasuk.findViewById(R.id.img_absen_masuk);
        bs_absen_masuk = sheetMasuk.findViewById(R.id.btn_bs_absen_masuk);
        lrAbsenMasuk = sheetMasuk.findViewById(R.id.lr_bs_absen_masuk);

        lrMasuk.setOnClickListener(view4 -> {
            bottomSheetDialog.setContentView(sheetMasuk);
            bottomSheetDialog.setCancelable(false);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ConstantKey.CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission Granted");
            } else {
                Log.d(TAG, "Permission Denied");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantKey.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap photoAbsen = (Bitmap) data.getExtras().get("data");
            img_masuk.setImageBitmap(photoAbsen);

            bs_absen_masuk.setEnabled(true);
            bs_absen_masuk.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            bs_absen_masuk.setTextColor(getResources().getColor(R.color.white));

            bs_absen_masuk.setOnClickListener(view -> absenMasuk());
        }
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
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(requireActivity(), "LatLong: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        try {
            Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            String address = addresses.get(0).getAddressLine(0);
            lokasi_sekarang.setText(address);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

}