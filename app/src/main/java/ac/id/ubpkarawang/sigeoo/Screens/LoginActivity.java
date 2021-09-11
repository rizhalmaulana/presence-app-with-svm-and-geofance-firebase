package ac.id.ubpkarawang.sigeoo.Screens;

import ac.id.ubpkarawang.sigeoo.R;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button btnMasuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnMasuk = findViewById(R.id.btn_masuk);

        btnMasuk.setOnClickListener(v -> {
            Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
    }
}