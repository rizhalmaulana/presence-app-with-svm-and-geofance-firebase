package ac.id.ubpkarawang.sigeoo.Screens.utama;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Screens.MapsActivity;
import ac.id.ubpkarawang.sigeoo.Screens.PeriksaActivity;

public class UtamaFragment extends Fragment {

    BottomSheetDialog bottomSheetDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_utama, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout lrMaps = view.findViewById(R.id.lr_lokasi);
        LinearLayout lrLogout = view.findViewById(R.id.lr_logout);
        LinearLayout lrKelola = view.findViewById(R.id.lr_keloladata);
        CardView cvPeriksa = view.findViewById(R.id.cv_periksa);

        lrMaps.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), MapsActivity.class)));
        lrLogout.setOnClickListener(view12 -> showDialog());
        cvPeriksa.setOnClickListener(view13 -> startActivity(new Intent(getActivity(), PeriksaActivity.class)));

        lrKelola.setOnClickListener(view14 -> {
            bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.ThemeOverlay_MaterialComponents_BottomSheetDialog);

            View sheetView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_keloladata, view.findViewById(R.id.bottom_sheet_kelola));

            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
        });
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
}