package ac.id.ubpkarawang.sigeoo.Utils;

import ac.id.ubpkarawang.sigeoo.Model.Covid.DataIndonesia;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.BeritaResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInformasiService {
    @GET("v2/top-headlines?country=id&category=technology")
    Call<BeritaResponse> getBeritaTerkini(@Query("apiKey")String apiKey);

    @GET("indonesia")
    Call<DataIndonesia> getKasus();
}