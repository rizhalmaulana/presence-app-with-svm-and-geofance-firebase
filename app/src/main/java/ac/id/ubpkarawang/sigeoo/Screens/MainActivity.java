package ac.id.ubpkarawang.sigeoo.Screens;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ac.id.ubpkarawang.sigeoo.Adapters.ViewPagerAdapter;
import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Screens.informasi.InformasiFragment;
import ac.id.ubpkarawang.sigeoo.Screens.profil.ProfilFragment;
import ac.id.ubpkarawang.sigeoo.Screens.utama.UtamaFragment;

public class MainActivity extends AppCompatActivity {

    ViewPagerAdapter adapter;
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    MenuItem prevMenuItem;

    boolean doubleBackToExit = false;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.vp1);
        bottomNavigationView = findViewById(R.id.nav_view);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_informasi:
                    viewPager.setCurrentItem(0);
                    break;

                case R.id.navigation_facerecognation:
                    viewPager.setCurrentItem(1);
                    break;

                case R.id.navigation_profil:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return false;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page",""+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter  = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new InformasiFragment(),"");
        adapter.AddFragment(new UtamaFragment(),"");
        adapter.AddFragment(new ProfilFragment(),"");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExit){
            super.onBackPressed();
            return;
        }

        this.doubleBackToExit = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(() -> doubleBackToExit = false, 2000);
    }
}