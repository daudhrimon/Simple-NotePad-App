package com.daud.simplenotepad;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private TextView titleTv, noteTv;
    private ImageButton backIBtn,saveIBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userName = "User";
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        //initialization
        initial(view);
        /*Bundle bundle = this.getArguments();
       if (bundle != null){
           String state = bundle.getString("State");
           titleEt.setHint("BODY");
       }*/

        //getCurrentUser
        getCurrentUser();

        //saveBtn OnClick
        saveIBtn.setOnClickListener(view1 -> {
            String titleIn = titleEt.getText().toString();
            String noteIn = noteEt.getText().toString();
            if (titleIn.isEmpty()){
                Toast.makeText(getContext(), "Dear "+ userName +" You Can't Save a Note Without Title", Toast.LENGTH_SHORT).show();
            }else{
                PushNoteToFirebase(titleIn,noteIn);
            }
        });

        //backIBtn OnClick
        backIBtn.setOnClickListener(view1 -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in,R.anim.slide_out_left_to_right)
                    .replace(R.id.FrameLay,new HomeFragment()).commit();
        });

        ////////////////////////

        return view;
    }

    //initialization
    private void initial(View view) {
        titleEt = view.findViewById(R.id.titleEt);
        noteEt = view.findViewById(R.id.noteEt);
        titleTv = view.findViewById(R.id.titleTv);
        noteTv = view.findViewById(R.id.noteTv);
        backIBtn = view.findViewById(R.id.backIBtn);
        saveIBtn = view.findViewById(R.id.saveIBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("AllUsersNote");
    }

    //Push Note To Firebase Method
    private void PushNoteToFirebase(String titleIn, String noteIn) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference pushDataRef = databaseReference.child(userId).child("Notes").push();
        String pushKey = pushDataRef.getKey().toString();
        HashMap<String,Object> noteMap = new HashMap<>();
        noteMap.put("Title",titleIn);
        noteMap.put("Note",noteIn);
        noteMap.put("Key",pushKey);
        pushDataRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Dear..."+ userName +" Your Note Saved Successfully", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in,R.anim.slide_out_left_to_right)
                            .replace(R.id.FrameLay,new HomeFragment()).commit();
                }else{
                    Toast.makeText(getContext(), ""+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //getCurrentUser Method
    private void getCurrentUser() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference currentUserRef = databaseReference.child(userId).child("Profile").child("Name");
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userName = snapshot.getValue(String.class);
                }else {
                    userName = "User";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}