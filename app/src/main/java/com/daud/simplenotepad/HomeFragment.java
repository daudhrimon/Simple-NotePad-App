package com.daud.simplenotepad;

import static com.daud.simplenotepad.MainActivity.editor;
import static com.daud.simplenotepad.MainActivity.hideKeyboard;
import static com.daud.simplenotepad.MainActivity.sharedPreferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.print.PrintAttributes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
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
    private CircleImageView profileIcon;
    private SearchView searchView;
    private RecyclerView recyclerV;
    private FloatingActionButton addBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private List<NotesModel> list;
    public static String userId;
    public static String userName;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ProgressBar progress;

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
            editor.putString("State","Add").commit();
            getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right_bottom,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out_right_bottom).replace(R.id.FrameLay,new TaskFragment()).addToBackStack(null).commit();
        });
        // Profile ToolBar CircleImageView OnClick
        profileIcon.setOnClickListener(view1 -> {
            profileIconOnClick();
        });

        return view;
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
    private void profileIconOnClick() {
        //////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        AlertDialog profileDialog = new AlertDialog.Builder(getContext()).create();
        View alertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_layout,null);
        //initialize alertView Components
        CircleImageView profileCiv = alertView.findViewById(R.id.profileCiv);
        TextView nameTv = alertView.findViewById(R.id.nameTv);
        TextView emailTv = alertView.findViewById(R.id.emailTv);
        ImageView updateName = alertView.findViewById(R.id.updateName);
        MaterialButton settingsBtn = alertView.findViewById(R.id.settingsBtn);
        MaterialButton signOutBtn = alertView.findViewById(R.id.signOutBtn);
        ImageButton cancelIb = alertView.findViewById(R.id.cancelIb);
        //set View
        profileDialog.setView(alertView);
        nameTv.setText(sharedPreferences.getString("Name",""));
        emailTv.setText(sharedPreferences.getString("Email",""));

        //Get Profile Data
        getProfileDataFirebase(profileCiv,nameTv,emailTv);

        // CancelBtn Onclick
        cancelIb.setOnClickListener(view -> {
            hideKeyboard(getActivity());
            profileDialog.dismiss();
        });

        // SignOut Fab OnClick
        signOutBtn.setOnClickListener(view -> {
            signOutBtnOnClickFromDialog(profileDialog);
        });

        // UPDATE Name OnClick AlertDialog
        updateName.setOnClickListener(view -> {
            updateNameOnClickMethod();
        });
/////////////////////////////////////////////////////////////////////////////////////////
        profileDialog.setCancelable(false);
        profileDialog.show();
        Window window = profileDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }

    private void signOutBtnOnClickFromDialog(AlertDialog profileDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("SignOut Alert !");
        builder.setMessage("Do You Want To SignOut ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editor.putString("SignIn","False");
                editor.commit();
                firebaseAuth.signOut();
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_up,R.anim.fade_out)
                        .replace(R.id.FrameLay,new SignInFragment()).commit();
                dialogInterface.dismiss();
                profileDialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void updateNameOnClickMethod() {
        AlertDialog nameDialog = new AlertDialog.Builder(getContext()).create();
        View nameView = LayoutInflater.from(getContext()).inflate(R.layout.single_edittext_updater,null);
        //initial
        TextInputEditText updateEt = nameView.findViewById(R.id.updateEt);
        MaterialButton saveBtn = nameView.findViewById(R.id.saveBtn);
        ImageButton cancelBtn = nameView.findViewById(R.id.cancelBtn);
        ProgressBar progress = nameView.findViewById(R.id.updateProgress);
        updateEt.setHint("Enter New Name Here");
        //set View
        nameDialog.setView(nameView);

        // Save Btn OnClick
        saveBtn.setOnClickListener(view1 -> {
            String updateIn = updateEt.getText().toString();
            if (updateIn.isEmpty()){
                updateEt.setError("Invalid Value");
                updateEt.requestFocus();
                return;
            }
            progress.setVisibility(View.VISIBLE);
            DatabaseReference nameRef = databaseReference.child(userId).child("Profile").child("Name");
            nameRef.setValue(updateIn).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getContext(),updateIn+" Your Name Changed Successfully",Toast.LENGTH_SHORT).show();
                        hideKeyboard(getActivity());
                        nameDialog.dismiss();
                    }else {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getContext(),task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        cancelBtn.setOnClickListener(view1 -> {
            nameDialog.dismiss();
        });
        nameDialog.setCancelable(false);
        nameDialog.show();
    }

    private void getProfileDataFirebase(CircleImageView profileCiv, TextView nameTv, TextView emailTv) {
        DatabaseReference profileRef = databaseReference.child(userId).child("Profile");
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ProfileModel profileModel = snapshot.getValue(ProfileModel.class);
                    //profileCiv
                    editor.putString("Name",profileModel.getName());
                    editor.putString("Email",profileModel.getEmail());
                    editor.commit();
                    nameTv.setText(sharedPreferences.getString("Name",""));
                    emailTv.setText(sharedPreferences.getString("Email",""));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initial(View view) {
        profileIcon = view.findViewById(R.id.profileIcon);
        recyclerV = view.findViewById(R.id.recyclerV);
        addBtn = view.findViewById(R.id.addBtn);
        recyclerV = view.findViewById(R.id.recyclerV);
        recyclerV.setHasFixedSize(true);
        recyclerV.setLayoutManager(staggeredGridLayoutManager);
        recyclerV.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsersNote");
        list = new ArrayList<>();
        progress = view.findViewById(R.id.progress);
    }
}