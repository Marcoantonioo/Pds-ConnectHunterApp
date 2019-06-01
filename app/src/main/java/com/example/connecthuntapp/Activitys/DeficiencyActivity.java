package com.example.connecthuntapp.Activitys;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DeficiencyActivity extends AppCompatActivity {

    private EditText edt_comment;
    private CheckBox cb_none, cb_intellectual, cb_physical, cb_hearing, cb_visual;
    private ImageView saveData, back;

    private String user_id;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    final Tags tag = new Tags();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deficiency);


        findview();
        firebaseConfig();

        checkboxConfig();

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDeficiencyData();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getDeficiencyData();

    }

    private void firebaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void findview() {
        cb_none = findViewById(R.id.cb_none);
        cb_visual = findViewById(R.id.cb_visual);
        cb_hearing = findViewById(R.id.cb_hearing);
        cb_physical = findViewById(R.id.cb_physical);
        cb_intellectual = findViewById(R.id.cb_intellectual);
        saveData = findViewById(R.id.saveData);
        back = findViewById(R.id.back);
        edt_comment = findViewById(R.id.edt_comment);
    }


    private void getDeficiencyData(){
        user_id = mAuth.getCurrentUser().getUid();

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_DEFICIENCY()).document(user_id);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String comment = documentSnapshot.getString(tag.getKEY_DEFICIENCY_COMMENT());
                    String visual = documentSnapshot.getString(tag.getKEY_DEFICIENCY_VISUAL());
                    String auditiva = documentSnapshot.getString(tag.getKEY_DEFICIENCY_HEARING());
                    String fisica = documentSnapshot.getString(tag.getKEY_DEFICIENCY_PHYSICAL());
                    String intelectual = documentSnapshot.getString(tag.getKEY_DEFICIENCY_INTELLECTUAL());
                    String naoPossuo = documentSnapshot.getString(tag.getKEY_DEFICIENCY_NONE());

                    edt_comment.setText(comment);

                    if (visual.equals(tag.getKEY_CHECK())) {
                        cb_visual.setChecked(true);
                    } else {
                        cb_visual.setChecked(false);
                    }
                    if (auditiva.equals(tag.getKEY_CHECK())) {
                        cb_hearing.setChecked(true);
                    } else {
                        cb_hearing.setChecked(false);
                    }
                    if (fisica.equals(tag.getKEY_CHECK())) {
                        cb_physical.setChecked(true);
                    } else {
                        cb_physical.setChecked(false);
                    }
                    if (intelectual.equals(tag.getKEY_CHECK())) {
                        cb_intellectual.setChecked(true);
                    } else {
                        cb_intellectual.setChecked(false);
                    }
                    if (naoPossuo.equals(tag.getKEY_CHECK())) {
                        cb_none.setChecked(true);
                    } else {
                        cb_none.setChecked(false);
                    }

                }
            }
        });

    }

    private void saveDeficiencyData() {
        user_id = mAuth.getCurrentUser().getUid();

        String comment = edt_comment.getText().toString();

        final boolean visual = cb_visual.isChecked();
        final boolean hearing = cb_hearing.isChecked();
        final boolean physical = cb_physical.isChecked();
        final boolean none = cb_none.isChecked();
        final boolean intellectual = cb_intellectual.isChecked();

        Map<String, String> map = new HashMap<>();

        map.put(tag.getKEY_DEFICIENCY_COMMENT(), comment);
        map.put(tag.getKEY_DEFICIENCY_VISUAL(), String.valueOf(visual));
        map.put(tag.getKEY_DEFICIENCY_HEARING(), String.valueOf(hearing));
        map.put(tag.getKEY_DEFICIENCY_PHYSICAL(), String.valueOf(physical));
        map.put(tag.getKEY_DEFICIENCY_INTELLECTUAL(), String.valueOf(intellectual));
        map.put(tag.getKEY_DEFICIENCY_NONE(), String.valueOf(none));

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .collection(tag.getKEY_DEFICIENCY()).document(user_id);
        doc.set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Deficiencia Atualizada", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(tag.getKEY_ERROR(), "Error", e);
            }
        });

    }

    private void checkboxConfig() {
        cb_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_none.isChecked()) {
                    cb_visual.setChecked(false);
                    cb_hearing.setChecked(false);
                    cb_physical.setChecked(false);
                    cb_intellectual.setChecked(false);
                }
            }
        });
        cb_visual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_visual.isChecked()) {
                    cb_none.setChecked(false);
                }
            }
        });
        cb_physical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_physical.isChecked()) {
                    cb_none.setChecked(false);
                }
            }
        });
        cb_hearing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_hearing.isChecked()) {
                    cb_none.setChecked(false);
                }
            }
        });
        cb_intellectual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_intellectual.isChecked()) {
                    cb_none.setChecked(false);
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
}
