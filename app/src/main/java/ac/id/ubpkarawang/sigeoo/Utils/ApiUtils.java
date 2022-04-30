package ac.id.ubpkarawang.sigeoo.Utils;

import android.content.Context;

public class ApiUtils {
    public static String API = Static.BASE_URL;

    public static MobileService MobileService(Context context){
        return RetrofitClient.getClient(context, API).create(MobileService.class);
    }
}
