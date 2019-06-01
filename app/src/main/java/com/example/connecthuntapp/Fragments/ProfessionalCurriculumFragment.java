package com.example.connecthuntapp.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.connecthuntapp.Activitys.DeficiencyActivity;
import com.example.connecthuntapp.Activitys.ExperienceProfessionalActivity;
import com.example.connecthuntapp.Activitys.ProfessionalGoalActivity;
import com.example.connecthuntapp.Activitys.ProfessionalLanguageActivity;
import com.example.connecthuntapp.Activitys.ProfessionalPersonalDataActivity;
import com.example.connecthuntapp.Activitys.SkillActivity;
import com.example.connecthuntapp.Adapters.ExperienceProfessionalAdapter;
import com.example.connecthuntapp.Adapters.LanguageAdapter;
import com.example.connecthuntapp.Adapters.SkillAdapter;
import com.example.connecthuntapp.Models.ExperienceProfessional;
import com.example.connecthuntapp.Models.Language;
import com.example.connecthuntapp.Models.Skill;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfessionalCurriculumFragment extends Fragment {

    private Tags tag = new Tags();
    private String user_id;
    private ImageView edit, edit_personal, addExp, addSkill, deficiency, addLanguage;
    private TextView myGoal, phone, city, email;
    private TextView naoPossuo, fisica, intelectual, auditiva, visual, comment, tv_alert, tv_alert_personal_data,
            tv_alert_experience, tv_alert_skill, tv_alert_deficiency, tv_alert_language;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ExperienceProfessionalAdapter experienceAdapter;
    private SkillAdapter skillAdapter;
    private LanguageAdapter languageAdapter;

    private Uri mSelectedUri;
    private ImageView btn_select_img;
    private CircleImageView img_select;
    private ProgressBar progressBar;

    private ImageView alert, alert_personal_data, alert_experience, alert_skill, alert_deficiency, alert_language;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_professional_curriculum, container, false);

        findView(v);
        firebaseConfig();

        getProfessionalGoal();
        getProfessionalPersonalData();

        user_id = mAuth.getCurrentUser().getUid();
        currentUser = mAuth.getCurrentUser();
        email.setText(currentUser.getEmail());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToProfessionalGoal();
            }
        });
        edit_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToProfessionalPersonData();
            }
        });
        addExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToExperienceProfessional();
            }
        });

        addSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToSkill();
            }
        });

        deficiency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToDeficiency();
            }
        });

        addLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLanguage();
            }
        });

        btn_select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStorage();
            }
        });

        getDeficiencyData();
        getProfessionalPersonalData();
        getProfessionalData();
        setupRecyclerViewExperience(v);
        setupRecyclerViewSkill(v);
        setupRecyclerViewLanguage(v);


        setAlertNotifyProfessionalGoal();
        setAlertNotifyProfessionalPersonalData();
        setAlertNotifyProfessionalExperience();
        setAlertNotifyProfessionalSkill();
        setAlertNotifyProfessionalDeficiency();
        setAlertNotifyProfessionalLanguage();

        return v;
    }

    private void setAlertNotifyProfessionalLanguage() {
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id).collection(tag.getKEY_LANGUAGE()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (queryDocumentSnapshots.isEmpty()) {
                    alert_language.setVisibility(View.VISIBLE);
                    tv_alert_language.setVisibility(View.VISIBLE);
                } else {
                    alert_language.setVisibility(View.GONE);
                    tv_alert_language.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setAlertNotifyProfessionalDeficiency() {
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id).collection(tag.getKEY_DEFICIENCY()).document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                alert_deficiency.setVisibility(View.GONE);
                                tv_alert_deficiency.setVisibility(View.GONE);
                            } else {
                                alert_deficiency.setVisibility(View.VISIBLE);
                                tv_alert_deficiency.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    private void setAlertNotifyProfessionalSkill() {
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id).collection(tag.getKEY_SKILL()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (queryDocumentSnapshots.isEmpty()) {
                    alert_skill.setVisibility(View.VISIBLE);
                    tv_alert_skill.setVisibility(View.VISIBLE);
                } else {
                    alert_skill.setVisibility(View.GONE);
                    tv_alert_skill.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setAlertNotifyProfessionalExperience() {
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id).collection(tag.getKEY_PROFESSIONAL_EXPERIENCE()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (queryDocumentSnapshots.isEmpty()) {
                    alert_experience.setVisibility(View.VISIBLE);
                    tv_alert_experience.setVisibility(View.VISIBLE);
                } else {
                    alert_experience.setVisibility(View.GONE);
                    tv_alert_experience.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setAlertNotifyProfessionalPersonalData() {
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id).collection(tag.getKEY_PERSONAL_DATA()).document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            if (documentSnapshot.getString(tag.getKEY_PROFESSION()).equals("") || documentSnapshot.getString(tag.getKEY_CITY()).equals("") ||
                                    documentSnapshot.getString(tag.getKEY_ADDRESS()).equals("") || documentSnapshot.getString(tag.getKEY_PHONE()).equals("") ||
                                    documentSnapshot.getString(tag.getKEY_PHONE_RESIDENTIAL()).equals("") || documentSnapshot.getString(tag.getKEY_BIRTH()).equals("") ||
                                    documentSnapshot.getString(tag.getKEY_CEP()).equals("")) {
                                alert_personal_data.setVisibility(View.VISIBLE);
                                tv_alert_personal_data.setVisibility(View.VISIBLE);
                            } else if (!documentSnapshot.getString(tag.getKEY_PROFESSION()).equals("") && !documentSnapshot.getString(tag.getKEY_CITY()).equals("") &&
                                    !documentSnapshot.getString(tag.getKEY_ADDRESS()).equals("") && !documentSnapshot.getString(tag.getKEY_PHONE_RESIDENTIAL()).equals("") &&
                                    !documentSnapshot.getString(tag.getKEY_PHONE_RESIDENTIAL()).equals("") && !documentSnapshot.getString(tag.getKEY_BIRTH()).equals("") &&
                                    !documentSnapshot.getString(tag.getKEY_CEP()).equals("")) {
                                alert_personal_data.setVisibility(View.GONE);
                                tv_alert_personal_data.setVisibility(View.GONE);
                            }


                        }
                    }
                });

    }

    private void setAlertNotifyProfessionalGoal() {
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id).collection(tag.getKEY_GOAL()).document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            if (documentSnapshot.getString(tag.getKEY_GOAL()).equals("")) {
                                alert.setVisibility(View.VISIBLE);
                                tv_alert.setVisibility(View.VISIBLE);
                            } else if (!documentSnapshot.getString(tag.getKEY_GOAL()).equals("")) {
                                alert.setVisibility(View.GONE);
                                tv_alert.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void setupRecyclerViewLanguage(View v) {

        final Tags tag = new Tags();

        user_id = mAuth.getCurrentUser().getUid();

        Query query = db.collection(tag.getKEY_PROFESSIONAL()).document(user_id).collection(tag.getKEY_LANGUAGE())
                .whereEqualTo(tag.getKEY_USER_ID(), user_id);
        FirestoreRecyclerOptions<Language> options = new FirestoreRecyclerOptions.Builder<Language>()
                .setQuery(query, Language.class)
                .build();

        languageAdapter = new LanguageAdapter(options);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_language);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(languageAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                languageAdapter.deleteItem(viewHolder.getAdapterPosition());
                Toast.makeText(getContext(), "Idioma deletado", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);
        languageAdapter.setOnItemClickListener(new LanguageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot doc, int position) {

            }
        });

    }

    private void setupRecyclerViewExperience(View v) {
        final Tags tag = new Tags();

        user_id = mAuth.getCurrentUser().getUid();

        Query query = db.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_PROFESSIONAL_EXPERIENCE()).whereEqualTo(tag.getKEY_USER_ID(), user_id);
        FirestoreRecyclerOptions<ExperienceProfessional> options = new FirestoreRecyclerOptions.Builder<ExperienceProfessional>()
                .setQuery(query, ExperienceProfessional.class)
                .build();
        experienceAdapter = new ExperienceProfessionalAdapter(options);
        RecyclerView recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(experienceAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(final @NonNull RecyclerView.ViewHolder viewHolder, int i) {

                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View view = inflater.inflate(R.layout.alert_ask, null);

                Button excluir = view.findViewById(R.id.excluir);
                Button cancel = view.findViewById(R.id.cancelar);

                final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setView(view)
                        .create();

                excluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        experienceAdapter.deleteItem(viewHolder.getAdapterPosition());
                        Toast.makeText(getContext(), "Experiencia deletada", Toast.LENGTH_LONG).show();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);
        experienceAdapter.setOnItemClickListener(new ExperienceProfessionalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot doc, int position) {

            }
        });
    }

    private void setupRecyclerViewSkill(View v) {
        final Tags tag = new Tags();

        String user_id = mAuth.getCurrentUser().getUid();

        Query query = db.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_SKILL()).whereEqualTo(tag.getKEY_USER_ID(), user_id);
            FirestoreRecyclerOptions<Skill> options = new FirestoreRecyclerOptions.Builder<Skill>()
                    .setQuery(query, Skill.class)
                    .build();
        skillAdapter = new SkillAdapter(options);
        RecyclerView recyclerView = v.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(skillAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                skillAdapter.deleteItem(viewHolder.getAdapterPosition());
                Toast.makeText(getContext(), "Habilidade deletada", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);
        skillAdapter.setOnItemClickListener(new SkillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot doc, int position) {

            }
        });
    }

    private void sendToExperienceProfessional() {
        Intent intent = new Intent(getContext(), ExperienceProfessionalActivity.class);
        startActivity(intent);
    }

    private void sendToProfessionalPersonData() {
        Intent intent = new Intent(getContext(), ProfessionalPersonalDataActivity.class);
        startActivity(intent);
    }

    private void getProfessionalData() {
        String user_id = mAuth.getCurrentUser().getUid();

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.getString(tag.getKEY_PHOTO()) != null && documentSnapshot.exists()) {

                    if (img_select != null) {
                        String image = documentSnapshot.getString(tag.getKEY_PHOTO());
                        mSelectedUri = Uri.parse(image);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.profile);
                        Glide.with(ProfessionalCurriculumFragment.this).setDefaultRequestOptions(requestOptions).load(image).into(img_select);
                    } else {
                        Log.d(tag.getKEY_ERROR(), "No documents found.");
                    }
                } else {
                    Log.d(tag.getKEY_ERROR(), "No documents found.");
                }
            }
        });

    }

    private void getProfessionalGoal() {
        user_id = mAuth.getCurrentUser().getUid();

        final DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_GOAL()).document(user_id);

        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String my_goal = documentSnapshot.getString(tag.getKEY_GOAL());
                    myGoal.setText(my_goal);
                }
            }
        });
    }

    private void getProfessionalPersonalData() {

        final DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_PERSONAL_DATA()).document(user_id);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String professional_city = documentSnapshot.getString(tag.getKEY_CITY());
                    city.setText(professional_city);
                    String professional_phone = documentSnapshot.getString(tag.getKEY_PHONE());
                    phone.setText(professional_phone);
                }
            }
        });

    }

    private void getDeficiencyData() {
        String user_id = mAuth.getCurrentUser().getUid();

        final DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_DEFICIENCY()).document(user_id);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen Failed", e);
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String comment_def = documentSnapshot.getString(tag.getKEY_DEFICIENCY_COMMENT());
                    comment.setText(comment_def);
                    comment.setTextColor(Color.parseColor("#1a237e"));

                    String visual_def = documentSnapshot.getString(tag.getKEY_DEFICIENCY_VISUAL());
                    if (visual_def.equals(tag.getKEY_CHECK())) {
                        visual.setTextColor(Color.parseColor("#1a237e"));
                    } else {
                        visual.setTextColor(Color.parseColor("#DDDDDD"));
                    }

                    String auditiva_def = documentSnapshot.getString(tag.getKEY_DEFICIENCY_HEARING());
                    if (auditiva_def.equals(tag.getKEY_CHECK())) {
                        auditiva.setTextColor(Color.parseColor("#1a237e"));
                    } else {
                        auditiva.setTextColor(Color.parseColor("#DDDDDD"));
                    }

                    String fisica_def = documentSnapshot.getString(tag.getKEY_DEFICIENCY_PHYSICAL());
                    if (fisica_def.equals(tag.getKEY_CHECK())) {
                        fisica.setTextColor(Color.parseColor("#1a237e"));
                    } else {
                        fisica.setTextColor(Color.parseColor("#DDDDDD"));
                    }

                    String intelectual_def = documentSnapshot.getString(tag.getKEY_DEFICIENCY_INTELLECTUAL());
                    if (intelectual_def.equals(tag.getKEY_CHECK())) {
                        intelectual.setTextColor(Color.parseColor("#1a237e"));
                    } else {
                        intelectual.setTextColor(Color.parseColor("#DDDDDD"));
                    }

                    String naoPossuo_def = documentSnapshot.getString(tag.getKEY_DEFICIENCY_NONE());
                    if (naoPossuo_def.equals(tag.getKEY_CHECK())) {
                        naoPossuo.setTextColor(Color.parseColor("#1a237e"));
                    } else {
                        naoPossuo.setTextColor(Color.parseColor("#DDDDDD"));
                    }

                }
            }
        });
    }

    private void sendToProfessionalGoal() {
        Intent intent = new Intent(getContext(), ProfessionalGoalActivity.class);
        startActivity(intent);
    }

    private void sendToDeficiency() {
        Intent intent = new Intent(getContext(), DeficiencyActivity.class);
        startActivity(intent);
    }

    private void sendToSkill() {
        Intent intent = new Intent(getContext(), SkillActivity.class);
        startActivity(intent);
    }

    private void sendToLanguage() {
        Intent intent = new Intent(getContext(), ProfessionalLanguageActivity.class);
        startActivity(intent);
    }

    private void firebaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void findView(View v) {
        edit = v.findViewById(R.id.addBasicInfo);
        myGoal = v.findViewById(R.id.myGoal);
        edit_personal = v.findViewById(R.id.edit_personal);
        phone = v.findViewById(R.id.phone);
        email = v.findViewById(R.id.email);
        city = v.findViewById(R.id.city);
        addExp = v.findViewById(R.id.addExp);
        addSkill = v.findViewById(R.id.addSkill);
        deficiency = v.findViewById(R.id.deficiency);
        fisica = v.findViewById(R.id.fisica);
        auditiva = v.findViewById(R.id.auditiva);
        visual = v.findViewById(R.id.visual);
        intelectual = v.findViewById(R.id.intelectual);
        naoPossuo = v.findViewById(R.id.naoPossuo);
        comment = v.findViewById(R.id.comment);
        btn_select_img = v.findViewById(R.id.btnSelectImg);
        img_select = v.findViewById(R.id.photo);
        progressBar = v.findViewById(R.id.progressBar);
        alert = v.findViewById(R.id.alert);
        tv_alert = v.findViewById(R.id.tv_alert);
        alert_personal_data = v.findViewById(R.id.alert_personal_data);
        tv_alert_personal_data = v.findViewById(R.id.tv_alert_personal_data);
        tv_alert_experience = v.findViewById(R.id.tv_alert_experience);
        alert_experience = v.findViewById(R.id.alert_experience);
        tv_alert_skill = v.findViewById(R.id.tv_alert_skill);
        alert_skill = v.findViewById(R.id.alert_skill);
        tv_alert_deficiency = v.findViewById(R.id.tv_alert_deficiency);
        alert_deficiency = v.findViewById(R.id.alert_deficiency);
        addLanguage = v.findViewById(R.id.addLanguage);
        alert_language = v.findViewById(R.id.alert_language);
        tv_alert_language = v.findViewById(R.id.tv_alert_language);
    }

    private void saveImage() {
        final Tags tag = new Tags();

        final String user_id = mAuth.getCurrentUser().getUid();

        String fileName = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + fileName);
        progressBar.setVisibility(View.VISIBLE);
        ref.putFile(mSelectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("Ok", uri.toString());

                                String profile_url = uri.toString();

                                Map<String, Object> map = new HashMap<>();

                                map.put(tag.getKEY_PHOTO(), profile_url);

                                DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id);
                                doc.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Upload Ok", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            mSelectedUri = data.getData();

            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mSelectedUri);
                img_select.setImageDrawable(new BitmapDrawable(bitmap));
                saveImage();
            } catch (Exception e) {
                startActivity(new Intent(getContext(), ProfessionalHomeFragment.class));
            }
        }
    }

    private void openStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {
                selectPhoto();
            }
        } else {
            selectPhoto();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        experienceAdapter.startListening();
        skillAdapter.startListening();
        languageAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        experienceAdapter.stopListening();
        skillAdapter.stopListening();
        languageAdapter.stopListening();
    }

}
