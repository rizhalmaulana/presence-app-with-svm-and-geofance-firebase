package ac.id.ubpkarawang.sigeoo.Utils;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    public static final String PREF_COOKIES = Static.MyPref;
    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        HashSet<String> preferences = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_COOKIES, new HashSet<String>());
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
        }

        return chain.proceed(builder.build());
    }
}
