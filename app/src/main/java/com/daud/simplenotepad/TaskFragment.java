package com.daud.simplenotepad;

import static com.daud.simplenotepad.HomeFragment.userId;
import static com.daud.simplenotepad.HomeFragment.userName;
import static com.daud.simplenotepad.MainActivity.editor;
import static com.daud.simplenotepad.MainActivity.hideKeyboard;
import static com.daud.simplenotepad.MainActivity.sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TaskFragment extends Fragment {
    private TextInputEditText titleEt, noteEt;
    private ImageButton backIBtn, saveIBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String State;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        //initialization
        initial(view);
        State = sharedPreferences.getString("State", "Edit");
        checkStateAndDoCustomize();

        //saveBtn OnClick
        saveIBtn.setOnClickListener(view1 -> {
            if (State.equals("Edit")){
                String titleIn = titleEt.getText().toString();
                if (titleIn.isEmpty()) {
                    titleEt.setError("You Can't Save a Note Without Title");
                    titleEt.requestFocus();
                    return;
                } else {
                    String noteIn = noteEt.getText().toString();
                    updateNoteToFirebase(titleIn,noteIn);
                }

            }else{
                String titleIn = titleEt.getText().toString();
                if (titleIn.isEmpty()) {
                    titleEt.setError("You Can't Save a Note Without Title");
                    titleEt.requestFocus();
                    return;
                } else {
                    String noteIn = noteEt.getText().toString();
                    PushNoteToFirebase(titleIn, noteIn);
                }
            }

        });

        //backIBtn OnClick
        backIBtn.setOnClickListener(view1 -> {
            getParentFragmentManager().popBackStack();
            MainActivity.hideKeyboard(getActivity());
            MainActivity.setSharedPreferencesEmpty();
        });

        //////////////////////////////////////////////////

        return view;
    }

    private void checkStateAndDoCustomize() {
        if (State.equals("Edit")) {
            String Title = sharedPreferences.getString("Title","");
            String Note = sharedPreferences.getString("Note","");

            titleEt.setText(Title);
            noteEt.setText(Note);
        }
    }

    private void updateNoteToFirebase(String titleIn, String noteIn) {
        String Key = sharedPreferences.getString("Key","");
        DatabaseReference updateNoteRef = databaseReference.child(userId).child("Notes").child(Key);
        HashMap<String,Object> noteMap = new HashMap<>();
        noteMap.put("Title",titleIn);
        noteMap.put("Note",noteIn);
        noteMap.put("Key",Key);
        updateNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Dear..."+ userName +" Your Note Updated Successfully", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                    hideKeyboard(getActivity());
                    MainActivity.setSharedPreferencesEmpty();
                }else{
                    Toast.makeText(getContext(), ""+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Push Note To Firebase Method
    private void PushNoteToFirebase(String titleIn, String noteIn) {
        DatabaseReference pushNoteRef = databaseReference.child(userId).child("Notes").push();
        String pushKey = pushNoteRef.getKey().toString();
        HashMap<String,Object> noteMap = new HashMap<>();
        noteMap.put("Title",titleIn);
        noteMap.put("Note",noteIn);
        noteMap.put("Key",pushKey);
        pushNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Dear..."+ userName +" Your Note Saved Successfully", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                    hideKeyboard(getActivity());
                }else{
                    Toast.makeText(getContext(), ""+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //initialization
    private void initial(View view) {
        titleEt = view.findViewById(R.id.titleEt);
        noteEt = view.findViewById(R.id.noteEt);
        backIBtn = view.findViewById(R.id.backIBtn);
        saveIBtn = view.findViewById(R.id.saveIBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("AllUsersNote");
    }
}