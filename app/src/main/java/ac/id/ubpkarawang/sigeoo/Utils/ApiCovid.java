package ac.id.ubpkarawang.sigeoo.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCovid {
    public static String API_KEY = "https://api.kawalcorona.com/";
    public static CovidService service(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_KEY)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CovidService covidService = retrofit.create(CovidService.class);
        return covidService;
    }
}
