package com.example.connecthuntapp.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.connecthuntapp.Activitys.SituationVagaActivity;
import com.example.connecthuntapp.Models.History;
import com.example.connecthuntapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class HistoryAdapter extends FirestoreRecyclerAdapter<History, HistoryAdapter.HistoryHolder> {


    public HistoryAdapter(@NonNull FirestoreRecyclerOptions<History> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HistoryHolder holder, final int position, @NonNull History model) {
        holder.companyName.setText(model.getCompanyName());
        holder.vagaName.setText(model.getNameVaga());
        holder.city.setText(model.getCity());
        Picasso.get().load(model.getLogo_company()).into(holder.logo_company);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getSnapshots().getSnapshot(position).getId();
                Intent intent = new Intent(v.getContext(), SituationVagaActivity.class);
                intent.putExtra("history_id", id);
                v.getContext().startActivity(intent);
            }
        });

        if (model.isSituation()) {
            holder.visualized_ok.setVisibility(View.VISIBLE);
        } else {
            holder.visualized_ok.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_history,
                viewGroup, false);
        return new HistoryHolder(v);
    }

    class HistoryHolder extends RecyclerView.ViewHolder{
        TextView vagaName;
        TextView companyName;
        TextView visualized_ok;
        TextView city;
        ImageView logo_company;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);

            logo_company = itemView.findViewById(R.id.logo_company);
            vagaName = itemView.findViewById(R.id.vaga_name);
            visualized_ok = itemView.findViewById(R.id.visualized_ok);
            companyName = itemView.findViewById(R.id.company_name);
            city = itemView.findViewById(R.id.city);
        }
    }
}
