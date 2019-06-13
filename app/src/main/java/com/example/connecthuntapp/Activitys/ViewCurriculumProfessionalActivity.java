package com.example.connecthuntapp.Activitys;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.connecthuntapp.Models.ExperienceProfessional;
import com.example.connecthuntapp.Models.Language;
import com.example.connecthuntapp.Models.Skill;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private Uri mSelectedUri;
    private TextView tv_goal, username;
    private Tags tag = new Tags();
    private TextView tv_data, tv_skills, tv_language;
    private ImageView btnAddComment, user_photo;

    private TextView tv_profession, tv_address, tv_cep, tv_city, tv_phone, tv_phone_residential, tv_birthday;
    private ImageView check_comment, not_check_comment;

    private RadioGroup rg_group;
    private RadioButton rb_options, rb_yes, rb_no;
    private String str_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_curriculum_professional);

        view = getIntent().getStringExtra("view_id");

        findView();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogComment();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        applyCurriculumPreview();

        getProfessionalCurriculum();

        checkComment();
        getEvaluate();

        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb_options = rg_group.findViewById(checkedId);
                switch (checkedId) {
                    case R.id.rb_yes:
                        if (rb_yes.isChecked()) {
                            str_type = rb_options.getText().toString();
                            evaluateCandidate();
                            break;
                        }
                        break;
                    case R.id.rb_no:
                        if (rb_no.isChecked()) {
                            str_type = rb_options.getText().toString();
                            evaluateCandidate();
                            break;
                        }
                        break;
                    default:
                }
            }
        });

    }

    private void getEvaluate() {
        user_id = mAuth.getCurrentUser().getUid();
        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_CANDIDATE()).document(view);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String id = task.getResult().getString(tag.getKEY_USER_ID());
                        final String history_id = task.getResult().getString("history_id");

                        firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo(tag.getKEY_USER_ID(), id)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                                            return;
                                        }
                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            if (doc.getString(tag.getKEY_FEEDBACK_YES_NO()).equals("")) {
                                                rb_no.setChecked(false);
                                                rb_yes.setChecked(false);
                                            } else {
                                                if (doc.getString(tag.getKEY_FEEDBACK_YES_NO()).equals(tag.getKEY_APPROVED())) {
                                                    rb_yes.setChecked(true);
                                                }
                                                if (doc.getString(tag.getKEY_FEEDBACK_YES_NO()).equals(tag.getKEY_REFUSED())) {
                                                    rb_no.setChecked(true);
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void evaluateCandidate() {
        user_id = mAuth.getCurrentUser().getUid();
        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_CANDIDATE()).document(view);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String id = task.getResult().getString(tag.getKEY_USER_ID());
                        final String history_id = task.getResult().getString("history_id");

                        firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo(tag.getKEY_USER_ID(), id)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Map<String, Object> map = new HashMap<>();

                                        map.put(tag.getKEY_FEEDBACK_YES_NO(), str_type);

                                        firebaseFirestore.collection(tag.getKEY_HISTORY()).document(history_id)
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

    private void findView() {
        rg_group = findViewById(R.id.rg_group);
        tv_data = findViewById(R.id.tv_data);
        tv_skills = findViewById(R.id.tv_skills);
        tv_language = findViewById(R.id.tv_language);
        btnAddComment = findViewById(R.id.addComment);
        username = findViewById(R.id.name);
        tv_goal = findViewById(R.id.tv_goal);
        back = findViewById(R.id.back);
        tv_profession = findViewById(R.id.tv_profession);
        tv_address = findViewById(R.id.tv_address);
        tv_cep = findViewById(R.id.tv_cep);
        tv_city = findViewById(R.id.tv_city);
        tv_phone = findViewById(R.id.tv_phone);
        tv_phone_residential = findViewById(R.id.tv_phone_residential);
        tv_birthday = findViewById(R.id.tv_birthday);
        user_photo = findViewById(R.id.user_photo);
        check_comment = findViewById(R.id.check_comment);
        not_check_comment = findViewById(R.id.not_check_comment);
        rb_yes = findViewById(R.id.rb_yes);
        rb_no = findViewById(R.id.rb_no);
    }

    private void getProfessionalCurriculum() {

        firebaseFirestore.collection(tag.getKEY_CANDIDATE()).document(view)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        } else {
                            if (documentSnapshot != null && documentSnapshot.getString(tag.getKEY_PHOTO()) != null && documentSnapshot.exists()) {
                                final String id = documentSnapshot.getString(tag.getKEY_USER_ID());
                                String nameUser = documentSnapshot.getString(tag.getKEY_NAME());
                                username.setText(nameUser);


                                if (user_photo != null) {
                                    String image = documentSnapshot.getString(tag.getKEY_PHOTO());
                                    mSelectedUri = Uri.parse(image);
                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.drawable.profile);
                                    Glide.with(ViewCurriculumProfessionalActivity.this).setDefaultRequestOptions(requestOptions).load(image).into(user_photo);
                                } else {
                                    Log.d(tag.getKEY_ERROR(), "No documents found.");
                                }

                                getProfessionalPersonalData(id);
                                getProfessionalGoal(id);
                                getProfessionalExperience(id);
                                getProfessionalSkill(id);
                                getProfessionalLanguage(id);

                            }
                        }
                    }
                });
    }

    private void getProfessionalGoal(String id) {
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(id)
                .collection(tag.getKEY_GOAL())
                .whereEqualTo(tag.getKEY_USER_ID(), id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc != null) {
                                String goal_professional = doc.getString(tag.getKEY_GOAL());
                                tv_goal.setText(goal_professional);
                            }
                        }
                    }
                });
    }

    private void getProfessionalPersonalData(String id) {
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(id)
                .collection(tag.getKEY_PERSONAL_DATA())
                .whereEqualTo(tag.getKEY_USER_ID(), id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc != null) {
                                String address = doc.getString(tag.getKEY_ADDRESS());
                                tv_address.setText(address);
                                String birthday = doc.getString(tag.getKEY_BIRTH());
                                tv_birthday.setText(birthday);
                                String cep = doc.getString(tag.getKEY_CEP());
                                tv_cep.setText(cep);
                                String city = doc.getString(tag.getKEY_CITY());
                                tv_city.setText(city);
                                String phone = doc.getString(tag.getKEY_PHONE());
                                tv_phone.setText(phone);
                                String phone_residential = doc.getString(tag.getKEY_PHONE_RESIDENTIAL());
                                tv_phone_residential.setText(phone_residential);
                                String profession = doc.getString(tag.getKEY_PROFESSION());
                                tv_profession.setText(profession);
                            }
                        }
                    }
                });

    }

    private void getProfessionalLanguage(String id) {
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

    private void getProfessionalSkill(String id) {
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
    }

    private void getProfessionalExperience(String id) {
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
                                "Descrição da Vaga: " + description + "\n" +
                                "-----------------------------------------" +
                                "-------------------------" + "\n";


                    }
                    tv_data.setText(dataExperienceProfessional);

                } else {
                    Log.d(tag.getKEY_ERROR(), " No document");
                }
            }
        });
    }

    private void checkComment() {

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_CANDIDATE()).document(view);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String professional_id = task.getResult().getString(tag.getKEY_USER_ID());
                        firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo(tag.getKEY_USER_ID(), professional_id)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                        if (e != null) {
                                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                                            return;
                                        }

                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            if (doc.getString("Comment").isEmpty()) {
                                                check_comment.setVisibility(View.GONE);
                                                not_check_comment.setVisibility(View.VISIBLE);
                                            } else {
                                                check_comment.setVisibility(View.VISIBLE);
                                                not_check_comment.setVisibility(View.GONE);
                                            }

                                        }
                                    }
                                });

                    }
                }
            }
        });
    }

    private void dialogComment() {
        LayoutInflater inflater = LayoutInflater.from(ViewCurriculumProfessionalActivity.this);
        final View view_ = inflater.inflate(R.layout.alert_comment, null);

        final Button btn_send = view_.findViewById(R.id.btn_send);
        final Button cancelar = view_.findViewById(R.id.cancel);
        final EditText comment = view_.findViewById(R.id.edt_comment);
        final AlertDialog alertDialog = new AlertDialog.Builder(ViewCurriculumProfessionalActivity.this)
                .setView(view_)
                .create();
        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_CANDIDATE()).document(view);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String professional_id = task.getResult().getString(tag.getKEY_USER_ID());
                        firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo(tag.getKEY_USER_ID(), professional_id)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        String company_comment = documentSnapshot.getString("Comment");
                                        comment.setText(company_comment);
                                    }
                                }
                            }
                        });
                    }

                }
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = mAuth.getCurrentUser().getUid();
                DocumentReference doc = firebaseFirestore.collection(tag.getKEY_CANDIDATE()).document(view);
                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                String name = task.getResult().getString(tag.getKEY_NAME());
                                String professional_id = task.getResult().getString(tag.getKEY_USER_ID());
                                final String history_id = task.getResult().getString("history_id");


                                firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo(tag.getKEY_USER_ID(), professional_id)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                                                String feedback = comment.getText().toString();

                                                Map<String, Object> map = new HashMap<>();

                                                map.put(tag.getKEY_FEEDBACK_COMMENT(), feedback);
                                                map.put("dateComment", FieldValue.serverTimestamp());

                                                firebaseFirestore.collection(tag.getKEY_HISTORY()).document(history_id)
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

        alertDialog.getWindow().

                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void applyCurriculumPreview() {
        user_id = mAuth.getCurrentUser().getUid();
        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_CANDIDATE()).document(view);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String id = task.getResult().getString(tag.getKEY_USER_ID());
                        final String history_id = task.getResult().getString("history_id");

                        firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo(tag.getKEY_USER_ID(), id)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("Situation", true);
                                    map.put("dateVisualized", FieldValue.serverTimestamp());

                                    firebaseFirestore.collection(tag.getKEY_HISTORY()).document(history_id)
                                            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

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
