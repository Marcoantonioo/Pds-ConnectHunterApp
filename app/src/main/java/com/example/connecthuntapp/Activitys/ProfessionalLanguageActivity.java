package com.example.connecthuntapp.Activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfessionalLanguageActivity extends AppCompatActivity {
    private Spinner spinner1, spinner2;
    private ImageView saveData, back;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_language);

        findView();


        firebaseConfig();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLanguage();
            }
        });
    }
    private void firebaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }


    private void addLanguage() {
        final Tags tag = new Tags();

        String idioma = String.valueOf(spinner1.getSelectedItem());
        String nivel = String.valueOf(spinner2.getSelectedItem());

        user_id = mAuth.getCurrentUser().getUid();

        Map<String, String> language = new HashMap<>();
        language.put(tag.getKEY_LANGUAGE_LEVEL(), nivel);
        language.put(tag.getKEY_LANGUAGE_NAME(), idioma);
        language.put(tag.getKEY_USER_ID(), user_id);

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id).collection(tag.getKEY_LANGUAGE()).document();
        doc.set(language)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Idioma Adicionado", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    }
                });
    }


    private void findView() {
        back = findViewById(R.id.back);
        saveData = findViewById(R.id.saveData);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
