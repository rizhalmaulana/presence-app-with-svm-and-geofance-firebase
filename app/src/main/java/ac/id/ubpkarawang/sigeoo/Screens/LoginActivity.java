package ac.id.ubpkarawang.sigeoo.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

import ac.id.ubpkarawang.sigeoo.Model.Akun.Staf;
import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Utils.Preferences;
import ac.id.ubpkarawang.sigeoo.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding bindingLogin;

    private static final int REQUEST_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingLogin = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bindingLogin.getRoot());

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        firebaseAuth = FirebaseAuth.getInstance();

        bindingLogin.btnGoogleLogin.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, REQUEST_SIGN_IN);
        });
    }

    private void firebaseLoginWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "Login: firebaseLoginWithGoogle");
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    String uid = firebaseUser.getUid();
                    String email = firebaseUser.getEmail();
                    String username = firebaseUser.getDisplayName();
                    String phone = firebaseUser.getPhoneNumber();
                    String uriPhoto = String.valueOf(firebaseUser.getPhotoUrl());

                    Staf stafUser = new Staf();
                    stafUser.setUid(uid);
                    stafUser.setUsername(username);
                    stafUser.setEmail(email);
                    stafUser.setPhone(phone);
                    stafUser.setUriPhoto(uriPhoto);

                    if (authResult.getAdditionalUserInfo().isNewUser()) {
                        Log.d(TAG, "onSuccess: Akun dibuat");
                    }

                    Staf user = new Gson().fromJson(new Gson().toJson(stafUser), Staf.class);
                    Preferences.setStaf(getApplicationContext(), user);

                    Log.d(TAG, "Login Sukses: " + user.getUsername());

                    startActivity(new Intent(this, MainActivity.class));
                    finish();

                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: Login failed " + e.getMessage()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGN_IN) {
            Log.d(TAG, "Login Result: Running");

            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseLoginWithGoogle(account);
            } catch (Exception e) {
                Log.d(TAG, "Login: Exception " + e.getMessage());
            }
        }
    }
}