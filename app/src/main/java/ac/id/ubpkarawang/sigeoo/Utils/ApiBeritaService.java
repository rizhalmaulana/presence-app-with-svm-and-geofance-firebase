package ac.id.ubpkarawang.sigeoo.Utils;

import ac.id.ubpkarawang.sigeoo.Model.Informasi.BeritaResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiBeritaService {
    @GET("v2/top-headlines?country=id")
    Call<BeritaResponse> getBeritaTerkini(@Query("apiKey")String apiKey);
}