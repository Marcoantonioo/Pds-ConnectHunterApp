package com.example.connecthuntapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.connecthuntapp.Models.Skill;
import com.example.connecthuntapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class SkillAdapter extends FirestoreRecyclerAdapter<Skill, SkillAdapter.SkillHolder> {

    private OnItemClickListener listener;

    public SkillAdapter(@NonNull FirestoreRecyclerOptions<Skill> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SkillHolder holder, int position, @NonNull Skill model) {
        holder.name.setText(model.getSkillName());
        holder.degree.setText(model.getSkillDegree());
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public SkillHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_skill,
                viewGroup, false);
        return new SkillHolder(v);
    }

    class SkillHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView degree;

        public SkillHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.skillName);
            degree = itemView.findViewById(R.id.skillDegree);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot doc, int position);
    }

    public void setOnItemClickListener(SkillAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
