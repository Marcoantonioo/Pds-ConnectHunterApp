package com.example.connecthuntapp.Activitys;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class CompanyAboutActivity extends AppCompatActivity {
    private final Tags tag = new Tags();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private ImageView back, saveData;
    private EditText about_company;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_about);

        findView();
        firebaseConfig();
        user_id = mAuth.getCurrentUser().getUid();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateAboutSalary()){
                    addAboutCompany();
                }
            }
        });

        getCompanyData();


    }

    private void getCompanyData() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String about = documentSnapshot.getString(tag.getKEY_ABOUT());
                    about_company.setText(about);
                }
            }
        });
    }

    private void addAboutCompany() {
        String about = about_company.getText().toString();

        Map<String, Object> map = new HashMap<>();

        map.put(tag.getKEY_ABOUT(), about);

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id);
        doc.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Dados Atualizados", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateAboutSalary() {
        TextInputLayout error_about_company = findViewById(R.id.error_about_company);
        String about = error_about_company.getEditText().getText().toString();

        if (about.isEmpty()) {
            error_about_company.setError("Preencha este campo");
            return false;
        } else {
            error_about_company.setError(null);
            return true;
        }
    }

    private void firebaseConfig() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void findView() {
        back = findViewById(R.id.back);
        about_company = findViewById(R.id.about_company);
        saveData = findViewById(R.id.saveData);
    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
