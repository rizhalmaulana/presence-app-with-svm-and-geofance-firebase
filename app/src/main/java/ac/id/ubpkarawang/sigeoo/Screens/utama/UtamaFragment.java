package ac.id.ubpkarawang.sigeoo.Screens.utama;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Screens.MapsActivity;

public class UtamaFragment extends Fragment {

    private LinearLayout lrMaps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_utama, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lrMaps = view.findViewById(R.id.lrLokasi);

        lrMaps.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), MapsActivity.class));
        });
    }
}