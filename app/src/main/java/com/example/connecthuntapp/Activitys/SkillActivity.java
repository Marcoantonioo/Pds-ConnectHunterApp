package com.example.connecthuntapp.Activitys;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class SkillActivity extends AppCompatActivity {
    private Spinner spinner;
    private ImageView back;
    private ImageView saveData;
    private EditText edt_skill_name;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill);


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
                addSkill();
            }
        });

    }

    private void firebaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }


    private void findView() {
        back = findViewById(R.id.back);
        saveData = findViewById(R.id.saveData);
        edt_skill_name = findViewById(R.id.nomeCompetencia);
        spinner = findViewById(R.id.spinner1);

    }

    private void addSkill() {
        final Tags tag = new Tags();

        user_id = mAuth.getCurrentUser().getUid();

        String skillDegree = String.valueOf(spinner.getSelectedItem());
        String skillName = edt_skill_name.getText().toString();

        Map<String, String> skill = new HashMap<>();
        skill.put(tag.getKEY_SKILL_DEGREE(), skillDegree);
        skill.put(tag.getKEY_SKILL_NAME(), skillName);
        skill.put(tag.getKEY_USER_ID(), user_id);

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_SKILL()).document();
        doc.set(skill)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Habilidade Adicionada", Toast.LENGTH_LONG).show();
                        onBackPressed();
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
