package ac.id.ubpkarawang.sigeoo.Screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import ac.id.ubpkarawang.sigeoo.Model.Akun.Staf;
import ac.id.ubpkarawang.sigeoo.Utils.Preferences;
import ac.id.ubpkarawang.sigeoo.databinding.ActivityOnboardingBinding;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding onboardingBinding;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "OnBoarding";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingBinding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(onboardingBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        onboardingBinding.btnLanjutkan.setOnClickListener(v -> {
            if (Preferences.getStaf(getApplicationContext()) != null) {
                startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
            }
            finish();
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Staf stafUser = new Gson().fromJson(new Gson().toJson(Preferences.getStaf(getApplicationContext())), Staf.class);

        if (firebaseUser != null) {
            if (Preferences.getStaf(getApplicationContext()) != null) {

                Log.d(TAG, "OnBoard: " + stafUser.getUsername());
                Log.d(TAG, "OnBoard Firebase: " + firebaseUser.getDisplayName());

                Toast.makeText(this, "Hai " + firebaseUser.getDisplayName() + ". Welcome Back!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Log.d(TAG, "OnBoard: Preferences Null");
            }
        } else {
            Log.d(TAG, "OnBoard: Firebase Null");
        }
    }
}