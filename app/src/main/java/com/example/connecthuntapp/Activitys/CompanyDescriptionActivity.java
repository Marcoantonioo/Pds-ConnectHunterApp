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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class CompanyDescriptionActivity extends AppCompatActivity {
    private final Tags tag = new Tags();
    private ImageView back, saveData;
    private EditText description;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_description_vaga);

        findView();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
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
                if (validateDescription()) {
                    addJobDescription();
                }
            }
        });

        getJobDescriptionData();
    }

    private void getJobDescriptionData(){
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String descriptionVaga = documentSnapshot.getString(tag.getKEY_DESCRIPTION_VAGA());
                    description.setText(descriptionVaga);
                }
            }
        });
    }

    private void addJobDescription() {
        String descriptionVaga = description.getText().toString();
        Map<String, Object> vaga = new HashMap<>();
        vaga.put(tag.getKEY_DESCRIPTION_VAGA(), descriptionVaga);
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).update(vaga)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CompanyDescriptionActivity.this, "Dados Salvos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private boolean validateDescription() {
        TextInputLayout error_description = findViewById(R.id.error_description);
        String descriotion = error_description.getEditText().getText().toString();

        if (descriotion.isEmpty()) {
            error_description.setError("Preencha este campo");
            return false;
        } else {
            error_description.setError(null);
            return true;
        }
    }

    private void findView() {
        back = findViewById(R.id.back);
        saveData = findViewById(R.id.saveData);
        description = findViewById(R.id.description);
    }

}
