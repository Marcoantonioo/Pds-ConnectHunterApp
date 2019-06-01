package com.example.connecthuntapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connecthuntapp.Adapters.HistoryAdapter;
import com.example.connecthuntapp.Models.History;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ProfessionalHistoryFragment extends Fragment {
    private String user_id;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_professional_history, container, false);


        firebaseConfig();

        user = mAuth.getCurrentUser();

        setupRecyclerView(v);

        return v;
    }

    private void setupRecyclerView(View v) {
        final Tags tag = new Tags();

        user_id = mAuth.getCurrentUser().getUid();


        Query query = db.collection("History").whereEqualTo("user_id",user_id);
        FirestoreRecyclerOptions<History> options = new FirestoreRecyclerOptions.Builder<History>()
                .setQuery(query, History.class)
                .build();

        adapter = new HistoryAdapter(options);
        RecyclerView recyclerView = v.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }

    private void firebaseConfig() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
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
