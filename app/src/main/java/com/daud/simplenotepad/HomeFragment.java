package com.daud.simplenotepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ImageButton menuIb;
    private SearchView searchView;
    private RecyclerView recyclerV;
    private FloatingActionButton addBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private List<NotesModel> list;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static String userId;
    public static String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // initialize
        initial(view);

         // Show Data RecyclerView
        ShowAllNotes();

        //Get User Name
        getUserName();

        //Plus Fab OnClick
        addBtn.setOnClickListener(view1 -> {

            getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right_bottom,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out_right_bottom).replace(R.id.FrameLay,new TaskFragment()).addToBackStack(null).commit();
        });

        return view;
    }

    private void initial(View view) {
        menuIb = view.findViewById(R.id.menuIb);
        recyclerV = view.findViewById(R.id.recyclerV);
        addBtn = view.findViewById(R.id.addBtn);
        recyclerV = view.findViewById(R.id.recyclerV);
        recyclerV.setLayoutManager(new GridLayoutManager(getContext(),2));
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsersNote");
        list = new ArrayList<>();
        sharedPreferences = getContext().getSharedPreferences("MySp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //Show All Notes
    private void ShowAllNotes() {
        userId = sharedPreferences.getString("userId","");
        DatabaseReference dataRef = databaseReference.child(userId).child("Notes");
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
    }

    //Get UserName As Public
    private void getUserName() {
        DatabaseReference userNameRef = databaseReference.child(userId).child("Profile").child("Name");
        userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userName = snapshot.getValue().toString();
                }else{
                    userName = "User";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}