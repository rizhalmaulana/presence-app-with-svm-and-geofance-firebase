package ac.id.ubpkarawang.sigeoo.Helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static Context context;
    private MaterialDialog materialDialogPleasewait;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void showSnackbar(String message){
        Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_SHORT).show();
    }

    public void showActivity(Context packageContext, Class<?> cls) {
        Intent intent = getIntent(packageContext, cls);
        showActivity(intent);
    }

    public Intent getIntent(Context packageContext, Class<?> cls) {
        return new Intent(packageContext, cls);
    }

    public void showActivity(Intent intent) {
        startActivity(intent);
    }

    public void showActivityAndFinishCurrentActivity(Context packageContext, Class<?> cls) {
        Intent intent = getIntent(packageContext, cls);
        showActivity(intent);
        finish();
    }
}
