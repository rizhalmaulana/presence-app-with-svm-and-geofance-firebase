package ac.id.ubpkarawang.sigeoo.Screens.informasi;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Objects;

import ac.id.ubpkarawang.sigeoo.Adapters.BannerAdapter;
import ac.id.ubpkarawang.sigeoo.Adapters.BeritaAdapter;
import ac.id.ubpkarawang.sigeoo.Base.BaseFragment;
import ac.id.ubpkarawang.sigeoo.Model.Covid.DataIndonesia;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.Banner;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.BannerItem;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.Berita;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.BeritaResponse;
import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Utils.ApiBeritaClient;
import ac.id.ubpkarawang.sigeoo.Utils.ApiBeritaService;
import ac.id.ubpkarawang.sigeoo.Utils.ApiCovidClient;
import ac.id.ubpkarawang.sigeoo.Utils.ApiRetrofitClient;
import ac.id.ubpkarawang.sigeoo.Utils.ApiUtils;
import ac.id.ubpkarawang.sigeoo.Utils.MobileService;
import ac.id.ubpkarawang.sigeoo.Utils.UtlisCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class InformasiFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, UtlisCallback {

    private BeritaAdapter beritaAdapter;
    private BannerAdapter bannerAdapter;

    private Context context;
    private RecyclerView recycle_informasi, recycle_banner;

    private final static String API_KEY = "da1131b188264bd6b8842ffa7db53761";

    private ArrayList<Berita> listBerita = new ArrayList<>();
    private ArrayList<Banner> listBanner = new ArrayList<>();

    TextView sembuh, meninggal, positif, konfirmasi;
    SwipeRefreshLayout refresh_informasi;
    RelativeLayout view_load;
    CardView card_banner;
    MobileService mobileService;
    ScrollingPagerIndicator recycle_indicator;

    private static final String TAG = "InformasiFragment";

    public InformasiFragment(){
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_informasi, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        beritaAdapter = new BeritaAdapter(context, listBerita);
        bannerAdapter = new BannerAdapter(context, listBanner);

        view_load = view.findViewById(R.id.view_load);
        sembuh = view.findViewById(R.id.text_sembuh_informasi);
        meninggal = view.findViewById(R.id.text_meninggal_informasi);
        positif = view.findViewById(R.id.text_positif_informasi);
        konfirmasi = view.findViewById(R.id.text_konfirmasi_informasi);

        card_banner = view.findViewById(R.id.card_fill_banenr);
        recycle_indicator = view.findViewById(R.id.indicator_banner);

        mobileService = ApiUtils.MobileService(getContext());

        recycle_informasi = view.findViewById(R.id.recycle_informasi);
        recycle_banner = view.findViewById(R.id.recycle_banner);

        refresh_informasi = view.findViewById(R.id.layout_refresh_informasi);
        refresh_informasi.setOnRefreshListener(this);

        recycle_informasi.setHasFixedSize(true);
        recycle_banner.setHasFixedSize(true);

        recycle_informasi.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.LayoutManager layoutBanner = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recycle_banner.setLayoutManager(layoutBanner);

        loadInformasi();
    }

    private void loadInformasi() {

        view_load.setVisibility(View.VISIBLE);
        refresh_informasi.setVisibility(View.GONE);

        Call<DataIndonesia> dataCovidIndonesia = ApiCovidClient.service().getKasus();
        dataCovidIndonesia.enqueue(new Callback<DataIndonesia>() {
            @Override
            public void onResponse(@NonNull Call<DataIndonesia> call, @NonNull Response<DataIndonesia> response) {
                Log.d(TAG, "Status Covid: Berhasil diload");

                konfirmasi.setText(Objects.requireNonNull(response.body()).getCases());
                sembuh.setText(response.body().getRecovered());
                meninggal.setText(response.body().getDeaths());
                positif.setText(response.body().getActive());

                ApiBeritaService apiBeritaService = ApiBeritaClient.getBerita().create(ApiBeritaService.class);
                Call<BeritaResponse> callBerita = apiBeritaService.getBeritaTerkini(API_KEY);
                callBerita.enqueue(new Callback<BeritaResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BeritaResponse> callBeritaResponse, @NonNull Response<BeritaResponse> beritaResponse) {
                        assert beritaResponse.body() != null;

                        listBerita = beritaResponse.body().getResults();
                        recycle_informasi.setAdapter(new BeritaAdapter(getContext(), listBerita));

                        refresh_informasi.setVisibility(View.VISIBLE);
                        view_load.setVisibility(View.GONE);

                        //Load Banner API
                        loadBanner();
                    }

                    @Override
                    public void onFailure(@NonNull Call<BeritaResponse> callBeritaResponse, @NonNull Throwable throwableBerita) {
                        Log.d(TAG, "Status Berita: " + throwableBerita);
                        Toast.makeText(getContext(), "Koneksi kamu bermasalah, coba lagi.", Toast.LENGTH_SHORT).show();

                        view_load.setVisibility(View.GONE);
                        refresh_informasi.setVisibility(View.VISIBLE);

                    }
                });

            }

            @Override
            public void onFailure(@NonNull Call<DataIndonesia> call, @NonNull Throwable t) {
                Log.d(TAG, "Status Covid: " + t);

                konfirmasi.setText("-");
                sembuh.setText("-");
                meninggal.setText("-");
                positif.setText("-");

                view_load.setVisibility(View.GONE);
                refresh_informasi.setVisibility(View.VISIBLE);
            }
        });
    }


    private void loadBanner() {
        Call<BannerItem> dataBanner = ApiRetrofitClient.service().getbanner();
        dataBanner.enqueue(new Callback<BannerItem>() {
            @Override
            public void onResponse(@NonNull Call<BannerItem> call, @NonNull Response<BannerItem> response) {
                BannerItem body = response.body();
                if (response.isSuccessful()){
                    assert body != null;
                    if (!body.isState()){
                        initialize();
                        ArrayList<Banner> bannerList = new ArrayList<>();
                        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), bannerList);
                        recycle_banner.setAdapter(bannerAdapter);

                        recycle_banner.setVisibility(View.GONE);
                        card_banner.setVisibility(View.VISIBLE);

                    } else {
                        Log.d(TAG, "Status data banner: " + body.getData());
                        initialize();
                        for (int i = 0; i < body.getData().size(); i++){
                            final Banner itemBanner = new Banner();
                            itemBanner.setFoto_banner(body.getData().get(i).getFoto_banner());
                            listBanner.add(itemBanner);
                        }

                        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), listBanner);
                        recycle_banner.setAdapter(bannerAdapter);
                        recycle_indicator.attachToRecyclerView(recycle_banner);
                        recycle_banner.setVisibility(View.VISIBLE);
                        card_banner.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BannerItem> call, @NonNull Throwable t) {
                Log.d(TAG, "Status Banner: " + t);

                recycle_banner.setVisibility(View.GONE);
                card_banner.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), "Gagal load banner! Coba lagi", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initialize() {
        listBanner = new ArrayList<>();
        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), listBanner);
        recycle_banner.setAdapter(bannerAdapter);
        listBanner.clear();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            loadInformasi();
            loadBanner();
            refresh_informasi.setRefreshing(false);
        }, 3000);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadInformasi();
        loadBanner();
    }

    @Override
    public void onSuccess(BeritaResponse beritaResponse) {
        listBerita = beritaResponse.getResults();
        beritaAdapter.refill(listBerita);
    }

    @Override
    public void onFailed(String error) {
        Log.d(TAG, "Failed: " + error);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }
}