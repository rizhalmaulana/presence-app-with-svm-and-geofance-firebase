package ac.id.ubpkarawang.sigeoo.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import ac.id.ubpkarawang.sigeoo.Model.Akun.Staf;

public class Preferences {
    public static Staf getStaf(Context context){
        try{
            String json = getString(context, Static.USER_DATA);
            return new Gson().fromJson(json, Staf.class);
        }catch (Exception e){
            return null;
        }
    }

    public static void setStaf(Context context, Staf staf){
        putString(context, Static.USER_DATA, new Gson().toJson(staf));
    }

    public static String getToken(Context context){
        return getString(context, Static.TOKEN);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.edit();
    }

    public static void putString(Context context, String key, String value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value).commit();
    }

    public static String getString(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void putBoolean(Context context, String key, boolean value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void setLoginFlag(Context context, boolean flag){
        putBoolean(context, Static.LOGIN_KEY, flag);
    }

    public static void setScanFlag(Context context, boolean flag){
        putBoolean(context, Static.SCAN_KEY, flag);
    }

    public static boolean getLoginFlag(Context context){
        return getBoolean(context, Static.LOGIN_KEY);
    }
}
