package com.example.connecthuntapp.Activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.connecthuntapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText edt_email;
    private Button send_email_btn;
    private ImageView backToLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        findView();
        firebaseConfig();

        send_email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailToRecover();
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });
    }

    private void sendEmailToRecover() {
        String userEmail = edt_email.getText().toString();

        progressDialog = new ProgressDialog(RecoverPasswordActivity.this);
        progressDialog.setTitle("Carregando ...");
        progressDialog.setMessage("Aguarde enquanto enviamos o email");
        progressDialog.show();
        if (TextUtils.isEmpty(userEmail)) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Insira seu Email", Toast.LENGTH_LONG).show();
        } else {
            mAuth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Por favor, Verifique seu Email", Toast.LENGTH_LONG).show();
                                sendToLogin();
                            } else {
                                String erro = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), "Informe um email Válido " + erro, Toast.LENGTH_LONG).show();

                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void firebaseConfig() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void findView() {
        edt_email = findViewById(R.id.edt_email);
        send_email_btn = findViewById(R.id.send_email_btn);
        backToLogin = findViewById(R.id.backToLogin);
    }
}
