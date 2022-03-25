package com.daud.simplenotepad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_view_holder_layout, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.titleTv.setText(list.get(position).getTitle());
        holder.noteTv.setText(list.get(position).getNote());
        String Key = list.get(position).getKey();
        holder.itemView.setOnClickListener(view -> {
            itemViewOnclick(holder, position);
        });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Alert !");
            builder.setMessage("Do You Want To Delete This Note ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    itemViewOnLongClick(holder, Key);
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv, noteTv;
        private LinearLayout noteCard;
        private FirebaseAuth firebaseAuth;
        private DatabaseReference databaseReference;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTvNvh);
            noteTv = itemView.findViewById(R.id.noteTvNvh);
            noteCard = itemView.findViewById(R.id.noteCard);
            noteCard.setBackgroundColor(Color.parseColor(getRandomColor()));
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsersNote");
        }
    }

    private void itemViewOnclick(NotesViewHolder holder, int position) {
        MainActivity.editor.putString("Title", list.get(position).getTitle().toString());
        MainActivity.editor.putString("Note", list.get(position).getNote().toString());
        MainActivity.editor.putString("Key", list.get(position).getKey().toString());
        MainActivity.editor.putString("State", "Edit");
        MainActivity.editor.commit();
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_left_to_right)
                .replace(R.id.FrameLay, new TaskFragment())
                .addToBackStack(null).commit();
    }

    private void itemViewOnLongClick(NotesViewHolder holder, String key) {
        String userId = MainActivity.sharedPreferences.getString("userId", "");
        String Name = MainActivity.sharedPreferences.getString("Name", "");
        DatabaseReference deleteRef = holder.databaseReference.child(userId).child("Notes").child(key);
        deleteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(((FragmentActivity) context), Name + " Your Selected Note Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String getRandomColor() {
        List<String> colorCode = new ArrayList<>();
        colorCode.add("#DFFF00");
        colorCode.add("#FFBF00");
        colorCode.add("#FF7F50");
        colorCode.add("#DE3163");
        colorCode.add("#9FE2BF");
        colorCode.add("#40E0D0");
        colorCode.add("#6495ED");
        colorCode.add("#CCCCFF");

        Random random = new Random();
        int number = random.nextInt(colorCode.size());
        return colorCode.get(number);
    }
}
