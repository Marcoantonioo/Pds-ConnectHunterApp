package com.example.connecthuntapp.Fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.connecthuntapp.Activitys.LoginActivity;
import com.example.connecthuntapp.Adapters.ListCandidateAdapter;
import com.example.connecthuntapp.Models.Candidato;
import com.example.connecthuntapp.Models.User;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


public class CompanyHomeFragment extends Fragment {
    private Tags tag = new Tags();
    private TextView logout, number_candi, company_name;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private ListCandidateAdapter adapter;
    private String user_id;
    private TextView history_complete;
    private CircleImageView photo;
    private Uri mSelectedUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_company_home, container, false);

        photo = v.findViewById(R.id.photo);
        company_name = v.findViewById(R.id.company_name);
        number_candi = v.findViewById(R.id.number_candi);
        logout = v.findViewById(R.id.logout);
        history_complete = v.findViewById(R.id.history_complete);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v);
            }
        });
        setupRecyclerView(v);
        getCompanyName();
        history_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick2();
            }
        });

        firebaseFirestore.collection(tag.getKEY_CANDIDATE()).whereEqualTo(tag.getKEY_VAGA_ID(), user_id)
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

                        number_candi.setText(String.valueOf(count));
                        Log.d("TAG", count + "");
                    }
                });

        return v;
    }


    public void onClick2() {
        CompanyHistoryFragment fragment2 = new CompanyHistoryFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment2);
        fragmentTransaction.commit();
    }

    private void getCompanyName() {
        firebaseFirestore.collection(tag.getKEY_COMPANY()).document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(tag.getKEY_ERROR(), "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {

                    String name = documentSnapshot.getString(tag.getKEY_NAME_COMPANY());

                    company_name.setText(name);

                }
            }
        });
    }

    private void setupRecyclerView(View v) {
        final Tags tag = new Tags();

        user_id = mAuth.getCurrentUser().getUid();


        Query query = firebaseFirestore.collection(tag.getKEY_CANDIDATE()).whereEqualTo(tag.getKEY_VAGA_ID(), user_id).limit(5)
                .orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Candidato> options = new FirestoreRecyclerOptions.Builder<Candidato>()
                .setQuery(query, Candidato.class)
                .build();

        adapter = new ListCandidateAdapter(options);
        RecyclerView recyclerView = v.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }

    private void showDialog(View v) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.alert_logout, null);

        final Button logout = view.findViewById(R.id.logout_alert);
        Button cancelar = view.findViewById(R.id.cancel);

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
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
