package ac.id.ubpkarawang.sigeoo.Utils;

import ac.id.ubpkarawang.sigeoo.Model.Informasi.BannerItem;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MobileService {
//    @GET("user")
//    Call<Response> getuser();

    @GET("banner")
    Call<BannerItem> getbanner();

//    @FormUrlEncoded
//    @POST("login")
//    Call<Response> userlogin(@FieldMap Map<String, String> map);
}
