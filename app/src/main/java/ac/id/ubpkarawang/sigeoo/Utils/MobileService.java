package ac.id.ubpkarawang.sigeoo.Utils;

import java.util.Map;

import ac.id.ubpkarawang.sigeoo.Model.Informasi.BannerItem;
import ac.id.ubpkarawang.sigeoo.Model.Utama.JamKerjaItem;
import ac.id.ubpkarawang.sigeoo.Model.Utama.SvmItem;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface MobileService {
    @FormUrlEncoded
    @POST("jam_kerja")
    Call<JamKerjaItem> postJam(@FieldMap Map<String, String> map);

    @GET("banner")
    Call<BannerItem> getbanner();

    @Multipart
    @POST("svm/")
    Call<SvmItem> postImage(@Part MultipartBody.Part file);
}
