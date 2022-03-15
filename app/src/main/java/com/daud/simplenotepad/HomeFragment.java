package com.daud.simplenotepad;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerV;
    private FloatingActionButton addBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    List<NotesModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initial(view);

        String userId = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference dataRef = databaseReference.child("AllUsersNote").child(userId).child("Notes");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.exists()){
                        NotesModel notesModel = dataSnapshot.getValue(NotesModel.class);
                        list.add(notesModel);
                    }
                }
                recyclerV.setAdapter(new NotesAdapter(getContext(),list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        addBtn.setOnClickListener(view1 -> {

            getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right_to_left,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out_left_to_right).addToBackStack(null).replace(R.id.FrameLay,new TaskFragment()).commit();
        });

        return view;
    }


    private void initial(View view) {
        recyclerV = view.findViewById(R.id.recyclerV);
        addBtn = view.findViewById(R.id.addBtn);
        recyclerV = view.findViewById(R.id.recyclerV);
        recyclerV.setLayoutManager(new GridLayoutManager(getContext(),2));
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsersNote");
        list = new ArrayList<>();
    }
}