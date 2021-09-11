package ac.id.ubpkarawang.sigeoo.Screens.informasi;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.util.List;

import ac.id.ubpkarawang.sigeoo.Model.DataIndonesia;
import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Utils.ApiCovid;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformasiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    LinearLayout layout_wrapper_kasus;
    TextView sembuh, meninggal, positif;
    CircularProgressIndicator progress_covid, progress_informasi;
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
        layout_wrapper_kasus = view.findViewById(R.id.layout_wrapper_kasus);
        sembuh = view.findViewById(R.id.text_sembuh_informasi);
        meninggal = view.findViewById(R.id.text_meninggal_informasi);
        positif = view.findViewById(R.id.text_positif_informasi);
        recycle_informasi = view.findViewById(R.id.recycle_informasi);

        progress_informasi = view.findViewById(R.id.progress_informasi);
        progress_covid = view.findViewById(R.id.progress_covid_informasi);
        refresh_informasi = view.findViewById(R.id.layout_refresh_kategori);

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
//        recycle_informasi.setLayoutManager(layoutManager);

        refresh_informasi.setOnRefreshListener(this);

        loadCovid();
    }

    private void loadCovid() {
        progress_covid.setVisibility(View.VISIBLE);
        layout_wrapper_kasus.setVisibility(View.GONE);

        Call<List<DataIndonesia>> dataCovidIndonesia = ApiCovid.service().getKasus();
        dataCovidIndonesia.enqueue(new Callback<List<DataIndonesia>>() {
            @Override
            public void onResponse(Call<List<DataIndonesia>> call, Response<List<DataIndonesia>> response) {
                progress_covid.setVisibility(View.GONE);
                layout_wrapper_kasus.setVisibility(View.VISIBLE);

                sembuh.setText(response.body().get(0).getSembuh());
                meninggal.setText(response.body().get(0).getMeninggal());
                positif.setText(response.body().get(0).getPositif());
            }

            @Override
            public void onFailure(Call<List<DataIndonesia>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                progress_covid.setVisibility(View.GONE);
                layout_wrapper_kasus.setVisibility(View.VISIBLE);

                sembuh.setText("-");
                meninggal.setText("-");
                positif.setText("-");

                Toast.makeText(getContext(), "Gagal load data kasus! Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
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