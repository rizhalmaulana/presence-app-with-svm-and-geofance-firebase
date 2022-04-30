package ac.id.ubpkarawang.sigeoo.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRetrofitClient {
    public static String API_KEY = Static.BASE_URL;
    public static MobileService service() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_KEY)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MobileService.class);
    }
}
