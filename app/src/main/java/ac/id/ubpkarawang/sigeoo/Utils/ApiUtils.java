package ac.id.ubpkarawang.sigeoo.Utils;

import android.content.Context;

public class ApiUtils {
    public static String API = Static.BASE_URL;
    public static String API_SVM = Static.BASE_URL_PYTHON;

    public static MobileService MobileService(Context context){
        return RetrofitClient.getClient(context, API).create(MobileService.class);
    }

    public static MobileService servicePython(Context context){
        return RetrofitClient.getSvm(context, API_SVM).create(MobileService.class);
    }
}
