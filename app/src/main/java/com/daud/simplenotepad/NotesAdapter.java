package com.daud.simplenotepad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    private Context context;
    private List<NotesModel> list;

    public NotesAdapter(Context context, List<NotesModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_view_holder_layout,parent,false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.titleTv.setText(list.get(position).getTitle());
        holder.bodyTv.setText(list.get(position).getNote());


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv,bodyTv;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.titleTvNvh);
            bodyTv = itemView.findViewById(R.id.bodyTvNvh);
        }
    }
}
