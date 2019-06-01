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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfessionalGoalActivity extends AppCompatActivity {
    private ImageView saveData, back;
    private String user_id;
    private Tags tag = new Tags();
    private EditText goal;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextInputLayout errorGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_goal);

        findView();
        firebaseConfig();

        getProfessionalGoal();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateGoal()) {
                    saveProfessionalGoal();
                }
            }
        });
    }

    private void getProfessionalGoal() {
        user_id = mAuth.getCurrentUser().getUid();

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_GOAL()).document(user_id);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String my_goal = task.getResult().getString(tag.getKEY_GOAL());
                        goal.setText(my_goal);
                    } else {
                        Log.d(tag.getKEY_ERROR(), "No documents found.");
                    }
                } else {
                    Log.d(tag.getKEY_ERROR(), "Failed ", task.getException());
                }
            }
        });
    }

    private boolean validateGoal(){
        String goal = errorGoal.getEditText().getText().toString();
        if (goal.isEmpty()) {
            errorGoal.setError("Este campo não pode ficar vazio");
            return false;
        }
        if (goal.length() < 20){
            errorGoal.setError("Mínimo 20 caracteres");
            return false;
        }else {
            errorGoal.setError(null);
            return true;
        }
    }

    private void firebaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void findView() {
        saveData = findViewById(R.id.saveData);
        goal = findViewById(R.id.goal);
        back = findViewById(R.id.back);
        errorGoal = findViewById(R.id.errorGoal);
    }

    private void saveProfessionalGoal() {
        user_id = mAuth.getCurrentUser().getUid();

        String my_goal = goal.getText().toString();

        Map<String, Object> userGoal = new HashMap<>();

        userGoal.put(tag.getKEY_GOAL(), my_goal);
        userGoal.put(tag.getKEY_USER_ID(), user_id);

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_GOAL()).document(user_id);

        doc.update(userGoal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Dados Atualizados", Toast.LENGTH_LONG).show();
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
}
