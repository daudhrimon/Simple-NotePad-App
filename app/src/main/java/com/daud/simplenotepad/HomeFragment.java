package com.daud.simplenotepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.print.PrintAttributes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private CircleImageView profileTb;
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
        // Profile ToolBar CircleImageView OnClick
        profileTb.setOnClickListener(view1 -> {
            profileTbOnClick();
        });

        return view;
    }



    private void initial(View view) {
        profileTb = view.findViewById(R.id.profileTb);
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

    // Profile ToolBar CircleImageView OnClick
    private void profileTbOnClick() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        View alertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_layout,null);
        //initialize alertView Components
        CircleImageView profileCiv = alertView.findViewById(R.id.profileCiv);
        TextView nameTv = alertView.findViewById(R.id.nameTv);
        MaterialButton settingsFab = alertView.findViewById(R.id.settingsFab);
        MaterialButton signOutFab = alertView.findViewById(R.id.signOutFab);
        ImageButton cancelIb = alertView.findViewById(R.id.cancelIb);
        alertDialog.setView(alertView);

        cancelIb.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }
}