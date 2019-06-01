package com.example.connecthuntapp.Fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.connecthuntapp.Activitys.LoginActivity;
import com.example.connecthuntapp.Adapters.VagaAdapter;
import com.example.connecthuntapp.Models.Vaga;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfessionalHomeFragment extends Fragment {
    private TextView logout, username;
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

        logout = v.findViewById(R.id.logout);
        img_select = v.findViewById(R.id.photo);
        username = v.findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v);
            }
        });

        setupRecyclerView(v);



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



    private void showDialog(View v) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.alert_logout, null);

        final Button logout = view.findViewById(R.id.logout_alert);
        Button cancelar = view.findViewById(R.id.cancelar);

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
