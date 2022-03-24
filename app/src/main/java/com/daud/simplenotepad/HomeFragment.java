package com.daud.simplenotepad;

import static android.app.Activity.RESULT_OK;
import static com.daud.simplenotepad.MainActivity.editor;
import static com.daud.simplenotepad.MainActivity.hideKeyboard;
import static com.daud.simplenotepad.MainActivity.sharedPreferences;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private FloatingActionButton profileIcon;
    private SearchView searchView;
    private RecyclerView recyclerV;
    private LinearLayout emptyNotice;
    private FloatingActionButton addBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private List<NotesModel> list;
    public static String userId;
    public static String userName;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ProgressBar progress;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String ACTION;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // initialize
        initial(view);
        // Show Data RecyclerView
        showAllNotes();

        //Plus Fab OnClick
        addBtn.setOnClickListener(view1 -> {
            editor.putString("State", "Add").commit();
            getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right_bottom,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out_right_bottom).replace(R.id.FrameLay, new TaskFragment()).addToBackStack(null).commit();
        });
        // Profile ToolBar CircleImageView OnClick
        profileIcon.setOnClickListener(view1 -> {
            profileIconOnClick();
        });

        /////////////////////////////////////
        // Activity Result Launcher Method //
        /////////////////////////////////////
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result!=null && result.getData()!=null){
                   if (ACTION.equals("camera")){
                       Bundle bundle = result.getData().getExtras();
                       Bitmap imgBitmap = (Bitmap) bundle.get("data");
                       Uri imgUri = MainActivity.getImageUri(getActivity(),imgBitmap);
                       uploadImageFirebaseStorage(imgUri);
                   }else if (ACTION.equals("gallery")){
                       Uri imgUri = result.getData().getData();
                       uploadImageFirebaseStorage(imgUri);
                   }
                }
            }
        });

        return view;
    }

    private void uploadImageFirebaseStorage(Uri imgUri) {
        StorageReference imageRef = storageReference.child(userId);
        imageRef.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           if (uri!=null){
                               String imgURL = uri.toString();
                               uploadImageURLFirebaseDatabase(imgURL);
                           }
                        }
                    });
                }
            }
        });

    }

    private void uploadImageURLFirebaseDatabase(String imgURL) {
        DatabaseReference imageURLRef = databaseReference.child(userId).child("Profile").child("Image");
        imageURLRef.setValue(imgURL).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()){
                   Toast.makeText(getContext(),userName+" Image Uploaded Successfully",Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    //Show All Notes
    private void showAllNotes() {
        userId = sharedPreferences.getString("userId", "");
        DatabaseReference dataRef = databaseReference.child(userId).child("Notes");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    emptyNotice.setVisibility(View.GONE);
                    recyclerV.setVisibility(View.VISIBLE);
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            NotesModel notesModel = dataSnapshot.getValue(NotesModel.class);
                            list.add(notesModel);
                        }else {
                            showAllNotes();
                        }
                    }
                }else {
                    recyclerV.setVisibility(View.GONE);
                    emptyNotice.setVisibility(View.VISIBLE);
                }
                recyclerV.setAdapter(new NotesAdapter(getContext(), list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Profile ToolBar CircleImageView OnClick
    private void profileIconOnClick() {
        //////////////////////////////////////////////////////////////////////////////////////////////
        AlertDialog profileDialog = new AlertDialog.Builder(getContext()).create();
        View alertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_layout, null);
        //initialize alertView Components
        CircleImageView profileCiv = alertView.findViewById(R.id.profileCiv);
        TextView nameTv = alertView.findViewById(R.id.nameTv);
        TextView emailTv = alertView.findViewById(R.id.emailTv);
        ImageView updateName = alertView.findViewById(R.id.updateName);
        MaterialButton settingsBtn = alertView.findViewById(R.id.settingsBtn);
        MaterialButton signOutBtn = alertView.findViewById(R.id.signOutBtn);
        ImageButton cancelIb = alertView.findViewById(R.id.cancelIb);
        FloatingActionButton updateImage = alertView.findViewById(R.id.updateImage);
        profileDialog.setView(alertView);
        ///////////////////////////////////////////////////////////////////
        nameTv.setText(sharedPreferences.getString("Name", ""));
        emailTv.setText(sharedPreferences.getString("Email", ""));

        getProfileDataFirebase(profileCiv, nameTv, emailTv);

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

        updateImage.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(getContext(),updateImage);
            popupMenu.getMenuInflater().inflate(R.menu.image_picker_menu,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.camera:
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            activityResultLauncher.launch(intent);
                            ACTION="camera";
                            break;
                        case R.id.gallery:
                            Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                            activityResultLauncher.launch(intent1);
                            ACTION="gallery";
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
        });

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
                editor.putString("SignIn", "False");
                editor.commit();
                firebaseAuth.signOut();
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_up, R.anim.fade_out)
                        .replace(R.id.FrameLay, new SignInFragment()).commit();
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
        View nameView = LayoutInflater.from(getContext()).inflate(R.layout.single_edittext_updater, null);
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
            if (updateIn.isEmpty()) {
                updateEt.setError("Invalid Value");
                updateEt.requestFocus();
                return;
            }
            progress.setVisibility(View.VISIBLE);
            DatabaseReference nameRef = databaseReference.child(userId).child("Profile").child("Name");
            nameRef.setValue(updateIn).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), updateIn + " Your Name Changed Successfully", Toast.LENGTH_SHORT).show();
                        hideKeyboard(getActivity());
                        nameDialog.dismiss();
                    } else {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                if (snapshot.exists()) {
                    ProfileModel profileModel = snapshot.getValue(ProfileModel.class);
                    nameTv.setText(profileModel.getName());
                    emailTv.setText(profileModel.getEmail());

                    Picasso.get().load(profileModel.getImage())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .error(R.drawable.ic_baseline_person_24).into(profileCiv);

                    editor.putString("Name", profileModel.getName());
                    editor.putString("Email", profileModel.getEmail());
                    editor.commit();
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
        emptyNotice = view.findViewById(R.id.emptyNotice);
        recyclerV = view.findViewById(R.id.recyclerV);
        recyclerV.setHasFixedSize(true);
        recyclerV.setLayoutManager(staggeredGridLayoutManager);
        recyclerV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsersNote");
        storageReference = FirebaseStorage.getInstance().getReference("AllUsersImage");
        list = new ArrayList<>();
        progress = view.findViewById(R.id.progress);
        userName = sharedPreferences.getString("Name","");
    }
}