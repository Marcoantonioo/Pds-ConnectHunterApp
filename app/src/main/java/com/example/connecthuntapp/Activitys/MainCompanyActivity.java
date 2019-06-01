package com.example.connecthuntapp.Activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.connecthuntapp.Fragments.CompanyHistoryFragment;
import com.example.connecthuntapp.Fragments.CompanyHomeFragment;
import com.example.connecthuntapp.Fragments.CompanyVagaFragment;
import com.example.connecthuntapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainCompanyActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CompanyHomeFragment companyHomeFragment;
    private CompanyHistoryFragment companyHistoryFragment;
    private CompanyVagaFragment companyVagaFragment;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_company);

        mAuth = FirebaseAuth.getInstance();
        findView();

        fragmentLoad();
        setFragment(companyHomeFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.nav_home:
                        setFragment(companyHomeFragment);
                        return true;
                    case R.id.nav_curriculum:
                        setFragment(companyVagaFragment);
                        return true;
                    case R.id.nav_person:
                        setFragment(companyHistoryFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
            finish();
        }
    }
    private void sendToLogin(){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void fragmentLoad() {
        companyHomeFragment = new CompanyHomeFragment();
        companyHistoryFragment = new CompanyHistoryFragment();
        companyVagaFragment = new CompanyVagaFragment();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    private void findView() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
    }
}
