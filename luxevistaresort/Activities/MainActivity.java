package com.luxevistaresort.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.Fragments.HomeFragment;
import com.luxevistaresort.Fragments.NotificationFragment;
import com.luxevistaresort.Fragments.ProfileFragment;
import com.luxevistaresort.Fragments.RoomsFragment;
import com.luxevistaresort.Models.Room;
import com.luxevistaresort.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(this);
        dbHelper.getWritableDatabase();

        initViews();
        setupToolbarAndDrawer();
        setupBottomNavigation();
        if (getIntent().getBooleanExtra("openProfile", false)) {
            // Open profile tab
            bottomNavigationView.setSelectedItemId(R.id.profile);
            replaceFragment(new ProfileFragment());
        } else {
            // Default
            bottomNavigationView.setSelectedItemId(R.id.home);
            replaceFragment(new HomeFragment());
        }
        setupNavigationView();
        setupFabClick();

    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbarAndDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setBackground(null); // removes default FAB overlap background
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.rooms) {
                replaceFragment(new RoomsFragment());
            } else if (item.getItemId() == R.id.notification) {
                replaceFragment(new NotificationFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_rooms) {
                replaceFragment(new RoomsFragment());
            } else if (id == R.id.nav_offers) {
                Toast.makeText(this, "Offers clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.profile) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }

            drawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        });
    }


    private void setupFabClick() {
        fab.setOnClickListener(v -> showBottomDialog());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void showBottomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        LinearLayout layoutSpa = dialog.findViewById(R.id.layoutSpa);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        layoutSpa.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(MainActivity.this, ServiceReservationActivity.class));
            Toast.makeText(this, "Service Reservation clicked", Toast.LENGTH_SHORT).show();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}
