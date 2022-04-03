package com.daud.simplenotepad;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.daud.simplenotepad.HomeFragment.databaseReference;
import static com.daud.simplenotepad.HomeFragment.userId;
import static com.daud.simplenotepad.MainActivity.sharedPreferences;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskFragment extends Fragment {
    private TextInputEditText titleEt,ideaEt;
    private ImageButton backBtn;
    private TextView ckdBtn,itemPlus;
    private String State;
    private String IdeaKey;// from home fragment by sharedPref //
    private String TodoKey;
    private List<TodoModel> todoList;
    private RecyclerView todoRecycler;
    private TodoAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        //initialization
        initialize(view);

        State = sharedPreferences.getString("State", "Edit");
        IdeaKey = sharedPreferences.getString("IdeaKey","");

        // This Method Will Check State that this Fragment use for Add idea or View And Edit Idea
        checkStateAndDoCustomize();

        getTodoData();

        // ON CLICKS //

        //backIBtn OnClick
        backBtn.setOnClickListener(view1 -> {
            getParentFragmentManager().popBackStack();
            MainActivity.hideKeyboard(getActivity());
        });

        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String titleIn = charSequence.toString();
                DatabaseReference titleRef = databaseReference.child(userId).child("Ideas").child(IdeaKey).child("Title");
                titleRef.setValue(titleIn).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){                   }
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        ideaEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               String ideaIn = charSequence.toString();
               DatabaseReference ideaRef = databaseReference.child(userId).child("Ideas").child(IdeaKey).child("Idea");
               ideaRef.setValue(ideaIn).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){                   }
                   }
               });
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        ckdBtn.setOnClickListener(view1 -> {
            ckdBtnOnClick();
        });

        itemPlus.setOnClickListener(view1 -> {
            pushEmptyItemsFirebase();
        });

        /*ckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int status;
                if (statusBox.isChecked()){
                    status = 1;
                }else {
                    status = 0;
                }
                DatabaseReference statusRef = databaseReference
                        .child(userId).child("Ideas").child(IdeaKey).child("Items").child(ItemKey).child("Status");
                statusRef.setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){                   }
                    }
                });
            }
        });*/

        return view;
    }

    private void getTodoData() {
        DatabaseReference listRef = databaseReference.child(userId).child("Ideas").child(IdeaKey).child("Todo");
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    todoList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        TodoModel todoModel = dataSnapshot.getValue(TodoModel.class);
                        todoList.add(todoModel);
                        Log.d("List", todoList.toString());
                    }
                    todoRecycler.setVisibility(View.VISIBLE);
                    adapter = new TodoAdapter(getContext(),todoList);
                    todoRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    todoRecycler.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void ckdBtnOnClick() {
        DatabaseReference ideaRef = databaseReference.child(userId).child("Ideas").child(IdeaKey).child("Idea");
        ideaRef.setValue("");
        DatabaseReference IdeaStatusRef = databaseReference.child(userId).child("Ideas").child(IdeaKey).child("Status");
        IdeaStatusRef.setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ideaEt.setVisibility(View.GONE);
                    ckdBtn.setVisibility(View.GONE);
                    itemPlus.setVisibility(View.VISIBLE);
                    pushEmptyItemsFirebase();
                }
            }
        });
    }

    public void pushEmptyItemsFirebase() {
        DatabaseReference emptyListItemRef = databaseReference.child(userId).child("Ideas").child(IdeaKey).child("Todo").push();
        TodoKey = emptyListItemRef.getKey().toString();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Todo","");
        hashMap.put("Status",0);
        hashMap.put("TodoKey",TodoKey);
        emptyListItemRef.setValue(hashMap);
    }


    // METHODS //

    // This Method Will Check State that this Fragment use for
    // Add idea or View And Edit Idea
    private void checkStateAndDoCustomize() {
        if (State.equals("Edit")) {
            String Title = sharedPreferences.getString("Title", "");
            String Idea = sharedPreferences.getString("Idea", "");
            titleEt.setText(Title);
            ideaEt.setText(Idea);
        }else if (State.equals("Todo")){
            String Title = sharedPreferences.getString("Title", "");
            titleEt.setText(Title);
            ideaEt.setVisibility(View.GONE);
            ckdBtn.setVisibility(View.GONE);
            itemPlus.setVisibility(View.VISIBLE);
        }
    }


    //This method will check internet connected or not
    private boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }


    //initialization
    private void initialize(View view) {
        titleEt = view.findViewById(R.id.titleEt);
        ideaEt = view.findViewById(R.id.ideaEt);
        backBtn = view.findViewById(R.id.backBtn);
        ckdBtn = view.findViewById(R.id.ckdBtn);
        itemPlus = view.findViewById(R.id.itemPlus);
        todoList = new ArrayList<>();
        todoRecycler = view.findViewById(R.id.todoRecycler);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        todoRecycler.setLayoutManager(layoutManager);
        todoRecycler.onCheckIsTextEditor();
    }
}