package com.example.connecthuntapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.connecthuntapp.Models.Language;
import com.example.connecthuntapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class LanguageAdapter extends FirestoreRecyclerAdapter<Language, LanguageAdapter.LanguageHolder> {

    private OnItemClickListener listener;

    public LanguageAdapter(@NonNull FirestoreRecyclerOptions<Language> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull LanguageHolder holder, int position, @NonNull Language model) {
        holder.tv_language.setText(model.getLanguage_name());
        holder.tv_level.setText(model.getLanguage_level());



    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public LanguageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_language,
                viewGroup, false);

        return new LanguageHolder(v);
    }

    class LanguageHolder extends RecyclerView.ViewHolder {
        TextView tv_language;
        TextView tv_level;

        public LanguageHolder(@NonNull View itemView) {
            super(itemView);

            tv_language = itemView.findViewById(R.id.language);
            tv_level = itemView.findViewById(R.id.level);

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

    public void setOnItemClickListener(LanguageAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
