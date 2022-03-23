package com.daud.simplenotepad;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    private Context context;
    private List<NotesModel> list;
    NotesAdapter.NotesViewHolder notesViewHolder;

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
        holder.noteTv.setText(list.get(position).getNote());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv,noteTv;
        private LinearLayout noteCard;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTvNvh);
            noteTv = itemView.findViewById(R.id.noteTvNvh);
            noteCard = itemView.findViewById(R.id.noteCard);
            noteCard.setBackgroundColor(Color.parseColor(getRandomColor()));
        }
    }
    private String getRandomColor()
    {
        List<String> colorCode=new ArrayList<>();
        colorCode.add("#DFFF00");
        colorCode.add("#FFBF00");
        colorCode.add("#FF7F50");
        colorCode.add("#DE3163");
        colorCode.add("#9FE2BF");
        colorCode.add("#40E0D0");
        colorCode.add("#6495ED");
        colorCode.add("#CCCCFF");

        Random random=new Random();
        int number=random.nextInt(colorCode.size());
        return colorCode.get(number);
    }
}
