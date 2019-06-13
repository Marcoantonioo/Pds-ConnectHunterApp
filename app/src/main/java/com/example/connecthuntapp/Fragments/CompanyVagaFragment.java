package com.example.connecthuntapp.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.connecthuntapp.Activitys.CompanyAboutActivity;
import com.example.connecthuntapp.Activitys.CompanyDescriptionActivity;
import com.example.connecthuntapp.Activitys.CompanyRequirementActivity;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


public class CompanyVagaFragment extends Fragment {

    private CharSequence[] values = {"Disponivel", "Preenchida", "Indisponivel no Momento"};
    private AlertDialog alertDialog1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private Tags tag = new Tags();
    private CircleImageView logo_image;
    private ImageView btn_select_img;
    private Uri mSelectedUri;
    private ProgressBar progressBar;

    private ImageView addRequeriment, addDescription, addBasicInfo, addSalary, addAboutCompany,
            alert, alert_sucess, alert_sucess_about, alert_about, alert_sucess_salary, alert_salary,
            alert_sucess_description, alert_description, alert_sucess_requirement, alert_requirement, alert_status,
            available, unavailable, completed;
    private TextView city, job_name, company_name, max_salary, min_salary, benefit, description, requirement, about_company, status;

    private CheckBox cb_show;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_company_vaga, container, false);


        findView(v);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        alert_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertStatus(v);
            }
        });
        addAboutCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAddAboutCompany();
            }
        });
        addRequeriment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAddRequirement();
            }
        });
        addBasicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInfoBasic(v);
            }
        });
        addSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInfoSalary(v);
            }
        });
        addDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAddDescription();
            }
        });
        btn_select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        getInfoBasicData();
        getSalaryData();
        getDescriptionData();
        getRequirementData();
        getAboutData();
        getStatusData();
        getPhotoLogo();
        getJobData();


        setAlertBasicInfo();
        setAlertAbout();
        setAlertSalary();
        setAlertDescription();
        setAlertRequirement();

        cb_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShowJob();
            }
        });


        return v;
    }

    private void getJobData() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        if (doc.getBoolean(tag.getKEY_SHOW_JOB()).equals(true)) {
                            cb_show.setChecked(true);
                        } else {
                            cb_show.setChecked(false);
                        }
                    }else {
                        Log.d(tag.getKEY_ERROR(),"No document");
                    }
                }
            }
        });
    }

    private void setShowJob(){
        final boolean show = cb_show.isChecked();

        Map<String, Object> map = new HashMap<>();

        map.put(tag.getKEY_SHOW_JOB(),show);

        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }


    private void getPhotoLogo() {
        DocumentReference doc = firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.getString(tag.getKEY_PHOTO_LOGO()) != null && documentSnapshot.exists()) {

                    if (logo_image != null) {
                        String image = documentSnapshot.getString(tag.getKEY_PHOTO_LOGO());
                        mSelectedUri = Uri.parse(image);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.profile);
                        Glide.with(CompanyVagaFragment.this).setDefaultRequestOptions(requestOptions).load(image).into(logo_image);
                    } else {
                        Log.d(tag.getKEY_ERROR(), "No documents found.");
                    }
                } else {
                    Log.d(tag.getKEY_ERROR(), "No documents found.");
                }
            }

        });
    }

    private void getStatusData() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String vaga_status = documentSnapshot.getString(tag.getKEY_JOB_STATUS());
                    status.setText(vaga_status);

                    if (documentSnapshot.getString(tag.getKEY_JOB_STATUS()) == null) {
                        available.setVisibility(View.GONE);
                        unavailable.setVisibility(View.GONE);
                        completed.setVisibility(View.GONE);
                    } else {
                        if (documentSnapshot.getString(tag.getKEY_JOB_STATUS()).equals("Disponivel")) {
                            Log.d("TAG", "OK " + documentSnapshot.get(tag.getKEY_JOB_STATUS()));
                            available.setVisibility(View.VISIBLE);
                            unavailable.setVisibility(View.GONE);
                            completed.setVisibility(View.GONE);
                        } else {
                            if (documentSnapshot.getString(tag.getKEY_JOB_STATUS()).equals("Indisponivel no Momento")) {
                                available.setVisibility(View.GONE);
                                unavailable.setVisibility(View.VISIBLE);
                                completed.setVisibility(View.GONE);
                            } else {
                                if (documentSnapshot.getString(tag.getKEY_JOB_STATUS()).equals("Preenchida")) {
                                    available.setVisibility(View.GONE);
                                    unavailable.setVisibility(View.GONE);
                                    completed.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void alertStatus(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Escolha um Status");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Map<String, Object> map_1 = new HashMap<>();
                        map_1.put(tag.getKEY_JOB_STATUS(), values[0]);
                        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id)
                                .update(map_1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Status de difinido como: " + values[0], Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case 1:
                        Map<String, Object> map_2 = new HashMap<>();
                        map_2.put(tag.getKEY_JOB_STATUS(), values[1]);
                        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id)
                                .update(map_2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Status de difinido como: " + values[1], Toast.LENGTH_SHORT).show();

                            }
                        });
                        break;
                    case 2:
                        Map<String, Object> map_3 = new HashMap<>();
                        map_3.put(tag.getKEY_JOB_STATUS(), values[2]);
                        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id)
                                .update(map_3).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Status de difinido como: " + values[2], Toast.LENGTH_SHORT).show();

                            }
                        });
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    private void setAlertRequirement() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot.getString(tag.getKEY_REQUERIMENT_VAGA()) == null) {
                            alert_requirement.setVisibility(View.VISIBLE);
                            alert_sucess_requirement.setVisibility(View.GONE);
                        } else {
                            if (documentSnapshot.getString(tag.getKEY_REQUERIMENT_VAGA()) != null) {
                                alert_requirement.setVisibility(View.GONE);
                                alert_sucess_requirement.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                });
    }

    private void setAlertDescription() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot.getString(tag.getKEY_DESCRIPTION_VAGA()) == null) {
                            alert_description.setVisibility(View.VISIBLE);
                            alert_sucess_description.setVisibility(View.GONE);
                        } else {
                            if (documentSnapshot.getString(tag.getKEY_DESCRIPTION_VAGA()) != null) {
                                alert_description.setVisibility(View.GONE);
                                alert_sucess_description.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                });
    }

    private void setAlertSalary() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot.getString(tag.getKEY_MIN_SALARY()) == null) {
                            alert_salary.setVisibility(View.VISIBLE);
                            alert_sucess_salary.setVisibility(View.GONE);
                        } else {
                            if (documentSnapshot.getString(tag.getKEY_MIN_SALARY()) != null) {
                                alert_salary.setVisibility(View.GONE);
                                alert_sucess_salary.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                });
    }

    private void setAlertAbout() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot.getString(tag.getKEY_ABOUT_COMPANY()) == null) {
                            alert_about.setVisibility(View.VISIBLE);
                            alert_sucess_about.setVisibility(View.GONE);
                        } else {
                            if (documentSnapshot.getString(tag.getKEY_ABOUT_COMPANY()) != null) {
                                alert_about.setVisibility(View.GONE);
                                alert_sucess_about.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                });
    }

    private void setAlertBasicInfo() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot.getString(tag.getKEY_COMPANY_NAME()) == null) {
                            alert.setVisibility(View.VISIBLE);
                            alert_sucess.setVisibility(View.GONE);
                        } else {
                            if (documentSnapshot.getString(tag.getKEY_COMPANY_NAME()) != null) {
                                alert.setVisibility(View.GONE);
                                alert_sucess.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                });
    }

    private void getAboutData() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String aboutCompany = documentSnapshot.getString(tag.getKEY_ABOUT());
                    about_company.setText(aboutCompany);
                }
            }
        });
    }

    private void getInfoBasicData() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String companyName = documentSnapshot.getString(tag.getKEY_COMPANY_NAME());
                    company_name.setText(companyName);
                    String vagaName = documentSnapshot.getString(tag.getKEY_NAME_VAGA());
                    job_name.setText(vagaName);
                    String cityName = documentSnapshot.getString(tag.getKEY_CITY_VAGA());
                    city.setText(cityName);
                }
            }
        });
    }

    private void getSalaryData() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String maxSalary = documentSnapshot.getString(tag.getKEY_MAX_SALARY());
                    max_salary.setText(maxSalary);
                    String minSalary = documentSnapshot.getString(tag.getKEY_MIN_SALARY());
                    min_salary.setText(minSalary);
                    String beneFit = documentSnapshot.getString("benefit");
                    benefit.setText(beneFit);

                }
            }
        });
    }

    private void getRequirementData() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String requirementVaga = documentSnapshot.getString(tag.getKEY_REQUERIMENT_VAGA());
                    requirement.setText(requirementVaga);

                }

            }
        });
    }

    private void getDescriptionData() {
        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String descriptionVaga = documentSnapshot.getString(tag.getKEY_DESCRIPTION_VAGA());
                    description.setText(descriptionVaga);

                }

            }
        });
    }

    private void showDialogInfoSalary(View v) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.alert_salary, null);

        LinearLayout saveData = view.findViewById(R.id.saveData);
        LinearLayout cancel = view.findViewById(R.id.cancel);

        final TextInputEditText maxSalary = view.findViewById(R.id.max_salary);
        final TextInputEditText minSalary = view.findViewById(R.id.min_salary);
        final TextInputEditText benefit = view.findViewById(R.id.benefit);

        //Mask For Mínimo Salario
        SimpleMaskFormatter smfMinSalario = new SimpleMaskFormatter("R$ N.NNN,NN");
        MaskTextWatcher mtwMinSalario = new MaskTextWatcher(minSalary, smfMinSalario);
        minSalary.addTextChangedListener(mtwMinSalario);
        //Mask For Máximo Salario
        SimpleMaskFormatter smfMaxSalario = new SimpleMaskFormatter("R$ N.NNN,NN");
        MaskTextWatcher mtwMaxSalario = new MaskTextWatcher(maxSalary, smfMaxSalario);
        maxSalary.addTextChangedListener(mtwMaxSalario);
        //End Mask

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();

        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String max_salary = documentSnapshot.getString(tag.getKEY_MAX_SALARY());
                    maxSalary.setText(max_salary);
                    String min_salary = documentSnapshot.getString(tag.getKEY_MIN_SALARY());
                    minSalary.setText(min_salary);
                    String benefits = documentSnapshot.getString("benefit");
                    benefit.setText(benefits);
                }
            }
        });

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateMinSalary(view) & validateMaxSalary(view) & validateBenefit(view)) {
                    String max_salary = maxSalary.getText().toString();
                    String min_salary = minSalary.getText().toString();
                    String benefits = benefit.getText().toString();

                    Map<String, Object> map = new HashMap<>();
                    map.put(tag.getKEY_MAX_SALARY(), max_salary);
                    map.put(tag.getKEY_MIN_SALARY(), min_salary);
                    map.put("benefit", benefits);

                    firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getContext(), "Dados Salvos", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
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

    private void showDialogInfoBasic(View v) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.alert_basic_info, null);

        LinearLayout saveData = view.findViewById(R.id.saveData);
        LinearLayout cancel = view.findViewById(R.id.cancel);
        final TextInputEditText companyName = view.findViewById(R.id.companyName);
        final TextInputEditText nameVaga = view.findViewById(R.id.nameVaga);
        final TextInputEditText city = view.findViewById(R.id.city);

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();

        firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String name_company = documentSnapshot.getString(tag.getKEY_COMPANY_NAME());
                    companyName.setText(name_company);
                    String name_vaga = documentSnapshot.getString(tag.getKEY_NAME_VAGA());
                    nameVaga.setText(name_vaga);
                    String name_city = documentSnapshot.getString(tag.getKEY_CITY_VAGA());
                    city.setText(name_city);
                }
            }
        });


        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateCompanyName(view) & validateJobName(view) & validateCityName(view)) {
                    String name_company = companyName.getText().toString();
                    String name_vaga = nameVaga.getText().toString();
                    String name_city = city.getText().toString();

                    Map<String, Object> map = new HashMap<>();
                    map.put(tag.getKEY_COMPANY_NAME(), name_company);
                    map.put(tag.getKEY_NAME_VAGA(), name_vaga);
                    map.put(tag.getKEY_CITY_VAGA(), name_city);


                    firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getContext(), "Dados Salvos", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
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

    private boolean validateMinSalary(View view) {
        TextInputLayout error_min_salary = view.findViewById(R.id.error_min_salary);
        String salary_min = error_min_salary.getEditText().getText().toString();

        if (salary_min.isEmpty()) {
            error_min_salary.setError("Preencha este campo");
            return false;
        } else {
            error_min_salary.setError(null);
            return true;
        }
    }

    private boolean validateMaxSalary(View view) {
        TextInputLayout error_max_salary = view.findViewById(R.id.error_max_salary);
        String salary_max = error_max_salary.getEditText().getText().toString();

        if (salary_max.isEmpty()) {
            error_max_salary.setError("Preencha este campo");
            return false;
        } else {
            error_max_salary.setError(null);
            return true;
        }
    }

    private boolean validateBenefit(View view) {
        TextInputLayout error_beneficy = view.findViewById(R.id.error_beneficy);
        String beneficy = error_beneficy.getEditText().getText().toString();

        if (beneficy.isEmpty()) {
            error_beneficy.setError("Preencha este campo");
            return false;
        } else {
            error_beneficy.setError(null);
            return true;
        }
    }

    private boolean validateCompanyName(View view) {
        TextInputLayout error_company_name = view.findViewById(R.id.error_company_name);
        String company_name = error_company_name.getEditText().getText().toString();

        if (company_name.isEmpty()) {
            error_company_name.setError("Preencha este campo");
            return false;
        } else {
            error_company_name.setError(null);
            return true;
        }
    }

    private boolean validateJobName(View view) {
        TextInputLayout error_vaga_name = view.findViewById(R.id.error_vaga_name);
        String vaga_name = error_vaga_name.getEditText().getText().toString();

        if (vaga_name.isEmpty()) {
            error_vaga_name.setError("Preencha este campo");
            return false;
        } else {
            error_vaga_name.setError(null);
            return true;
        }
    }

    private boolean validateCityName(View view) {
        TextInputLayout error_city_name = view.findViewById(R.id.error_city_name);
        String city_name = error_city_name.getEditText().getText().toString();

        if (city_name.isEmpty()) {
            error_city_name.setError("Preencha este campo");
            return false;
        } else {
            error_city_name.setError(null);
            return true;
        }
    }

    private void findView(View v) {
        addRequeriment = v.findViewById(R.id.addRequeriment);
        addDescription = v.findViewById(R.id.addDescription);
        addSalary = v.findViewById(R.id.addSalary);
        addBasicInfo = v.findViewById(R.id.addBasicInfo);
        city = v.findViewById(R.id.city);
        job_name = v.findViewById(R.id.vaga_name);
        company_name = v.findViewById(R.id.company_name);
        min_salary = v.findViewById(R.id.min_salary);
        max_salary = v.findViewById(R.id.max_salary);
        benefit = v.findViewById(R.id.benefit);
        description = v.findViewById(R.id.description);
        requirement = v.findViewById(R.id.requirement);
        addAboutCompany = v.findViewById(R.id.addAboutCompany);
        about_company = v.findViewById(R.id.about_company);
        alert = v.findViewById(R.id.alert);
        alert_sucess = v.findViewById(R.id.alert_sucess);
        alert_sucess_about = v.findViewById(R.id.alert_sucess_about);
        alert_about = v.findViewById(R.id.alert_about);
        alert_sucess_salary = v.findViewById(R.id.alert_sucess_salary);
        alert_salary = v.findViewById(R.id.alert_salary);
        alert_sucess_description = v.findViewById(R.id.alert_sucess_description);
        alert_description = v.findViewById(R.id.alert_description);
        alert_sucess_requirement = v.findViewById(R.id.alert_sucess_requirement);
        alert_requirement = v.findViewById(R.id.alert_requirement);
        alert_status = v.findViewById(R.id.alert_status);
        status = v.findViewById(R.id.status);
        completed = v.findViewById(R.id.completed);
        available = v.findViewById(R.id.available);
        unavailable = v.findViewById(R.id.unavailable);
        btn_select_img = v.findViewById(R.id.btn_select_img);
        logo_image = v.findViewById(R.id.logo_image);
        progressBar = v.findViewById(R.id.progressBar);
        cb_show = v.findViewById(R.id.cb_show);


    }

    private void sendToAddRequirement() {
        Intent intent = new Intent(getContext(), CompanyRequirementActivity.class);
        startActivity(intent);
    }

    private void sendToAddDescription() {
        Intent intent = new Intent(getContext(), CompanyDescriptionActivity.class);
        startActivity(intent);
    }

    private void sendToAddAboutCompany() {
        Intent intent = new Intent(getContext(), CompanyAboutActivity.class);
        startActivity(intent);
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
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mSelectedUri);
                logo_image.setImageDrawable(new BitmapDrawable(bitmap));
                saveCompanyLogo();
            } catch (Exception e) {

            }
        }
    }

    private void saveCompanyLogo() {

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

                                firebaseFirestore.collection(tag.getKEY_VAGA()).document(user_id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
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


}

