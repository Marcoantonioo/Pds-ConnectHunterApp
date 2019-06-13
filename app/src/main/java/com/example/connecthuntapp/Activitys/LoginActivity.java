package com.example.connecthuntapp.Activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout txt_account;
    private TextView forgotPasswd;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private Button login_btn;
    private EditText edit_email;
    private EditText edit_password;
    private ProgressDialog progressDialog;
    private TextInputLayout errorPassword, errorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findView();
        fireBaseConfig();

        txt_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegister();
            }
        });
        forgotPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRecoverPassword();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePassword() & validateEmail()) {
                    signInUserWithEmailAndPassword();
                }


            }
        });
    }



    private boolean validateEmail() {
        String email = errorEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            errorEmail.setError("Informe um email");
            return false;
        } else {
            errorEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = errorPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            errorPassword.setError("Informe uma senha");
            return false;
        } else {
            errorPassword.setError(null);
            return true;
        }

    }

    private void signInUserWithEmailAndPassword() {
        final String email = edit_email.getText().toString();
        final String password = edit_password.getText().toString();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Carregando ...");
        progressDialog.setMessage("Aguarde um Momento");

        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            verifyUserCompany();
                            verifyUserProfessional();

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(), "Email inválido" + error, Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();

                    }
                });
    }


    private void fireBaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void sendToRegister() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void findView() {
        txt_account = findViewById(R.id.txt_account);
        forgotPasswd = findViewById(R.id.forgotPasswd);
        login_btn = findViewById(R.id.login_btn);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        errorEmail = findViewById(R.id.errorEmail);
        errorPassword = findViewById(R.id.errorPassword);
    }

    private void verifyUserProfessional() {
        Tags tag = new Tags();
        String user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    sendToMainProfessional();
                }
            }
        });
    }

    private void verifyUserCompany() {
        Tags tag = new Tags();
        String user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection(tag.getKEY_COMPANY()).document(user_id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    sendToMainCompany();
                }
            }
        });
    }

    private void sendToMainProfessional() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMainCompany() {
        Intent intent = new Intent(getApplicationContext(), MainCompanyActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToRecoverPassword() {
        Intent intent = new Intent(getApplicationContext(), RecoverPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            verifyUserProfessional();
            verifyUserCompany();
        }

    }
}
