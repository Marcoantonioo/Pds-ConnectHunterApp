package com.example.connecthuntapp.Activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyCreateVagaActivity extends AppCompatActivity {
    private final Tags tag = new Tags();
    private CircleImageView logo_image;
    private ImageView btn_select_img, back;
    private TextView saveData, username;
    private EditText companyName, nameVaga, city, description, requeriment, minSalary, maxSalary;
    private ProgressBar progressBar;
    private CheckBox cb_trainee, cb_junior, cb_senior, cb_pleno;
    private Spinner spinnerUf;
    private String user_id;
    private Uri mSelectedUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_create_vaga);

        findView();

        firebaseConfig();

        maskFormatter();

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createVaga();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        getVagaData();

    }

    private void getVagaData() {
        user_id = mAuth.getCurrentUser().getUid();

        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id);
        doc.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {

                                String name = doc.getString(tag.getKEY_COMPANY_NAME());
                                username.setText(name);
                                companyName.setText(name);

                                String name_company = task.getResult().getString(tag.getKEY_NAME_COMPANY());
                                companyName.setText(name_company);

                                String name_vaga = task.getResult().getString(tag.getKEY_NAME_VAGA());
                                nameVaga.setText(name_vaga);

                                String city_vaga = task.getResult().getString(tag.getKEY_CITY_VAGA());
                                city.setText(city_vaga);

                                String description_vaga = task.getResult().getString(tag.getKEY_DESCRIPTION_VAGA());
                                description.setText(description_vaga);

                                String requeriment_vaga = task.getResult().getString(tag.getKEY_REQUERIMENT_VAGA());
                                requeriment.setText(requeriment_vaga);

                                String salary_max = task.getResult().getString(tag.getKEY_MAX_SALARY());
                                maxSalary.setText(salary_max);

                                String salary_min = task.getResult().getString(tag.getKEY_MIN_SALARY());
                                minSalary.setText(salary_min);


                                if (mSelectedUri != null) {
                                    String image = task.getResult().getString(tag.getKEY_PHOTO_LOGO());
                                    mSelectedUri = Uri.parse(image);
                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.drawable.profile);
                                    Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(image).into(logo_image);
                                } else {
                                    Log.e(tag.getKEY_ERROR(), "No Document here");

                                }
                            } else {
                                Log.e(tag.getKEY_ERROR(), "No Document here");
                            }
                        }
                        Log.e(tag.getKEY_ERROR(), "No Document here");
                    }
                });
    }


    private void createVaga() {
        user_id = mAuth.getCurrentUser().getUid();
        String name_company = companyName.getText().toString();
        String name_vaga = nameVaga.getText().toString();
        String city = this.city.getText().toString();
        String description = this.description.getText().toString();
        String requeriment = this.requeriment.getText().toString();
        String salary_max = maxSalary.getText().toString();
        String salary_min = minSalary.getText().toString();
        String spinner_uf = String.valueOf(spinnerUf.getSelectedItem());

        final boolean trainee = cb_trainee.isChecked();
        final boolean junior = cb_junior.isChecked();
        final boolean senior = cb_senior.isChecked();
        final boolean pleno = cb_pleno.isChecked();

        if (!TextUtils.isEmpty(name_company) && !TextUtils.isEmpty(name_vaga) && !TextUtils.isEmpty(city) &&
                !TextUtils.isEmpty(description) && !TextUtils.isEmpty(requeriment) && !TextUtils.isEmpty(salary_max) &&
                !TextUtils.isEmpty(salary_min) && !TextUtils.isEmpty(spinner_uf)) {
            Map<String, Object> map = new HashMap<>();

            map.put(tag.getKEY_USER_ID(), user_id);
            map.put(tag.getKEY_NAME_COMPANY(), name_company);
            map.put(tag.getKEY_UF(), spinner_uf);
            map.put(tag.getKEY_MAX_SALARY(), salary_max);
            map.put(tag.getKEY_MIN_SALARY(), salary_min);
            map.put(tag.getKEY_NAME_VAGA(), name_vaga);
            map.put(tag.getKEY_CITY_VAGA(), city);
            map.put(tag.getKEY_DESCRIPTION_VAGA(), description);
            map.put(tag.getKEY_REQUERIMENT_VAGA(), requeriment);
            map.put(tag.getKEY_TREINEE(), String.valueOf(trainee));
            map.put(tag.getKEY_JUNIOR(), String.valueOf(junior));
            map.put(tag.getKEY_SENIOR(), String.valueOf(senior));
            map.put(tag.getKEY_PLENO(), String.valueOf(pleno));

            firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).
                    set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Vaga Atualizada!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ERRO", "Erro", e);
                }
            });
        }else {
            Toast.makeText(this, "Preencha todos os Campos!", Toast.LENGTH_SHORT).show();
        }

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
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mSelectedUri);
                logo_image.setImageDrawable(new BitmapDrawable(bitmap));
                saveImage();
            } catch (Exception e) {

            }
        }
    }

    private void saveImage() {

        String fileName = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/logo_images/" + fileName);
        progressBar.setVisibility(View.VISIBLE);
        ref.putFile(mSelectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("Ok", uri.toString());

                                String url = uri.toString();

                                Map<String, Object> map = new HashMap<>();

                                map.put(tag.getKEY_PHOTO_LOGO(), url);

                                DocumentReference doc = firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id);
                                doc.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Upload Ok", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void maskFormatter() {
        //Mask For Mínimo Salario
        SimpleMaskFormatter smfMinSalario = new SimpleMaskFormatter("R$ N,NNN,NN");
        MaskTextWatcher mtwMinSalario = new MaskTextWatcher(minSalary, smfMinSalario);
        minSalary.addTextChangedListener(mtwMinSalario);
        //End Mask
        //Mask For Máximo Salario
        SimpleMaskFormatter smfMaxSalario = new SimpleMaskFormatter("R$ N,NNN,NN");
        MaskTextWatcher mtwMaxSalario = new MaskTextWatcher(maxSalary, smfMaxSalario);
        maxSalary.addTextChangedListener(mtwMaxSalario);
        //End Mask
    }

    private void findView() {
        back = findViewById(R.id.back);
        saveData = findViewById(R.id.saveData);
        btn_select_img = findViewById(R.id.btnSelectImg);
        nameVaga = findViewById(R.id.nameVaga);
        city = findViewById(R.id.city);
        description = findViewById(R.id.about_company);
        requeriment = findViewById(R.id.requeriment);
        spinnerUf = findViewById(R.id.spinnerUf);
        cb_trainee = findViewById(R.id.cb_trainee);
        cb_junior = findViewById(R.id.cb_junior);
        cb_senior = findViewById(R.id.cb_senior);
        cb_pleno = findViewById(R.id.cb_pleno);
        minSalary = findViewById(R.id.min_salary);
        maxSalary = findViewById(R.id.min_salary);
        companyName = findViewById(R.id.companyName);
        username = findViewById(R.id.username);
        logo_image = findViewById(R.id.logoImage);
        progressBar = findViewById(R.id.progressBar);
    }

    private void firebaseConfig() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
