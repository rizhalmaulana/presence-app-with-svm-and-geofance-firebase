package ac.id.ubpkarawang.sigeoo.Screens.informasi;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Locale;

import ac.id.ubpkarawang.sigeoo.Adapters.BannerAdapter;
import ac.id.ubpkarawang.sigeoo.Adapters.BeritaAdapter;
import ac.id.ubpkarawang.sigeoo.Model.Covid.DataIndonesia;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.Banner;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.BannerItem;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.Berita;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.BeritaResponse;
import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Utils.ApiInformasiService;
import ac.id.ubpkarawang.sigeoo.Utils.ApiUtils;
import ac.id.ubpkarawang.sigeoo.Utils.MobileService;
import ac.id.ubpkarawang.sigeoo.Utils.RetrofitClient;
import ac.id.ubpkarawang.sigeoo.Utils.Static;
import ac.id.ubpkarawang.sigeoo.Utils.UtlisCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class InformasiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, UtlisCallback {

    private BeritaAdapter beritaAdapter;
    private BannerAdapter bannerAdapter;

    private Context context;
    private RecyclerView recycle_informasi, recycle_banner;

    private ArrayList<Berita> listBerita = new ArrayList<>();
    private ArrayList<Banner> listBanner = new ArrayList<>();

    TextView sembuh, meninggal, positif, konfirmasi;
    SwipeRefreshLayout refresh_informasi;
    RelativeLayout view_load;
    LinearLayout lrInfoCovid;
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

        lrInfoCovid = view.findViewById(R.id.lr_info_covid);
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

        Call<DataIndonesia> dataCovidIndonesia = RetrofitClient.serviceCovid().getKasus();
        dataCovidIndonesia.enqueue(new Callback<DataIndonesia>() {
            @Override
            public void onResponse(@NonNull Call<DataIndonesia> call, @NonNull Response<DataIndonesia> response) {
                Log.d(TAG, "Status Covid: Berhasil diload");
                Log.d(TAG, "Response Covid: " + response.body());

                DataIndonesia body = response.body();

                if (body != null) {
                    int responseKonfirmasi = Integer.parseInt(response.body().getCases());
                    int responseSembuh = Integer.parseInt(response.body().getRecovered());
                    int responseMeninggal = Integer.parseInt(response.body().getDeaths());
                    int responsePositif = Integer.parseInt(response.body().getActive());

                    String formatKonfirmasi = String.format(Locale.US, "%,d", responseKonfirmasi).replace(',', '.');
                    String formatSembuh = String.format(Locale.US, "%,d", responseSembuh).replace(',', '.');
                    String formatMeninggal = String.format(Locale.US, "%,d", responseMeninggal).replace(',', '.');
                    String formatPositif = String.format(Locale.US, "%,d", responsePositif).replace(',', '.');

                    ApiInformasiService apiInformasiService = RetrofitClient.serviceBerita().create(ApiInformasiService.class);
                    Call<BeritaResponse> callBerita = apiInformasiService.getBeritaTerkini(Static.API_KEY_BERITA_TERKINI);
                    callBerita.enqueue(new Callback<BeritaResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<BeritaResponse> callBeritaResponse, @NonNull Response<BeritaResponse> beritaResponse) {
                            assert beritaResponse.body() != null;
                            listBerita = beritaResponse.body().getResults();
                            recycle_informasi.setAdapter(new BeritaAdapter(getContext(), listBerita));
                            Log.d(TAG, "Status data berita: " + beritaResponse.body().getResults());

                            //Load Banner API
                            loadBanner();

                            konfirmasi.setText(formatKonfirmasi);
                            sembuh.setText(formatSembuh);
                            meninggal.setText(formatMeninggal);
                            positif.setText(formatPositif);
                        }

                        @Override
                        public void onFailure(@NonNull Call<BeritaResponse> callBeritaResponse, @NonNull Throwable throwableBerita) {
                            Log.d(TAG, "Status Berita: " + throwableBerita);
                            Toast.makeText(getContext(), "Koneksi kamu bermasalah, coba lagi.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    konfirmasi.setText("-");
                    sembuh.setText("-");
                    meninggal.setText("-");
                    positif.setText("-");

                    lrInfoCovid.setVisibility(View.GONE);
                }
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
        mobileService.getbanner().enqueue(new Callback<BannerItem>() {
            @Override
            public void onResponse(Call<BannerItem> call, Response<BannerItem> response) {
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

                        if (body.getData() == null) {
                            recycle_banner.setVisibility(View.GONE);
                            card_banner.setVisibility(View.VISIBLE);

                            refresh_informasi.setVisibility(View.VISIBLE);
                            view_load.setVisibility(View.GONE);
                        }

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

                    refresh_informasi.setVisibility(View.VISIBLE);
                    view_load.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<BannerItem> call, Throwable t) {
                Log.d(TAG, "Status Banner: " + t);

                recycle_banner.setVisibility(View.GONE);
                card_banner.setVisibility(View.VISIBLE);

                refresh_informasi.setVisibility(View.VISIBLE);
                view_load.setVisibility(View.GONE);
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
        Log.d(TAG, "onRefresh Running");
        view_load.setVisibility(View.VISIBLE);

        loadInformasi();
        refresh_informasi.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume Running");

        loadInformasi();
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