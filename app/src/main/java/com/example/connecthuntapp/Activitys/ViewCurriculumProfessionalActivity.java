package com.example.connecthuntapp.Activitys;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connecthuntapp.Adapters.ExperienceProfessionalAdapter;
import com.example.connecthuntapp.Models.ExperienceProfessional;
import com.example.connecthuntapp.Models.Language;
import com.example.connecthuntapp.Models.Skill;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ViewCurriculumProfessionalActivity extends AppCompatActivity {
    private ImageView back;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String user_id;
    private String history;
    private String view;
    private TextView goal, username;
    private Tags tag = new Tags();
    private ExperienceProfessionalAdapter adapter;
    private TextView tv_data, tv_skills, tv_language;
    private Button btn_enviar;
    private ImageView addComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_curriculum_professional);

        view = getIntent().getStringExtra("view_id");
        tv_data = findViewById(R.id.tv_data);
        tv_skills = findViewById(R.id.tv_skills);
        tv_language = findViewById(R.id.tv_language);
        addComment = findViewById(R.id.addComment);

        username = findViewById(R.id.username);
        goal = findViewById(R.id.goal);
        back = findViewById(R.id.back);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(ViewCurriculumProfessionalActivity.this);
                final View view_ = inflater.inflate(R.layout.alert_comment, null);

                final Button btn_send = view_.findViewById(R.id.btn_send);
                final Button cancelar = view_.findViewById(R.id.cancelar);
                final EditText comment = view_.findViewById(R.id.edt_comment);

                final AlertDialog alertDialog = new AlertDialog.Builder(ViewCurriculumProfessionalActivity.this)
                        .setView(view_)
                        .create();

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user_id = mAuth.getCurrentUser().getUid();
                        DocumentReference doc = firebaseFirestore.collection("Candidatos").document(view);
                        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        String name = task.getResult().getString("name");
                                        String id = task.getResult().getString("user_id");
                                        final String historyid = task.getResult().getString("history_id");

                                        firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo("user_id", id)
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                        String comentario = comment.getText().toString();
                                                        Map<String, Object> map = new HashMap<>();

                                                        map.put("Comment",comentario);

                                                        firebaseFirestore.collection(tag.getKEY_HISTORY()).document(historyid)
                                                                .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(ViewCurriculumProfessionalActivity.this,
                                                                        "Comentário enviado ao Profissional", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        applyCurriculumPreview();


        firebaseFirestore.collection("Candidatos").document(view)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        } else {
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                final String id = documentSnapshot.getString("user_id");
                                String user_name = documentSnapshot.getString("name");
                                username.setText(user_name);

                                String user_goal = documentSnapshot.getString("Goal");
                                goal.setText(user_goal);

                                firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(id)
                                        .collection(tag.getKEY_PROFESSIONAL_EXPERIENCE()).whereEqualTo("user_id", id)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String dataExperienceProfessional = "";
                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                Log.d("ID", queryDocumentSnapshot.getId());
                                                ExperienceProfessional exp = queryDocumentSnapshot.toObject(ExperienceProfessional.class);
                                                String name = exp.getCompanyName();
                                                String vaga_name = exp.getOffice();
                                                String description = exp.getDescription();
                                                String initialDate = exp.getInitialDate();
                                                String finalDate = exp.getFinalData();

                                                dataExperienceProfessional += "Name: " + name + "\nNome da Vaga: " + vaga_name + "\n" +
                                                        "Data Inicial: " + initialDate + "  Data Final: " + finalDate + "\n" +
                                                        "Descrição da Vaga: " + description + "\n\n";


                                            }
                                            tv_data.setText(dataExperienceProfessional);

                                        } else {
                                            Log.d(tag.getKEY_ERROR(), " No document");
                                        }
                                    }
                                });
                                firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(id).collection(tag.getKEY_SKILL())
                                        .whereEqualTo("user_id", id)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String dateSkills = "";
                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                Skill skill = queryDocumentSnapshot.toObject(Skill.class);
                                                String skill_name = skill.getSkillName();
                                                String skill_degree = skill.getSkillDegree();

                                                dateSkills += "Nome " + skill_name + "       Nível " + skill_degree + "\n";
                                            }
                                            tv_skills.setText(dateSkills);
                                        }
                                    }
                                });
                                firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(id).collection(tag.getKEY_LANGUAGE())
                                        .whereEqualTo("user_id", id)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String dataLanguage = "";
                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                Language language = queryDocumentSnapshot.toObject(Language.class);
                                                String language_name = language.getLanguage_name();
                                                String language_degree = language.getLanguage_level();

                                                dataLanguage += "Nome " + language_name + "                Nível " + language_degree + "\n";
                                            }
                                            tv_language.setText(dataLanguage);
                                        }
                                    }
                                });

                            }
                        }
                    }
                });



        /*back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        */

    }


    private void applyCurriculumPreview() {
        user_id = mAuth.getCurrentUser().getUid();
        DocumentReference doc = firebaseFirestore.collection("Candidatos").document(view);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String name = task.getResult().getString("name");
                        String id = task.getResult().getString("user_id");
                        final String historyid = task.getResult().getString("history_id");

                        firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo("user_id", id)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Map<String, Object> map = new HashMap<>();

                                        map.put("Situation", true);
                                        map.put("dateVisualized", FieldValue.serverTimestamp());
                                        firebaseFirestore.collection(tag.getKEY_HISTORY()).document(historyid)
                                                .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
