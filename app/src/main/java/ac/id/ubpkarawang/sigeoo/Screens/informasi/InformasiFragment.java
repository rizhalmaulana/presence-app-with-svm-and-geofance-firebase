package ac.id.ubpkarawang.sigeoo.Screens.informasi;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

import ac.id.ubpkarawang.sigeoo.Model.Covid.DataIndonesia;
import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Utils.ApiCovid;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformasiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    LinearLayout wrapper_top, wrapper_bottom;
    TextView sembuh, meninggal, positif, konfirmasi;
    LottieAnimationView progress_covid, progress_informasi;
    RecyclerView recycle_informasi;
    SwipeRefreshLayout refresh_informasi;

    private static final String TAG = "InformasiFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_informasi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wrapper_top = view.findViewById(R.id.layout_wrapper_top);
        wrapper_bottom = view.findViewById(R.id.layout_wrapper_bottom);

        sembuh = view.findViewById(R.id.text_sembuh_informasi);
        meninggal = view.findViewById(R.id.text_meninggal_informasi);
        positif = view.findViewById(R.id.text_positif_informasi);
        konfirmasi = view.findViewById(R.id.text_konfirmasi_informasi);

        recycle_informasi = view.findViewById(R.id.recycle_informasi);

        progress_informasi = view.findViewById(R.id.progress_informasi);
        progress_covid = view.findViewById(R.id.progress_covid_informasi);
        refresh_informasi = view.findViewById(R.id.layout_refresh_kategori);

        refresh_informasi.setOnRefreshListener(this);
        loadCovid();
    }

    private void loadCovid() {
        progress_covid.setVisibility(View.VISIBLE);
        wrapper_bottom.setVisibility(View.GONE);
        wrapper_top.setVisibility(View.GONE);

        Call<DataIndonesia> dataCovidIndonesia = ApiCovid.service().getKasus();
        dataCovidIndonesia.enqueue(new Callback<DataIndonesia>() {
            @Override
            public void onResponse(Call<DataIndonesia> call, Response<DataIndonesia> response) {
                Log.d(TAG, "Status Covid: Berhasil diload");

//                progress_covid.setVisibility(View.GONE);
                wrapper_bottom.setVisibility(View.VISIBLE);
                wrapper_top.setVisibility(View.VISIBLE);

                assert response.body() != null;
                konfirmasi.setText(response.body().getCases());
                sembuh.setText(response.body().getRecovered());
                meninggal.setText(response.body().getDeaths());
                positif.setText(response.body().getActive());
            }

            @Override
            public void onFailure(Call<DataIndonesia> call, Throwable t) {
                Log.d(TAG, "Status Covid: " + t);

//                progress_covid.setVisibility(View.GONE);
                wrapper_bottom.setVisibility(View.VISIBLE);
                wrapper_top.setVisibility(View.VISIBLE);

                konfirmasi.setText("-");
                sembuh.setText("-");
                meninggal.setText("-");
                positif.setText("-");
            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            loadCovid();
            refresh_informasi.setRefreshing(false);
        }, 2000);
    }
}