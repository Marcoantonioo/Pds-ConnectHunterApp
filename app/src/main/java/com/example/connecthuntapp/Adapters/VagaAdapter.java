package com.example.connecthuntapp.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.connecthuntapp.Activitys.DetailsJobActivity;
import com.example.connecthuntapp.Models.Vaga;
import com.example.connecthuntapp.R;
import com.example.connecthuntapp.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;


public class VagaAdapter extends FirestoreRecyclerAdapter<Vaga, VagaAdapter.VagaHolder> {
    private Tags tag = new Tags();

    public VagaAdapter(@NonNull FirestoreRecyclerOptions<Vaga> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final VagaHolder holder, final int position, @NonNull final Vaga model) {
        holder.vagaName.setText(model.getNameVaga());
        holder.companyName.setText(model.getCompanyName());
        holder.city.setText(model.getCity());
        holder.status.setText(model.getStatus());

        Picasso.get().load(model.getLogo_company()).into(holder.logo_image);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSnapshots().getSnapshot(position).getId();
                Intent intent = new Intent(v.getContext(), DetailsJobActivity.class);
                intent.putExtra("vaga_id", id);
                v.getContext().startActivity(intent);

            }
        });
        if (model.getStatus().equals(tag.getKEY_AVAILABLE())) {
            holder.available.setVisibility(View.VISIBLE);
            holder.completed.setVisibility(View.GONE);
            holder.unvailable.setVisibility(View.GONE);
        }else {
            if (model.getStatus().equals(tag.getKEY_COMPLETED())){
                holder.completed.setVisibility(View.VISIBLE);
                holder.available.setVisibility(View.GONE);
                holder.unvailable.setVisibility(View.GONE);
            }else {
                if (model.getStatus().equals(tag.getKEY_UNAVAILABLE())){
                    holder.unvailable.setVisibility(View.VISIBLE);
                    holder.available.setVisibility(View.GONE);
                    holder.completed.setVisibility(View.GONE);
                }
            }
        }


    }

    @NonNull
    @Override
    public VagaHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_vagas,
                viewGroup, false);
        return new VagaHolder(v);
    }

    class VagaHolder extends RecyclerView.ViewHolder {
        TextView vagaName;
        TextView companyName;
        TextView city;
        TextView status;
        ImageView logo_image;
        TextView timestamp;
        ImageView available, unvailable, completed;
        LinearLayout btnApplication;

        public VagaHolder(@NonNull View itemView) {
            super(itemView);

            vagaName = itemView.findViewById(R.id.nameVaga);
            companyName = itemView.findViewById(R.id.companyName);
            city = itemView.findViewById(R.id.city);
            status = itemView.findViewById(R.id.status);
            logo_image = itemView.findViewById(R.id.logoImage);
            available = itemView.findViewById(R.id.available);
            unvailable = itemView.findViewById(R.id.unvailable);
            completed = itemView.findViewById(R.id.completed);
            btnApplication = itemView.findViewById(R.id.btnApplication);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}
