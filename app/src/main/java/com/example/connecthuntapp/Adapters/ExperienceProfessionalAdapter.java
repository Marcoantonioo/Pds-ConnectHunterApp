package com.example.connecthuntapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.connecthuntapp.Models.ExperienceProfessional;
import com.example.connecthuntapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ExperienceProfessionalAdapter extends FirestoreRecyclerAdapter<ExperienceProfessional, ExperienceProfessionalAdapter.ExperienceHolder> {
    private OnItemClickListener listener;

    public ExperienceProfessionalAdapter(@NonNull FirestoreRecyclerOptions<ExperienceProfessional> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ExperienceHolder holder, int position, @NonNull ExperienceProfessional model) {
        holder.name.setText(model.getCompanyName());
        holder.office.setText(model.getOffice());
        holder.initialDate.setText(model.getInitialDate());
        holder.finalDate.setText(model.getFinalData());
        holder.description.setText(model.getDescription());
    }
    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public ExperienceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_experience_professional,
                viewGroup, false);
        return new ExperienceHolder(v);
    }

    class ExperienceHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView office;
        TextView initialDate;
        TextView finalDate;
        TextView description;

        public ExperienceHolder(@NonNull View itemView) {
            super(itemView);
            name =itemView.findViewById(R.id.companyName);
            office = itemView.findViewById(R.id.office);
            initialDate = itemView.findViewById(R.id.initialDate);
            finalDate = itemView.findViewById(R.id.finalDate);
            description = itemView.findViewById(R.id.about_company);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot doc, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
