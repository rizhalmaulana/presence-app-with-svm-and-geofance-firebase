package ac.id.ubpkarawang.sigeoo.Utils;

import java.util.Map;

import ac.id.ubpkarawang.sigeoo.Model.Informasi.BannerItem;
import ac.id.ubpkarawang.sigeoo.Model.Informasi.JamKerjaItem;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MobileService {
    @FormUrlEncoded
    @POST("jam_kerja")
    Call<JamKerjaItem> postJam(@FieldMap Map<String, String> map);

    @GET("banner")
    Call<BannerItem> getbanner();

//    @FormUrlEncoded
//    @POST("login")
//    Call<Response> userlogin(@FieldMap Map<String, String> map);
}
