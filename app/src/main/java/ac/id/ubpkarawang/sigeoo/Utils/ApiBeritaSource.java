package ac.id.ubpkarawang.sigeoo.Utils;

import static ac.id.ubpkarawang.sigeoo.Utils.ApiBeritaClient.BASE_URL;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import ac.id.ubpkarawang.sigeoo.Model.Informasi.BeritaResponse;

public class ApiBeritaSource {
    private final static String API_KEY = "da1131b188264bd6b8842ffa7db53761";
    public static final String URL_BERITA = BASE_URL + "/v2/top-headlines?country=id&apiKey={apiKey}";
    public void getBerita(String movieEndpoint, final UtlisCallback callback) {
        AndroidNetworking.get(movieEndpoint)
                .addPathParameter("apiKey", API_KEY)
                .setTag(ApiBeritaSource.class)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsObject(BeritaResponse.class, new ParsedRequestListener<BeritaResponse>() {
                    @Override
                    public void onResponse(BeritaResponse response) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("ERROR", "onError: ", anError);
                    }
                });
    }
}
