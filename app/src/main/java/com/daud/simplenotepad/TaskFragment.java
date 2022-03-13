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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class TaskFragment extends Fragment {
    private TextInputEditText titleEt,bodyEt;
    private TextView titleTv,bodyTv;
    private ImageButton backIBtn,saveIBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String currentUser = "User";

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
            String bodyIn = bodyEt.getText().toString();
            if (titleIn.isEmpty()){
                Toast.makeText(getContext(), "Dear "+currentUser+" You Can't Save a Note Without Title", Toast.LENGTH_SHORT).show();
            }else{
                PushNoteToFirebase(titleIn,bodyIn);
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
        bodyEt = view.findViewById(R.id.bodyEt);
        titleTv = view.findViewById(R.id.titleTv);
        bodyTv = view.findViewById(R.id.bodyTv);
        backIBtn = view.findViewById(R.id.backIBtn);
        saveIBtn = view.findViewById(R.id.saveIBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("AllUsersNote");
    }

    //Push Note To Firebase Method
    private void PushNoteToFirebase(String titleIn, String bodyIn) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference pushDataRef = databaseReference.child(userId).child("Notes").push();
        String pushKey = pushDataRef.getKey().toString();
        HashMap<String,Object> noteMap = new HashMap<>();
        noteMap.put("Title",titleIn);
        noteMap.put("Body",bodyIn);
        noteMap.put("Key",pushKey);
        pushDataRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Dear..."+currentUser+" Your Note Saved Successfully", Toast.LENGTH_SHORT).show();
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
        DatabaseReference currentUserRef = databaseReference.child(userId).child("Name");
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentUser = snapshot.getValue(String.class);
                }else {
                    currentUser = "User";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}