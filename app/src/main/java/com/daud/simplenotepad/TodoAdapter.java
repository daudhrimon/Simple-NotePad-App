package com.daud.simplenotepad;

import static com.daud.simplenotepad.HomeFragment.databaseReference;
import static com.daud.simplenotepad.HomeFragment.userId;
import static com.daud.simplenotepad.MainActivity.hideKeyboard;
import static com.daud.simplenotepad.MainActivity.sharedPreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;

import java.nio.file.Watchable;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private Context context;
    private List<TodoModel> todoList;

    public TodoAdapter(Context context, List<TodoModel> todoList) {
        this.context = context;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_view_layout, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.todoTv.setText(todoList.get(position).getTodo());
        String IdeaKey = sharedPreferences.getString("IdeaKey", "");
        String TodoKey = todoList.get(position).getTodoKey().toString();
        int Status = todoList.get(position).getStatus();

        // Check To-do item checked or not
        if (Status == 0) {
            // this will be set ImageView as UnChecked //
            holder.statusBox.setImageResource(R.drawable.unchecked_checkbox);
        } else if (Status == 1) {
            // this will be set ImageView as Checked //
            holder.statusBox.setImageResource(R.drawable.ic_baseline_check_box_24);
        }

        holder.itemView.setOnClickListener(view -> {
            itemViewOnClickMethod(Status, todoList.get(position).getTodo(), IdeaKey, TodoKey);

        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        private ImageView statusBox, unCkd;
        private TextView todoTv;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            todoTv = itemView.findViewById(R.id.todoTv);
            statusBox = itemView.findViewById(R.id.statusBox);
        }
    }


    // items OnClick AlertDialog for set to-do items info
    private void itemViewOnClickMethod(int Status, String Todo, String IdeaKey, String TodoKey) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        View alertView = LayoutInflater.from(context).inflate(R.layout.add_or_edit_todo, null);

        ///
        ImageView statusBoxT = alertView.findViewById(R.id.statusBoxT);
        TextInputEditText todoEtT = alertView.findViewById(R.id.todoEtT);
        MaterialButton doneBtnT = alertView.findViewById(R.id.doneBtnT);
        alertDialog.setView(alertView);

        ///
        if (Status == 0) {
            // this will be set ImageView as UnChecked //
            statusBoxT.setImageResource(R.drawable.unchecked_checkbox);
        } else if (Status == 1) {
            // this will be set ImageView as Checked //
            statusBoxT.setImageResource(R.drawable.ic_baseline_check_box_24);
        }

        ///
        todoEtT.setText(Todo);

        ///
        todoEtT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String todoIn = charSequence.toString();
                DatabaseReference todoInRef = databaseReference.child(userId).child("Ideas")
                        .child(IdeaKey).child("Todo").child(TodoKey).child("Todo");
                todoInRef.setValue(todoIn);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ///
        statusBoxT.setOnClickListener(view1 -> {
            if (Status == 0) {
                // this will be set ImageView as Checked //
                statusBoxT.setImageResource(R.drawable.ic_baseline_check_box_24);
                // change status to firebase //
                DatabaseReference todoInRef = databaseReference.child(userId).child("Ideas")
                        .child(IdeaKey).child("Todo").child(TodoKey).child("Status");
                todoInRef.setValue(1);
            } else if (Status == 1) {
                // this will be set ImageView as UnChecked //
                statusBoxT.setImageResource(R.drawable.unchecked_checkbox);
                // change status to firebase //
                DatabaseReference todoInRef = databaseReference.child(userId).child("Ideas")
                        .child(IdeaKey).child("Todo").child(TodoKey).child("Status");
                todoInRef.setValue(0);
            }
        });

        ///
        doneBtnT.setOnClickListener(view1 -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
    }
}
