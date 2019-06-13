package com.example.connecthuntapp.Fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connecthuntapp.Activitys.LoginActivity;
import com.example.connecthuntapp.Adapters.VagaAdapter;
import com.example.connecthuntapp.Models.User;
import com.example.connecthuntapp.Models.Vaga;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;


public class ProfessionalHomeFragment extends Fragment {
    private Tags tag = new Tags();
    private TextView logout, username, number_job;
    private Button list_vagas;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private Uri mSelectedUri;
    private ImageView img_select;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private VagaAdapter adapter;
    private String user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_professional_home, container, false);

        number_job = v.findViewById(R.id.number_job);
        logout = v.findViewById(R.id.logout);
        img_select = v.findViewById(R.id.photo);
        username = v.findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v);
            }
        });

        setupRecyclerView(v);

        getUserName();

        firebaseFirestore.collection(tag.getKEY_VAGA()).whereEqualTo("showVaga", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        int count = 0;

                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            count++;
                        }

                        number_job.setText(String.valueOf(count));
                        Log.d("TAG", count + "");
                    }
                });

        return v;
    }

    private void setupRecyclerView(View v) {
        final Tags tag = new Tags();

        user_id = mAuth.getCurrentUser().getUid();


        com.google.firebase.firestore.Query query = db.collection(tag.getKEY_VAGA()).whereEqualTo("showVaga", true);
        FirestoreRecyclerOptions<Vaga> options = new FirestoreRecyclerOptions.Builder<Vaga>()
                .setQuery(query, Vaga.class)
                .build();


        adapter = new VagaAdapter(options);
        RecyclerView recyclerView = v.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


    }


    private void getUserName() {
        firebaseFirestore.collection(tag.getKEY_PROFESSIONAL()).document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);

                            username.setText(user.getName());

                        }
                    }
                });
    }

    private void showDialog(View v) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.alert_logout, null);

        final Button logout = view.findViewById(R.id.logout_alert);
        Button cancel = view.findViewById(R.id.cancel);

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
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

    private void logOut() {
        mAuth.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
