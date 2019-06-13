package com.example.connecthuntapp.Activitys;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfessionalExperienceActivity extends AppCompatActivity {

    private EditText epCompanyName, epJobBusy, dateInitialExperience, dateEndExperience, epDescriptionCompany;
    private ImageView saveData, back;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_professional);

        findView();
        fireBaseConfig();

        user_id = mAuth.getCurrentUser().getUid();

        maskFormatter();

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFieldCompanyName() & validateFieldDescriptionExperience() & validateFieldEndDate() & validateFieldInitialDate() & validateFieldJobBusy()) {
                    saveProfessionalExperience();
                }
            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private boolean validateFieldDescriptionExperience() {
        TextInputLayout errorDescriptionExperience = findViewById(R.id.errorDescriptionExperience);
        String description_experience = errorDescriptionExperience.getEditText().getText().toString();

        if (description_experience.isEmpty()) {
            errorDescriptionExperience.setError("Preencha este campo");
            return false;
        } else {
            errorDescriptionExperience.setError(null);
            return true;
        }
    }

    private boolean validateFieldInitialDate() {
        TextInputLayout errorDateInitialExperience = findViewById(R.id.errorDateInitialExperience);
        String initial_date = errorDateInitialExperience.getEditText().getText().toString();

        if (!checkDateFormat(initial_date)) {
            errorDateInitialExperience.setError("Data inválida");
            return false;
        } else {
            errorDateInitialExperience.setError(null);
            return true;
        }
    }

    private boolean validateFieldEndDate() {
        TextInputLayout errorDateEndExperience = findViewById(R.id.errorDateEndExperience);
        String end_date = errorDateEndExperience.getEditText().getText().toString();

        if (!checkDateFormat(end_date)) {
                errorDateEndExperience.setError("Data inválida");
            return false;
        } else {
            errorDateEndExperience.setError(null);
            return true;
        }
    }

    public Boolean checkDateFormat(String date) {
        if (date == null || !date.matches("^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.]((19|20)\\d\\d)$"))
            return false;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            format.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean validateFieldJobBusy() {
        TextInputLayout errorJobBusy = findViewById(R.id.errorJobBusy);
        String job_busy = errorJobBusy.getEditText().getText().toString();

        if (job_busy.isEmpty()) {
            errorJobBusy.setError("Preencha este campo");
            return false;
        } else {
            errorJobBusy.setError(null);
            return true;
        }
    }

    private boolean validateFieldCompanyName() {
        TextInputLayout errorCompanyName = findViewById(R.id.errorCompanyName);
        String name_company = errorCompanyName.getEditText().getText().toString();

        if (name_company.isEmpty()) {
            errorCompanyName.setError("Preencha este campo");
            return false;
        } else {
            errorCompanyName.setError(null);
            return true;
        }
    }

    private void saveProfessionalExperience() {
        final Tags tag = new Tags();

        user_id = mAuth.getCurrentUser().getUid();

        String initialDate = dateInitialExperience.getText().toString();
        String finalDate = dateEndExperience.getText().toString();
        String description = epDescriptionCompany.getText().toString();
        String companyName = epCompanyName.getText().toString();
        String office = epJobBusy.getText().toString();

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
        MaskTextWatcher mtwInicio = new MaskTextWatcher(dateInitialExperience, smfInicio);
        dateInitialExperience.addTextChangedListener(mtwInicio);
        //End Mask
        //Mask For Data Fim
        SimpleMaskFormatter smfFim = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtwFim = new MaskTextWatcher(dateEndExperience, smfFim);
        dateEndExperience.addTextChangedListener(mtwFim);
        //End Mask
    }

    private void fireBaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void findView() {
        back = findViewById(R.id.back);
        saveData = findViewById(R.id.saveData);
        epCompanyName = findViewById(R.id.epCompanyName);
        epJobBusy = findViewById(R.id.epJobBusy);
        epDescriptionCompany = findViewById(R.id.epDescriptionExperience);
        dateInitialExperience = findViewById(R.id.dateInitialExperience);
        dateEndExperience = findViewById(R.id.dateEndExperience);
    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
