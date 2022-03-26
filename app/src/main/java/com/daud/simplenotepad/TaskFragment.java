package com.daud.simplenotepad;

import static com.daud.simplenotepad.HomeFragment.databaseReference;
import static com.daud.simplenotepad.HomeFragment.userId;
import static com.daud.simplenotepad.MainActivity.hideKeyboard;
import static com.daud.simplenotepad.MainActivity.sharedPreferences;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TaskFragment extends Fragment {
    private TextInputEditText titleEt, ideaEt;
    private ImageButton backIBtn, saveIBtn;
    private String State;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        //initialization
        initial(view);

        // This Method Will Check State that this Fragment use for
        // Add idea or View And Edit Idea
        checkStateAndDoCustomize();

        // ON CLICKS //

        //save Button OnClick
        saveIBtn.setOnClickListener(view1 -> {
            String titleIn = titleEt.getText().toString();
            String ideaIn = ideaEt.getText().toString();
            if (titleIn.isEmpty() && ideaIn.isEmpty()) {
                Toast.makeText(getContext(), "You cant save a empty idea", Toast.LENGTH_SHORT).show();
            } else {
                if (State.equals("Edit")) {
                    // This Method Will Update idea
                    updateIdeaToFirebase(titleIn, ideaIn);
                } else {
                    // This Method Will Push New Added idea
                    pushIdeaToFirebase(titleIn, ideaIn);
                }
            }
        });

        //backIBtn OnClick
        backIBtn.setOnClickListener(view1 -> {
            getParentFragmentManager().popBackStack();
            MainActivity.hideKeyboard(getActivity());
        });

        return view;
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
        }
    }


    //Update Idea To Firebase Method
    private void updateIdeaToFirebase(String titleIn, String ideaIn) {
        String Key = sharedPreferences.getString("Key", "");
        DatabaseReference updateNoteRef = databaseReference.child(userId).child("Ideas").child(Key);
        HashMap<String, Object> noteMap = new HashMap<>();
        noteMap.put("Title", titleIn);
        noteMap.put("Idea", ideaIn);
        noteMap.put("Key", Key);
        updateNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Your idea updated successfully", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                    hideKeyboard(getActivity());
                } else {
                    Toast.makeText(getContext(), "" + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Push Note To Firebase Method
    private void pushIdeaToFirebase(String titleIn, String ideaIn) {
        DatabaseReference pushNoteRef = databaseReference.child(userId).child("Ideas").push();
        String pushKey = pushNoteRef.getKey().toString();
        HashMap<String, Object> noteMap = new HashMap<>();
        noteMap.put("Title", titleIn);
        noteMap.put("Idea", ideaIn);
        noteMap.put("Key", pushKey);
        pushNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Your idea Saved successfully", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                    hideKeyboard(getActivity());
                } else {
                    Toast.makeText(getContext(), "" + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //initialization
    private void initial(View view) {
        titleEt = view.findViewById(R.id.titleEt);
        ideaEt = view.findViewById(R.id.ideaEt);
        backIBtn = view.findViewById(R.id.backIBtn);
        saveIBtn = view.findViewById(R.id.saveIBtn);
        State = sharedPreferences.getString("State", "Edit");
    }
}