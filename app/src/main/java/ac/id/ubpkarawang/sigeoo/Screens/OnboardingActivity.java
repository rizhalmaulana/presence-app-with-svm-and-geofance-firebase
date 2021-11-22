package ac.id.ubpkarawang.sigeoo.Screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.button.MaterialButton;

import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Utils.Preferences;

public class OnboardingActivity extends AppCompatActivity {

    MaterialButton btnLanjutkan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        btnLanjutkan = findViewById(R.id.btn_lanjutkan);
        btnLanjutkan.setOnClickListener(v -> {
            if (Preferences.getStaf(getApplicationContext()) != null) {
                startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
            }
            finish();
        });
    }
}