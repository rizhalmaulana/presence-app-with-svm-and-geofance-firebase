package ac.id.ubpkarawang.sigeoo.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCovidClient {
    public static String API_KEY = "https://disease.sh/v3/covid-19/countries/";
    public static ApiCovidService service(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_KEY)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiCovidService.class);
    }
}
