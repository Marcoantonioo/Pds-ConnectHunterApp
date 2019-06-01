package com.example.connecthuntapp.Activitys;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.connecthuntapp.Fragments.RegisterCompanyFragment;
import com.example.connecthuntapp.Fragments.RegisterProfessionalFragment;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackToLogin();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbProfessional:
                        loadFragment(new RegisterProfessionalFragment());
                        break;
                    case R.id.rbCompany:
                        loadFragment(new RegisterCompanyFragment());
                        break;
                }
            }
        });
    }

    private void sendBackToLogin() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentRegister, fragment)
                .commit();
    }

    private void findView() {
        radioGroup = findViewById(R.id.radioGroup);
        back = findViewById(R.id.back);
    }

}
