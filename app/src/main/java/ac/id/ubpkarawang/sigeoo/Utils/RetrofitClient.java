package ac.id.ubpkarawang.sigeoo.Utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Retrofit retrofit;
    public static Retrofit getClient(final Context context, String baseUrl) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder()
                .setLenient()
                .excludeFieldsWithModifiers(Modifier.STATIC)
                .create();

        httpClient.readTimeout(300, TimeUnit.SECONDS)
                .connectTimeout(300, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(logging)
                .addInterceptor(new AddCookiesInterceptor(context))
                .addInterceptor(new ReceivedCookiesInterceptor(context))
                .addInterceptor(chain -> {
                    Request newRequest  = chain.request().newBuilder()
                            .addHeader("Authorization", Preferences.getToken(context))
                            .addHeader("Content-Type", "multipart/form-data")
                            .build();
                    return chain.proceed(newRequest);
                });

        retrofit = new Retrofit.Builder()
                .baseUrl(Static.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
        return retrofit;

    }
}