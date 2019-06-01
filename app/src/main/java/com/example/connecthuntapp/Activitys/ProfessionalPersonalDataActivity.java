package com.example.connecthuntapp.Activitys;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfessionalPersonalDataActivity extends AppCompatActivity {
    private ImageView back, saveData;
    private EditText edt_phone, edt_phone_residential, edt_cep, edt_name,
            edt_email, edt_profession, edt_city, edt_address, edt_birth;
    private String user_id;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private TextInputLayout error_name, error_profession, error_birth, error_city, error_address, error_cep,
            error_cel_phone, error_cel_residential, error_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_personal_data);


        findView();
        firebaseConfig();

        user_id = mAuth.getCurrentUser().getUid();

        maskFormatter();

        getProfessionalPersonalData();
        getUserProfessionalData();

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail() & validateName() & validateAddress() & validateBirth() & validateCelPhone() &
                        validateCelResidential() & validateCep() & validateCity() & validateProfession()) {
                    updateProfessionalPersonalData();
                    updateProfessionalName();
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

    private boolean validateEmail() {
        String email = error_email.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            error_email.setError("Preencha esse campo");
            return false;
        } else {
            error_email.setError(null);
            return true;
        }
    }

    private boolean validateCelResidential() {
        String cel_residential = error_cel_residential.getEditText().getText().toString().trim();

        if (cel_residential.isEmpty()) {
            error_cel_residential.setError("Preencha esse campo");
            return false;
        } else if (cel_residential.length() < 10) {
            error_cel_residential.setError("Número inválido");
            return false;
        }else {
            error_cel_residential.setError(null);
            return android.util.Patterns.PHONE.matcher(cel_residential).matches();
        }
    }

    private boolean validateCelPhone() {
        String cel_phon = error_cel_phone.getEditText().getText().toString().trim();

        if (cel_phon.isEmpty()) {
            error_cel_phone.setError("Preencha esse campo");
            return false;
        } else if (cel_phon.length() < 11){
            error_cel_phone.setError("Número Inválido");
            return false;
        }else {
            error_cel_phone.setError(null);
            return true;
        }
    }

    private boolean validateCep() {
        String cep = error_cep.getEditText().getText().toString().trim();

        if (cep.isEmpty()) {
            error_cep.setError("Preencha esse campo");
            return false;
        } else if (cep.length() < 9 ) {
            error_cep.setError("Cep inválido");
            return false;
        }else {
            error_cep.setError(null);
            return true;
        }
    }
    public Boolean checkDateFormat(String date){
        if (date == null || !date.matches("^(1[0-9]|0[1-9]|3[0-1]|2[1-9])/(0[1-9]|1[0-2])/[0-9]{4}$"))
            return false;
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            format.parse(date);
            return true;
        }catch (ParseException e){
            return false;
        }
    }

    private boolean validateAddress() {
        String address = error_address.getEditText().getText().toString().trim();

        if (address.isEmpty()) {
            error_address.setError("Preencha esse campo");
            return false;
        } else {
            error_address.setError(null);
            return true;
        }
    }

    private boolean validateCity() {
        String city = error_city.getEditText().getText().toString().trim();

        if (city.isEmpty()) {
            error_city.setError("Preencha esse campo");
            return false;
        } else {
            error_city.setError(null);
            return true;
        }
    }

    private boolean validateBirth() {
        String birth = error_birth.getEditText().getText().toString().trim();

        if (!checkDateFormat(birth)) {
            error_birth.setError("Data de Nascimento Inválido");
            return false;
        } else {
            error_birth.setError(null);
            return true;
        }
    }

    private boolean validateProfession() {
        String profession = error_profession.getEditText().getText().toString().trim();

        if (profession.isEmpty()) {
            error_profession.setError("Preencha esse campo");
            return false;
        } else {
            error_profession.setError(null);
            return true;
        }
    }

    private boolean validateName() {
        String name = error_name.getEditText().getText().toString().trim();

        if (name.isEmpty()) {
            error_name.setError("Preencha esse campo");
            return false;
        } else {
            error_name.setError(null);
            return true;
        }
    }

    private void getProfessionalPersonalData() {
        final Tags tag = new Tags();
        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection("Personal Data").document(user_id);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {

                        String city = task.getResult().getString(tag.getKEY_CITY());
                        edt_city.setText(city);

                        String address = task.getResult().getString(tag.getKEY_ADDRESS());
                        edt_address.setText(address);

                        String profession = task.getResult().getString(tag.getKEY_PROFESSION());
                        edt_profession.setText(profession);

                        String cep = task.getResult().getString(tag.getKEY_CEP());
                        edt_cep.setText(cep);

                        String phone = task.getResult().getString(tag.getKEY_PHONE());
                        edt_phone.setText(phone);

                        String phone_residential = task.getResult().getString(tag.getKEY_PHONE_RESIDENTIAL());
                        edt_phone_residential.setText(phone_residential);

                        String birth = task.getResult().getString(tag.getKEY_BIRTH());
                        edt_birth.setText(birth);
                    } else {
                        Log.d(tag.getKEY_ERROR(), "No documents found.");
                    }

                } else {
                    Log.d(tag.getKEY_ERROR(), "Failed ", task.getException());
                }
            }
        });
    }

    private void getUserProfessionalData() {
        final Tags tag = new Tags();

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id);
        doc.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                String name = task.getResult().getString(tag.getKEY_NAME());

                                edt_name.setText(name);

                                currentUser = mAuth.getCurrentUser();
                                edt_email.setText(currentUser.getEmail());
                            } else {
                                Log.d(tag.getKEY_ERROR(), "No documents found.");
                            }
                        } else {
                            Log.d(tag.getKEY_ERROR(), "Failed ", task.getException());
                        }
                    }
                });
    }

    private void updateProfessionalPersonalData() {
        final Tags tag = new Tags();
        user_id = mAuth.getCurrentUser().getUid();

        String phone = edt_phone.getText().toString();
        String phone_residential = edt_phone_residential.getText().toString();
        String cep = edt_cep.getText().toString();
        String profession = edt_profession.getText().toString();
        String city = edt_city.getText().toString();
        String address = edt_address.getText().toString();
        String birth = edt_birth.getText().toString();

        Map<String, Object> personalData = new HashMap<>();

        personalData.put(tag.getKEY_PHONE(), phone);
        personalData.put(tag.getKEY_PHONE_RESIDENTIAL(), phone_residential);
        personalData.put(tag.getKEY_CEP(), cep);
        personalData.put(tag.getKEY_PROFESSION(), profession);
        personalData.put(tag.getKEY_CITY(), city);
        personalData.put(tag.getKEY_ADDRESS(), address);
        personalData.put(tag.getKEY_BIRTH(), birth);

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection("Personal Data").document(user_id);
        doc.update(personalData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Dados Atualizados", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void updateProfessionalName() {
        String user_id = mAuth.getCurrentUser().getUid();
        final Tags tag = new Tags();

        String username = edt_name.getText().toString();

        Map<String, Object> user = new HashMap<>();

        user.put(tag.getKEY_NAME(), username);

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id);
        doc.update(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Dados Atualizados", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void maskFormatter() {
        //Mask For Cep
        SimpleMaskFormatter smfCep = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher mtwCep = new MaskTextWatcher(edt_cep, smfCep);
        edt_cep.addTextChangedListener(mtwCep);
        //End Mask
        //Mask For DataNasc
        SimpleMaskFormatter smfNasc = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtwNasc = new MaskTextWatcher(edt_birth, smfNasc);
        edt_birth.addTextChangedListener(mtwNasc);
        //End Mask
    }

    private void firebaseConfig() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void findView() {
        back = findViewById(R.id.back);
        saveData = findViewById(R.id.saveData);
        edt_phone = findViewById(R.id.edt_cel_1);
        edt_phone_residential = findViewById(R.id.edt_tel_residencial_2);
        edt_cep = findViewById(R.id.edt_cep);
        edt_name = findViewById(R.id.edt_nome);
        edt_email = findViewById(R.id.edt_email);
        edt_city = findViewById(R.id.edt_cidade);
        edt_profession = findViewById(R.id.edt_profissao);
        edt_address = findViewById(R.id.edt_endereco);
        edt_birth = findViewById(R.id.edt_nascimento);
        error_name = findViewById(R.id.error_name);
        error_profession = findViewById(R.id.error_profession);
        error_birth = findViewById(R.id.error_birth);
        error_city = findViewById(R.id.error_city);
        error_address = findViewById(R.id.error_address);
        error_cep = findViewById(R.id.error_cep);
        error_cel_phone = findViewById(R.id.error_cel_phone);
        error_cel_residential = findViewById(R.id.error_cel_residential);
        error_email = findViewById(R.id.error_email);
    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
