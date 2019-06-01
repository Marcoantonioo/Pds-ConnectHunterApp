package com.example.connecthuntapp.Activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.connecthuntapp.Fragments.ProfessionalCurriculumFragment;
import com.example.connecthuntapp.Fragments.ProfessionalFavoriteFragment;
import com.example.connecthuntapp.Fragments.ProfessionalHistoryFragment;
import com.example.connecthuntapp.Fragments.ProfessionalHomeFragment;
import com.example.connecthuntapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ProfessionalHomeFragment professionalHomeFragment;
    private ProfessionalCurriculumFragment professionalCurriculumFragment;
    private ProfessionalHistoryFragment professionalHistoryFragment;
    private ProfessionalFavoriteFragment professionalFavoriteFragment;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        findView();

        fragmentLoad();
        setFragment(professionalHomeFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.nav_home:
                        setFragment(professionalHomeFragment);
                        return true;
                    case R.id.nav_curriculum:
                        setFragment(professionalCurriculumFragment);
                        return true;
                    case R.id.nav_person:
                        setFragment(professionalHistoryFragment);
                        return true;
                    case R.id.nav_fav:
                        setFragment(professionalFavoriteFragment);
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
        professionalHomeFragment = new ProfessionalHomeFragment();
        professionalCurriculumFragment = new ProfessionalCurriculumFragment();
        professionalHistoryFragment = new ProfessionalHistoryFragment();
        professionalFavoriteFragment = new ProfessionalFavoriteFragment();
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
