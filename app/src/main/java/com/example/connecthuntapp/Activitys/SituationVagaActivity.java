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

import com.example.connecthuntapp.Models.History;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class SituationVagaActivity extends AppCompatActivity {

    private Tags tag = new Tags();

    private String history_id;
    private LinearLayout collum_linear;
    private ImageView checked, not_checked, back, checked_2, not_checked_2, img_approved, img_refused;
    private TextView finish, feedback, visualized, vagaName, companyName, description, aboutCompany, requirement, minSalary, maxSalary, time;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private TextView tv_company_feedback, approved, refused, dateVisualized, dateComment, dateApply;

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


        firebaseFirestore.collection(tag.getKEY_HISTORY()).document(history_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot !=null && documentSnapshot.exists()) {
                            History history = documentSnapshot.toObject(History.class);

                            dateApply.setText(dateFormat(history.getDateApply()));
                        }else {
                            Log.d(tag.getKEY_ERROR(),"No document");
                        }
                    }
                });

        firebaseFirestore.collection(tag.getKEY_HISTORY()).document(history_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot !=null && documentSnapshot.exists()) {
                            if (!documentSnapshot.getString(tag.getKEY_FEEDBACK_COMMENT()).equals("")) {
                                History history = documentSnapshot.toObject(History.class);

                                dateComment.setText(dateFormat(history.getDateComment()));
                            } else {
                                Log.d(tag.getKEY_ERROR(), "No Document");

                            }
                        }
                    }
                });

        firebaseFirestore.collection(tag.getKEY_HISTORY()).document(history_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.getBoolean(tag.getKEY_SITUATION()).equals(true)) {
                        History history = doc.toObject(History.class);

                        dateVisualized.setText(dateFormat(history.getDateVisualized()));
                    } else {
                        Log.d(tag.getKEY_ERROR(), "No Document");

                    }
                } else {
                    Log.d(tag.getKEY_ERROR(), "Error");
                }
            }
        });


    }

    public static String dateFormat(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    private void getData() {
        final DocumentReference doc = firebaseFirestore.collection("History").document(history_id);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {


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

                    String feedback_company = documentSnapshot.getString(tag.getKEY_FEEDBACK_COMMENT());
                    feedback.setText(feedback_company);

                    if (documentSnapshot.getString(tag.getKEY_FEEDBACK_YES_NO()).equals("")) {
                        approved.setVisibility(View.GONE);
                        refused.setVisibility(View.GONE);
                        img_approved.setVisibility(View.GONE);
                        img_refused.setVisibility(View.GONE);
                    }
                    if (documentSnapshot.getString(tag.getKEY_FEEDBACK_YES_NO()).equals(tag.getKEY_APPROVED())) {
                        approved.setVisibility(View.VISIBLE);
                        refused.setVisibility(View.GONE);
                        img_approved.setVisibility(View.VISIBLE);
                        img_refused.setVisibility(View.GONE);
                    } else if (documentSnapshot.getString(tag.getKEY_FEEDBACK_YES_NO()).equals(tag.getKEY_REFUSED())) {
                        approved.setVisibility(View.GONE);
                        refused.setVisibility(View.VISIBLE);
                        img_approved.setVisibility(View.GONE);
                        img_refused.setVisibility(View.VISIBLE);
                    }

                    if (documentSnapshot.getBoolean(tag.getKEY_SITUATION()).equals(true)) {
                        visualized.setTextColor(Color.parseColor("#009624"));
                        collum_linear.setBackgroundColor(Color.parseColor("#009624"));
                        checked.setVisibility(View.VISIBLE);
                        not_checked.setVisibility(View.INVISIBLE);
                    } else if (documentSnapshot.getBoolean("Situation").equals(false)) {
                        checked.setVisibility(View.GONE);
                        not_checked.setVisibility(View.VISIBLE);
                    }
                    if (!documentSnapshot.getString("Comment").isEmpty()) {
                        checked_2.setVisibility(View.VISIBLE);
                        not_checked_2.setVisibility(View.INVISIBLE);
                        collum_linear.setBackgroundColor(Color.parseColor("#009624"));
                        tv_company_feedback.setTextColor(Color.parseColor("#009624"));
                        finish.setVisibility(View.VISIBLE);
                    } else {
                        if (documentSnapshot.getString("Comment").isEmpty()) {
                            checked_2.setVisibility(View.INVISIBLE);
                            not_checked_2.setVisibility(View.VISIBLE);
                            collum_linear.setBackgroundColor(Color.parseColor("#AAAAAA"));
                            tv_company_feedback.setTextColor(Color.parseColor("#AAAAAA"));
                            finish.setVisibility(View.GONE);
                        }
                    }
                } else {
                }
            }
        });

    }

    private void findView() {
        companyName = findViewById(R.id.companyName);
        vagaName = findViewById(R.id.vagaName);
        collum_linear = findViewById(R.id.linearLayout11);
        checked = findViewById(R.id.checked);
        not_checked = findViewById(R.id.not_x);
        checked_2 = findViewById(R.id.checked2);
        not_checked_2 = findViewById(R.id.not_x2);
        visualized = findViewById(R.id.visualized);
        back = findViewById(R.id.back);
        description = findViewById(R.id.description);
        aboutCompany = findViewById(R.id.about_company);
        requirement = findViewById(R.id.requirement_vaga);
        minSalary = findViewById(R.id.min_salary);
        maxSalary = findViewById(R.id.max_salary);
        feedback = findViewById(R.id.feedback);
        time = findViewById(R.id.time);
        tv_company_feedback = findViewById(R.id.tv_company_feedback);
        finish = findViewById(R.id.finish);
        refused = findViewById(R.id.refused);
        approved = findViewById(R.id.approved);
        img_refused = findViewById(R.id.img_refused);
        img_approved = findViewById(R.id.img_approved);
        dateVisualized = findViewById(R.id.dateVisualized);
        dateComment = findViewById(R.id.dateComment);
        dateApply = findViewById(R.id.dateApply);

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
