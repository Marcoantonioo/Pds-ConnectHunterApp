package com.example.connecthuntapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.connecthuntapp.Models.Candidato;
import com.example.connecthuntapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListCandidateAdapter extends FirestoreRecyclerAdapter<Candidato,ListCandidateAdapter.listHolder> {


    public ListCandidateAdapter(@NonNull FirestoreRecyclerOptions<Candidato> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull listHolder holder, int position, @NonNull Candidato model) {
        holder.username.setText(model.getName());

        Date date = model.getDate();

            if (date != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String createDate = simpleDateFormat.format(date);
                holder.date.setText(createDate);
            }

    }


    @NonNull
    @Override
    public listHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_preview_candidate,
                viewGroup, false);
        return new listHolder(v);
    }

    class listHolder extends RecyclerView.ViewHolder{

        TextView username;
        TextView date;
        public listHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            username = itemView.findViewById(R.id.name);
        }
    }
}
