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

import com.example.connecthuntapp.Activitys.MainCompanyActivity;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterCompanyFragment extends Fragment {
    private Tags tag = new Tags();
    private EditText regCompanyEmail, regCompanyPassword, regCompanyName;
    private Button mBtnEnter;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextInputLayout errorEmail, errorPassword, errorName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_register_company, container, false);

        findView(v);
        fireBaseConfig();

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

    private void findView(View v) {
        regCompanyEmail = v.findViewById(R.id.regCompanyEmail);
        regCompanyPassword = v.findViewById(R.id.regCompanyPassword);
        regCompanyName = v.findViewById(R.id.regCompanyName);
        mBtnEnter = v.findViewById(R.id.mBtnEnter);
        errorName = v.findViewById(R.id.errorName);
        errorEmail = v.findViewById(R.id.errorEmail);
        errorPassword = v.findViewById(R.id.errorPassword);
    }

    private void fireBaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void createUserWithEmailAndPassword() {
        String email = regCompanyEmail.getText().toString();
        String password = regCompanyPassword.getText().toString();
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

        String company_name = regCompanyName.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put(tag.getKEY_NAME_COMPANY(), company_name);
        user.put(tag.getKEY_TYPE_USER(), tag.getKEY_COMPANY());


        DocumentReference document = firebaseFirestore.collection(tag.getKEY_COMPANY()).document(user_id);
        document.set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendToMainCompany();
                            saveInitialJob();
                        } else {
                            Log.e(tag.getKEY_ERROR(), "No Document here");
                        }
                    }
                });
    }

    private void saveInitialJob() {
        String user_id = mAuth.getCurrentUser().getUid();

        Map<String, Object> vaga = new HashMap<>();
        vaga.put(tag.getKEY_USER_ID(), user_id);
        vaga.put(tag.getKEY_JOB_DATE(), FieldValue.serverTimestamp());
        vaga.put(tag.getKEY_SHOW_JOB(),false);

        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id)
                .set(vaga).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void sendToMainCompany() {
        Intent intent = new Intent(getContext(), MainCompanyActivity.class);
        startActivity(intent);
    }
}
