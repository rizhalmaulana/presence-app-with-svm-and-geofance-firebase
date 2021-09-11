package ac.id.ubpkarawang.sigeoo.Screens;

import android.content.Intent;
import android.os.Bundle;

import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Utils.Preferences;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final int time = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Thread(() -> {
            try{
                Thread.sleep(time);
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                runOnUiThread(() -> {
                    startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));
                    finish();
                });
            }
        }).start();
    }
}