package com.example.connecthuntapp.Fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.connecthuntapp.Activitys.LoginActivity;
import com.example.connecthuntapp.R;
import com.google.firebase.auth.FirebaseAuth;


public class CompanyHomeFragment extends Fragment {
    private TextView logout;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_company_home, container, false);
        logout = v.findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v);
            }
        });
        return v;
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

}
