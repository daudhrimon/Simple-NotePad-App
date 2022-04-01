package com.daud.simplenotepad;

import static com.daud.simplenotepad.HomeFragment.databaseReference;
import static com.daud.simplenotepad.MainActivity.sharedPreferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.IdeasViewHolder> {
    private Context context;
    private List<IdeasModel> list;
    private int colorCode;

    public IdeasAdapter(Context context, List<IdeasModel> list, int colorCode) {
        this.context = context;
        this.list = list;
        this.colorCode = colorCode;
        ;
    }

    @NonNull
    @Override
    public IdeasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_view_holder_layout, parent, false);
        return new IdeasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IdeasViewHolder holder, int position) {
        holder.titleTv.setText(list.get(position).getTitle());
        holder.ideaTv.setText(list.get(position).getIdea());
        String IdeaKey = list.get(position).getIdeaKey();

        // Auto Delete Empty Value //
        if (list.get(position).getTitle().equals("") && list.get(position).getIdea().equals("")){
            deleteDataOnLongClick(holder,IdeaKey,"Empty Idea Discarded");
        }

        //Status checker
        if (list.get(position).getStatus()==1){
            ////////////////////////////////////
            ////////////////////////////////////
            /////////////////////////////////////
            /////////////////////////////////////
        }

        // check random color Code
        if (colorCode ==1){
            holder.ideaCard.setBackgroundColor(Color.parseColor(MainActivity.getRandomColor()));
        }

        holder.itemView.setOnClickListener(view -> {
            itemViewOnclick(holder, position);
        });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Alert !");
            builder.setMessage("Do You Want To Delete This Idea ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteDataOnLongClick(holder, IdeaKey,"Selected Item Deleted");
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

    public class IdeasViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv, ideaTv;
        private LinearLayout ideaCard;

        public IdeasViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTvNvh);
            ideaTv = itemView.findViewById(R.id.ideaTvNvh);
            ideaCard = itemView.findViewById(R.id.ideaCard);
        }
    }

    private void itemViewOnclick(IdeasViewHolder holder, int position) {
        MainActivity.editor.putString("Title", list.get(position).getTitle().toString());
        MainActivity.editor.putString("Idea", list.get(position).getIdea().toString());
        MainActivity.editor.putString("IdeaKey", list.get(position).getIdeaKey().toString());
        MainActivity.editor.putString("State", "Edit");
        MainActivity.editor.commit();
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_left_to_right)
                .replace(R.id.FrameLay, new TaskFragment())
                .addToBackStack(null).commit();
    }

    private void deleteDataOnLongClick(IdeasViewHolder holder, String IdeaKey, String toast) {

        Toast.makeText(((FragmentActivity) context), toast, Toast.LENGTH_SHORT).show();

        String userId = sharedPreferences.getString("userId", "");
        DatabaseReference deleteRef = databaseReference.child(userId).child("Ideas").child(IdeaKey);
        deleteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //////////////////////////////////////////
                }
            }
        });
    }
}
