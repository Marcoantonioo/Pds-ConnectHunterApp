package com.example.connecthuntapp.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connecthuntapp.Activitys.ViewCurriculumProfessionalActivity;
import com.example.connecthuntapp.Models.Candidato;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class CandidatoAdapter extends FirestoreRecyclerAdapter<Candidato, CandidatoAdapter.CandidatoHolder> {


    public CandidatoAdapter(@NonNull FirestoreRecyclerOptions<Candidato> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CandidatoHolder holder, final int position, @NonNull Candidato model) {
        holder.name.setText(model.getName());

        Picasso.get().load(model.getProfileUrl()).into(holder.profileUser);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String id = getSnapshots().getSnapshot(position).getId();
                Intent intent = new Intent(v.getContext(), ViewCurriculumProfessionalActivity.class);
                intent.putExtra("view_id", id);
                v.getContext().startActivity(intent);

            }
        });
        Date date = model.getDate();

        if (date != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String createDate = simpleDateFormat.format(date);
            holder.date.setText(createDate);
        }


    }

    @NonNull
    @Override
    public CandidatoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_candidato,
                viewGroup, false);
        return new CandidatoHolder(v);
    }

    class CandidatoHolder extends RecyclerView.ViewHolder {

        TextView name, finish, date;
        ImageView profileUser;

        public CandidatoHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            profileUser = itemView.findViewById(R.id.profileUser);
            finish = itemView.findViewById(R.id.finish);
            date = itemView.findViewById(R.id.date);
        }
    }
}
