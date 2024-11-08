package com.example.yoga_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.yoga_app.databinding.HomePageActivityBinding;
import com.example.yoga_app.fragment.AccountFragment;
import com.example.yoga_app.fragment.MainHomeFragment;
import com.example.yoga_app.fragment.ScheduleFragment;
import com.example.yoga_app.fragment.SearchFragment;
import com.example.yoga_app.fragment.SettingFragment;

public class HomePageActivity extends AppCompatActivity {

    HomePageActivityBinding homePageActivityBinding;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //Check wifi connection here
//        if (!WifiChecker.isWifiConnected(this)) {
//            WifiChecker.showWifiDialog(this);
//        }

        homePageActivityBinding = HomePageActivityBinding.inflate(getLayoutInflater());
        setContentView(homePageActivityBinding.getRoot());

        replaceFragment(new MainHomeFragment());
        homePageActivityBinding.bottomNavigationView.setBackground(null);

        homePageActivityBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home){
                replaceFragment(new MainHomeFragment());
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
            }else if (item.getItemId() == R.id.setting) {
                replaceFragment(new SettingFragment());
            }else if (item.getItemId() == R.id.account) {
                replaceFragment(new AccountFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d("MainHomeFragment", "onPause() called");
//        // Lưu thời gian hiện tại vào SharedPreferences khi Activity dừng
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putLong("lastActiveTime", System.currentTimeMillis());
//        editor.apply();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        long lastActiveTime = sharedPreferences.getLong("lastActiveTime", 0);
//        long currentTime = System.currentTimeMillis();
//
//        if (currentTime - lastActiveTime > 60 * 60 * 1000) {
//            logout();
//        }
//    }

//    private void logout() {
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
//
//        Toast.makeText(this, "Logged out due to inactivity", Toast.LENGTH_SHORT).show();
//
//        startActivity(new Intent(this, InitialActivity.class));
//        finish();
//    }
}
