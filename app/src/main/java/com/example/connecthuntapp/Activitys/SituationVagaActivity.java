package com.example.connecthuntapp.Activitys;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class SituationVagaActivity extends AppCompatActivity {

    private Tags tag = new Tags();

    private String history_id;
    private LinearLayout linearLayout11;
    private ImageView checked, not_x, back,checked2,not_x2;
    private TextView finish, feedback,visualized, complete, text_visualized, aguardando, vagaName, companyName, description, aboutCompany, requirement, minSalary, maxSalary, time;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private TextView tv_empresa_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situation_vaga);

        history_id = getIntent().getStringExtra("history_id");

        findView();
        firebaseConfig();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getData();

    }

    private void getData() {
        final DocumentReference doc = firebaseFirestore.collection("History").document(history_id);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {

                    String name_company = documentSnapshot.getString(tag.getKEY_NAME_COMPANY());
                    companyName.setText(name_company);

                    String name_vaga = documentSnapshot.getString(tag.getKEY_NAME_VAGA());
                    vagaName.setText(name_vaga);

                    String description_vaga = documentSnapshot.getString(tag.getKEY_DESCRIPTION_VAGA());
                    description.setText(description_vaga);

                    String about_company = documentSnapshot.getString(tag.getKEY_ABOUT_COMPANY());
                    aboutCompany.setText(about_company);

                    String requirement_vaga = documentSnapshot.getString(tag.getKEY_REQUERIMENT_VAGA());
                    requirement.setText(requirement_vaga);

                    String min_salary = documentSnapshot.getString(tag.getKEY_MIN_SALARY());
                    minSalary.setText(min_salary);

                    String max_salary = documentSnapshot.getString(tag.getKEY_MAX_SALARY());
                    maxSalary.setText(max_salary);

                    String feedback_company = documentSnapshot.getString("Comment");
                    feedback.setText(feedback_company);

                    if (task.getResult().getBoolean("Situation").equals(true)) {
                        visualized.setTextColor(Color.parseColor("#009624"));
                        linearLayout11.setBackgroundColor(Color.parseColor("#009624"));
                        checked.setVisibility(View.VISIBLE);
                        not_x.setVisibility(View.INVISIBLE);
                        text_visualized.setTextColor(Color.parseColor("#009624"));
                    } else if (task.getResult().getBoolean("Situation").equals(false)) {
                        checked.setVisibility(View.GONE);
                        not_x.setVisibility(View.VISIBLE);
                    }
                    if (!task.getResult().getString("Comment").isEmpty()){
                        checked2.setVisibility(View.VISIBLE);
                        not_x2.setVisibility(View.INVISIBLE);
                        linearLayout11.setBackgroundColor(Color.parseColor("#009624"));
                        tv_empresa_feedback.setTextColor(Color.parseColor("#009624"));
                        feedback.setTextColor(Color.parseColor("#009624"));
                        finish.setVisibility(View.VISIBLE);
                    }else {
                        if (task.getResult().getString("Comment").isEmpty()){
                            checked2.setVisibility(View.INVISIBLE);
                            not_x2.setVisibility(View.VISIBLE);
                            linearLayout11.setBackgroundColor(Color.parseColor("#AAAAAA"));
                            tv_empresa_feedback.setTextColor(Color.parseColor("#AAAAAA"));
                            feedback.setTextColor(Color.parseColor("#AAAAAA"));
                            finish.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

    }

    private void findView() {
        companyName = findViewById(R.id.companyName);
        vagaName = findViewById(R.id.vagaName);
        linearLayout11 = findViewById(R.id.linearLayout1);
        checked = findViewById(R.id.checked);
        not_x = findViewById(R.id.not_x);
        checked2 = findViewById(R.id.checked2);
        not_x2 = findViewById(R.id.not_x2);
        visualized = findViewById(R.id.visualized);
        back = findViewById(R.id.back);
        description = findViewById(R.id.description);
        aboutCompany = findViewById(R.id.about_company);
        requirement = findViewById(R.id.requirement_vaga);
        minSalary = findViewById(R.id.min_salary);
        maxSalary = findViewById(R.id.max_salary);
        text_visualized = findViewById(R.id.text_visualized);
        feedback = findViewById(R.id.feedback);
        time = findViewById(R.id.time);
        tv_empresa_feedback = findViewById(R.id.tv_empresa_feedback);
        finish = findViewById(R.id.finish);

    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void firebaseConfig() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
}
