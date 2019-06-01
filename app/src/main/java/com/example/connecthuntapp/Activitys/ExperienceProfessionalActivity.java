package com.example.connecthuntapp.Activitys;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ExperienceProfessionalActivity extends AppCompatActivity {

    private EditText edt_company_name, edt_office, edt_initial_date, edt_final_date, edt_description;
    private ImageView saveData, back;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_professional);

        findview();
        firebaseConfig();

        user_id = mAuth.getCurrentUser().getUid();

        maskFormatter();

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfessionalExperience();
            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void saveProfessionalExperience() {
        final Tags tag = new Tags();

        user_id = mAuth.getCurrentUser().getUid();

        String initialDate = edt_initial_date.getText().toString();
        String finalDate = edt_final_date.getText().toString();
        String description = edt_description.getText().toString();
        String companyName = edt_company_name.getText().toString();
        String office = edt_office.getText().toString();

        if (!TextUtils.isEmpty(finalDate) && !TextUtils.isEmpty(finalDate) && !TextUtils.isEmpty(description) &&
                !TextUtils.isEmpty(companyName) && !TextUtils.isEmpty(office)) {

            Map<String, String> experience = new HashMap<>();

            experience.put(tag.getKEY_INITIAL_DATE(), initialDate);
            experience.put(tag.getKEY_FINAL_DATE(), finalDate);
            experience.put(tag.getKEY_DESCRIPTION_EXPERIENCE(), description);
            experience.put(tag.getKEY_COMPANY_NAME(), companyName);
            experience.put(tag.getKEY_OFFICE(), office);
            experience.put(tag.getKEY_USER_ID(), user_id);

            DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                    .collection(tag.getKEY_PROFESSIONAL_EXPERIENCE()).document();
            doc.set(experience)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Experiencia Adicionada", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Preencha os campos !", Toast.LENGTH_SHORT).show();
        }
    }

    private void maskFormatter() {
        //Mask For Data inicio
        SimpleMaskFormatter smfInicio = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtwInicio = new MaskTextWatcher(edt_initial_date, smfInicio);
        edt_initial_date.addTextChangedListener(mtwInicio);
        //End Mask
        //Mask For Data Fim
        SimpleMaskFormatter smfFim = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtwFim = new MaskTextWatcher(edt_final_date, smfFim);
        edt_final_date.addTextChangedListener(mtwFim);
        //End Mask
    }


    private void firebaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void findview() {
        back = findViewById(R.id.back);
        saveData = findViewById(R.id.saveData);
        edt_company_name = findViewById(R.id.epNomeEmpresa);
        edt_office = findViewById(R.id.epCargoOcupado);
        edt_description = findViewById(R.id.epDescricaoExperiencia);
        edt_initial_date = findViewById(R.id.dataInicioEperiencia);
        edt_final_date = findViewById(R.id.dataFimExperiencia);
    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
