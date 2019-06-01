package com.example.connecthuntapp.Activitys;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

import es.dmoral.toasty.Toasty;

public class DetailsJobActivity extends AppCompatActivity {
    private Tags tag = new Tags();
    private String vaga_id;
    private ImageView disponivel, inds, preenchida;
    private Button btn_candidatar, btnApplicationFalse;
    private TextView nameVaga, companyName, city, min_salary, max_salary, uf, description, vaga_req, about_company,benefit;
    private String history_id;
    private ImageView back;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LinearLayout linearLayout14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_job);


        vaga_id = getIntent().getStringExtra("vaga_id");
        findview();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_candidatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertApplyVaga();
            }
        });
        btnApplicationFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDeleteVaga();
            }
        });

        getVagaData();
    }


    private void alertDeleteVaga() {
        LayoutInflater inflater = LayoutInflater.from(DetailsJobActivity.this);
        final View view = inflater.inflate(R.layout.alert_cancel_vaga, null);
        final String user_id = mAuth.getCurrentUser().getUid();

        final Button btn_cancel_candi = view.findViewById(R.id.btn_cancel_candi);
        Button cancel = view.findViewById(R.id.cancel);

        final AlertDialog alertDialog = new AlertDialog.Builder(DetailsJobActivity.this)
                .setView(view)
                .create();

        btn_cancel_candi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CollectionReference collectionReference2 = firebaseFirestore.collection("Candidatos");
                final CollectionReference collectionReference = firebaseFirestore.collection(tag.getKEY_HISTORY());
                final Query query = collectionReference.whereEqualTo("vagaID", vaga_id)
                        .whereEqualTo("user_id", user_id);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Query query1 = collectionReference2.whereEqualTo("vagaID", vaga_id)
                                    .whereEqualTo("user_id", user_id);
                            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot documentSnapshot : task.getResult()){
                                            collectionReference2.document(documentSnapshot.getId()).delete();
                                        }
                                    }
                                }
                            });
                            for (DocumentSnapshot doc : task.getResult()) {
                                collectionReference.document(doc.getId()).delete();

                              //  Toasty.success(getApplicationContext(),"Candidatura Cancelada",Toasty.LENGTH_SHORT).show();
                                btn_candidatar.setVisibility(View.VISIBLE);
                                btn_cancel_candi.setVisibility(View.GONE);
                            }
                            for (DocumentSnapshot doc2 : task.getResult()){
                                collectionReference2.document(doc2.getId()).delete();
                            }
                        } else {
                            Log.d(tag.getKEY_ERROR(), "Error getting documents: ", task.getException());
                        }
                    }
                });
                alertDialog.dismiss();
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

    private void alertApplyVaga() {
        LayoutInflater inflater = LayoutInflater.from(DetailsJobActivity.this);
        final View view = inflater.inflate(R.layout.alert_apply_vaga, null);


        Button btn_apply = view.findViewById(R.id.btn_apply);
        Button cancel = view.findViewById(R.id.cancel);

        final AlertDialog alertDialog = new AlertDialog.Builder(DetailsJobActivity.this)
                .setView(view)
                .create();

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyVaga();
                alertDialog.dismiss();
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

    private void applyVaga() {
        final String user_id = mAuth.getCurrentUser().getUid();

        final DocumentReference documentReference = firebaseFirestore.collection(tag.getKEY_VAGA()).document(vaga_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        if (task.getResult().get(tag.getKEY_JOB_STATUS()).equals(tag.getKEY_AVAILABLE())) {
                            final String vaga_name = doc.getString(tag.getKEY_NAME_VAGA());
                            final String name_company = doc.getString(tag.getKEY_NAME_COMPANY());
                            final String name_city = doc.getString(tag.getKEY_CITY_VAGA());
                            final String salary_min = doc.getString(tag.getKEY_MIN_SALARY());
                            final String salary_max = doc.getString(tag.getKEY_MAX_SALARY());
                            final String city_uf = doc.getString(tag.getKEY_UF());
                            final String description_vaga = doc.getString(tag.getKEY_DESCRIPTION_VAGA());
                            final String req_vaga = doc.getString(tag.getKEY_REQUERIMENT_VAGA());
                            final String about = doc.getString(tag.getKEY_ABOUT_COMPANY());
                            final String logo = doc.getString(tag.getKEY_PHOTO_LOGO());


                            firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo("vagaID", vaga_id).whereEqualTo("user_id", user_id)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {

                                            final DocumentReference document = firebaseFirestore.collection(tag.getKEY_HISTORY()).document();
                                            final String history_id = document.getId();

                                            Map<String, Object> map = new HashMap<>();
                                            map.put("History_id", history_id);
                                            map.put(tag.getKEY_USER_ID(), user_id);
                                            map.put(tag.getKEY_VAGA_ID(), vaga_id);
                                            map.put(tag.getKEY_NAME_VAGA(), vaga_name);
                                            map.put(tag.getKEY_COMPANY_NAME(), name_company);
                                            map.put(tag.getKEY_CITY_VAGA(), name_city);
                                            map.put(tag.getKEY_MIN_SALARY(), salary_min);
                                            map.put(tag.getKEY_MAX_SALARY(), salary_max);
                                            map.put(tag.getKEY_UF(), city_uf);
                                            map.put(tag.getKEY_DESCRIPTION_VAGA(), description_vaga);
                                            map.put(tag.getKEY_REQUERIMENT_VAGA(), req_vaga);
                                            map.put(tag.getKEY_ABOUT_COMPANY(), about);
                                            map.put(tag.getKEY_PHOTO_LOGO(), logo);
                                            map.put(tag.getKEY_SITUATION(), false);
                                            map.put(tag.getKEY_FEEDBACK_COMMENT(),"");
                                            map.put("dateApplication", FieldValue.serverTimestamp());


                                            document.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                                                            .collection(tag.getKEY_GOAL()).document(user_id)
                                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot doc = task.getResult();
                                                                if (doc.exists()) {
                                                                    final String goal = doc.getString(tag.getKEY_GOAL());

                                                                    firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                                                                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                                                    if (e != null) {
                                                                                        Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                                                                                        return;
                                                                                    }
                                                                                    if (documentSnapshot != null && documentSnapshot.exists()) {

                                                                                        final String username = documentSnapshot.getString(tag.getKEY_NAME());
                                                                                        final String profileUrl = documentSnapshot.getString(tag.getKEY_PHOTO());
                                                                                        Map<String, Object> map = new HashMap<>();
                                                                                        map.put("history_id", history_id);
                                                                                        map.put(tag.getKEY_USER_ID(), user_id);
                                                                                        map.put(tag.getKEY_VAGA_ID(), vaga_id);
                                                                                        map.put(tag.getKEY_NAME(), username);
                                                                                        map.put(tag.getKEY_PHOTO(),profileUrl);
                                                                                        map.put(tag.getKEY_GOAL(), goal);

                                                                                        final DocumentReference document = firebaseFirestore.collection("Candidatos").document();
                                                                                        document.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                            });

                                        }
                                    } else {
                                        Toast.makeText(DetailsJobActivity.this, "nop document", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else if (task.getResult().get(tag.getKEY_JOB_STATUS()).equals(tag.getKEY_UNAVAILABLE())) {
                            Toast.makeText(getApplicationContext(), tag.getKEY_UNAVAILABLE(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), tag.getKEY_COMPLETED(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void getVagaData() {
        final Tags tag = new Tags();

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_VAGA()).document(vaga_id);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {

                        String vaga_name = doc.getString(tag.getKEY_NAME_VAGA());
                        nameVaga.setText(vaga_name);

                        String name_company = doc.getString(tag.getKEY_NAME_COMPANY());
                        companyName.setText(name_company);

                        String name_city = doc.getString(tag.getKEY_CITY_VAGA());
                        city.setText(name_city);

                        String salary_min = doc.getString(tag.getKEY_MIN_SALARY());
                        min_salary.setText(salary_min);

                        String salary_max = doc.getString(tag.getKEY_MAX_SALARY());
                        max_salary.setText(salary_max);

                        String city_uf = doc.getString(tag.getKEY_UF());
                        uf.setText(city_uf);

                        String description_vaga = doc.getString(tag.getKEY_DESCRIPTION_VAGA());
                        description.setText(description_vaga);

                        String req_vaga = doc.getString(tag.getKEY_REQUERIMENT_VAGA());
                        vaga_req.setText(req_vaga);

                        String about = doc.getString(tag.getKEY_ABOUT_COMPANY());
                        about_company.setText(about);

                        String benefit_vaga = doc.getString("benefit");
                        benefit.setText(benefit_vaga);


                        if (task.getResult().get(tag.getKEY_JOB_STATUS()).equals(tag.getKEY_AVAILABLE())) {
                            Log.d("TAG", "OK " + task.getResult().get(tag.getKEY_JOB_STATUS()));
                            disponivel.setVisibility(View.VISIBLE);
                            inds.setVisibility(View.INVISIBLE);
                            preenchida.setVisibility(View.INVISIBLE);
                        } else {
                            if (task.getResult().get(tag.getKEY_JOB_STATUS()).equals(tag.getKEY_UNAVAILABLE())) {
                                disponivel.setVisibility(View.INVISIBLE);
                                inds.setVisibility(View.VISIBLE);
                                preenchida.setVisibility(View.INVISIBLE);
                            } else {
                                if (task.getResult().get(tag.getKEY_JOB_STATUS()).equals(tag.getKEY_COMPLETED())) {
                                    disponivel.setVisibility(View.INVISIBLE);
                                    inds.setVisibility(View.INVISIBLE);
                                    preenchida.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                    } else {
                        Log.e(tag.getKEY_ERROR(), "No Document here");
                    }
                }
                Log.e(tag.getKEY_ERROR(), "No Document here");
            }
        });
    }

    private void findview() {
        nameVaga = findViewById(R.id.nameVaga);
        companyName = findViewById(R.id.companyName);
        city = findViewById(R.id.city);
        min_salary = findViewById(R.id.min_salary);
        max_salary = findViewById(R.id.max_salary);
        uf = findViewById(R.id.uf);
        back = findViewById(R.id.back);
        description = findViewById(R.id.description);
        vaga_req = findViewById(R.id.vaga_req);
        about_company = findViewById(R.id.about_company);
        btn_candidatar = findViewById(R.id.btnApplication);
        disponivel = findViewById(R.id.available);
        inds = findViewById(R.id.unavailable);
        preenchida = findViewById(R.id.completed);
        btnApplicationFalse = findViewById(R.id.btnApplicationFalse);
        linearLayout14 = findViewById(R.id.linearLayout14);
        benefit = findViewById(R.id.benefit);

    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection(tag.getKEY_HISTORY()).whereEqualTo("vagaID", vaga_id).whereEqualTo("user_id", user_id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            btn_candidatar.setVisibility(View.GONE);
                            btnApplicationFalse.setVisibility(View.VISIBLE);
                            linearLayout14.setBackgroundColor(Color.parseColor("#0069C0"));
                        }else {
                            linearLayout14.setBackgroundColor(Color.parseColor("#263238"));

                        }

                    }
                });

    }
}
