package com.example.connecthuntapp.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.connecthuntapp.Activitys.MainActivity;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterProfessionalFragment extends Fragment {
    private Tags tag = new Tags();
    private EditText regProfessionalEmail, regProfessionalPassword, regProfessionalName;
    private Button mBtnEnter;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextInputLayout errorEmail, errorPassword, errorName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_register_professional, container, false);

        findView(v);
        firebaseConfig();

        mBtnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail() & validatePassword() & validateName()) {
                    createUserWithEmailAndPassword();
                }
            }
        });
        return v;
    }

    private boolean validateEmail() {
        String emailInput = errorEmail.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            errorEmail.setError("Preencha este campo");
            return false;
        } else {
            errorEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = errorPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            errorPassword.setError("Preencha este campo");
            return false;
        } else if (passwordInput.length() < 6) {
            errorPassword.setError("Senha muito fraca");
            return false;
        } else {
            errorPassword.setError(null);
            return true;
        }
    }

    private boolean validateName() {
        String nameInput = errorName.getEditText().getText().toString().trim();

        if (nameInput.isEmpty()) {
            errorName.setError("Preencha este campo");
            return false;
        } else {
            errorName.setError(null);
            return true;
        }
    }

    private void firebaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void findView(View v) {
        regProfessionalEmail = v.findViewById(R.id.regCompanyEmail);
        regProfessionalPassword = v.findViewById(R.id.regCompanyPassword);
        regProfessionalName = v.findViewById(R.id.regCompanyName);
        mBtnEnter = v.findViewById(R.id.mBtnEnter);
        errorName = v.findViewById(R.id.errorName);
        errorEmail = v.findViewById(R.id.errorEmail);
        errorPassword = v.findViewById(R.id.errorPassword);
    }

    private void createUserWithEmailAndPassword() {
        String email = regProfessionalEmail.getText().toString();
        String password = regProfessionalPassword.getText().toString();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Carregando ...");
        progressDialog.setMessage("Aguarde um Momento");

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserInfo();
                        } else {
                            Toast.makeText(getContext(), "Email inválido", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void saveUserInfo() {
        String user_id = mAuth.getCurrentUser().getUid();

        String username = regProfessionalName.getText().toString();

        Map<String, Object> user = new HashMap<>();

        user.put(tag.getKEY_NAME(), username);
        user.put(tag.getKEY_TYPE_USER(), tag.getKEY_PROFESSIONAL());


        DocumentReference document = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id);
        document.set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendToMainProfessional();
                            saveInitialPersonalData();
                            saveInitialGoalData();

                        } else {
                            Log.e(tag.getKEY_ERROR(), "No Document here");
                        }
                    }
                });
    }

    private void sendToMainProfessional() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    private void saveInitialGoalData() {
        final Tags tag = new Tags();

        String user_id = mAuth.getCurrentUser().getUid();

        Map<String, Object> goal = new HashMap<>();
        goal.put(tag.getKEY_GOAL(), "");

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_GOAL()).document(user_id);
        doc.set(goal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void saveInitialPersonalData() {
        final Tags tag = new Tags();

        String user_id = mAuth.getCurrentUser().getUid();

        Map<String, Object> personalData = new HashMap<>();

        personalData.put(tag.getKEY_CEP(), "");
        personalData.put(tag.getKEY_PHONE(), "");
        personalData.put(tag.getKEY_PHONE_RESIDENTIAL(), "");
        personalData.put(tag.getKEY_ADDRESS(), "");
        personalData.put(tag.getKEY_CITY(), "");
        personalData.put(tag.getKEY_PROFESSION(), "");
        personalData.put(tag.getKEY_BIRTH(), "");
        personalData.put(tag.getKEY_USER_ID(), user_id);


        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_PERSONAL_DATA()).document(user_id);

        doc.set(personalData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

}
