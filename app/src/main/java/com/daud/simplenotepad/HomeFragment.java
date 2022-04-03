package com.daud.simplenotepad;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.daud.simplenotepad.MainActivity.editor;
import static com.daud.simplenotepad.MainActivity.hideKeyboard;
import static com.daud.simplenotepad.MainActivity.sharedPreferences;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private CircleImageView profileIcon;
    private SearchView searchView;
    private RecyclerView recyclerV;
    private LinearLayout emptyNotice;
    private FloatingActionButton addBtn;
    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference databaseReference;
    private StorageReference storageReference;
    private List<IdeasModel> list;
    public static String userId;
    private GridLayoutManager gridLayoutManager;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String ACTION;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // initialize
        initialize(view);

        // Show All Ideas From Firebase On HOME with RecyclerView
        showAllIdeas();

        // get ToolBars Profile Icon Image from Firebase
        getProfileIconImage();

        // ON CLICKS //

        //SearchView OnTextChanged or OnQueryTextListener //
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            List<IdeasModel> searchList = new ArrayList<>();
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!newText.isEmpty() || !newText.equals("")){
                    searchList.clear();
                    for (int i = 0; i < list.size(); i++){
                        if (list.get(i).getTitle().toLowerCase(Locale.ROOT).contains(newText)
                                || list.get(i).getIdea().toLowerCase(Locale.ROOT).contains(newText)){
                            searchList.add(list.get(i));
                        }
                    }
                    recyclerV.setAdapter(new IdeasAdapter(getContext(),searchList,0));
                }else {
                    recyclerV.setAdapter(new IdeasAdapter(getContext(),list, 1));
                }
                return false;
            }
        });

        // Profile ToolBar CircleImageView OnClick
        profileIcon.setOnClickListener(view1 -> {
            // Profile icon On Click Method
            profileIconOnClick();
        });

        // ADD Button OnClick
        addBtn.setOnClickListener(view1 -> {
            addBtnOnclick();
        });

        // Activity Result Launcher Method
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result != null && result.getData() != null) {
                    if (ACTION.equals("camera")) {
                        Bundle bundle = result.getData().getExtras();
                        Bitmap imgBitmap = (Bitmap) bundle.get("data");
                        Uri imgUri = MainActivity.getImageUri(getActivity(), imgBitmap);
                        // This Method Will Upload Selected IMAGE By User
                        uploadImageFirebaseStorage(imgUri);
                    } else if (ACTION.equals("gallery")) {
                        Uri imgUri = result.getData().getData();
                        // This Method Will Upload Selected IMAGE By User
                        uploadImageFirebaseStorage(imgUri);
                    }
                }
            }
        });

        return view;
    }

    // METHODS

    // Show All Ideas From Firebase On HOME with RecyclerView //////////////////////////////////////
    private void showAllIdeas() {
        DatabaseReference dataRef = databaseReference.child(userId).child("Ideas");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            IdeasModel ideasModel = dataSnapshot.getValue(IdeasModel.class);
                            list.add(ideasModel);
                        } else {
                            showAllIdeas();
                        }
                    }
                    emptyNotice.setVisibility(View.GONE);
                    recyclerV.setVisibility(View.VISIBLE);
                    recyclerV.setAdapter(new IdeasAdapter(getContext(), list, 1));
                } else {
                    recyclerV.setVisibility(View.GONE);
                    emptyNotice.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // get ToolBars Profile Icon Image from Firebase ///////////////////////////////////////////////
    private void getProfileIconImage() {
        DatabaseReference profileIconRef = databaseReference.child(userId).child("Profile").child("Image");
        profileIconRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String Image = snapshot.getValue(String.class);
                    Picasso.get()
                            .load(Image).placeholder(R.drawable.ic_baseline_person_24)
                            .error(R.drawable.ic_baseline_person_24).into(profileIcon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // ON CLICK METHODS ////////////////////////////////////////////////////////////////////////////


    // Profile icon ToolBar CircleImageView OnClick // Profile AlertDialog //
    private void profileIconOnClick() {
        AlertDialog profileDialog = new AlertDialog.Builder(getContext()).create();
        View alertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_layout, null);
        //initialize alertView Components
        CircleImageView profileCiv = alertView.findViewById(R.id.profileCiv);
        TextView nameTv = alertView.findViewById(R.id.nameTv);
        TextView emailTv = alertView.findViewById(R.id.emailTv);
        MaterialButton updateName = alertView.findViewById(R.id.updateName);
        MaterialButton settingsBtn = alertView.findViewById(R.id.settingsBtn);
        MaterialButton signOutBtn = alertView.findViewById(R.id.signOutBtn);
        ImageButton cancelIb = alertView.findViewById(R.id.cancelIb);
        FloatingActionButton updateImage = alertView.findViewById(R.id.updateImage);
        profileDialog.setView(alertView);

        // This Method Will Get User PROFILE data FROM FIREBASE
        getProfileData(nameTv, emailTv, profileCiv);

        // AlertDialog CancelBtn Onclick
        cancelIb.setOnClickListener(view -> {
            hideKeyboard(getActivity());
            profileDialog.dismiss();
        });

        // AlertDialog Update Image OnClick
        updateImage.setOnClickListener(view -> {
            if (checkInternet()){
                // This method Will show A PopUp And Start Acton Camera Or Gallery
                updateImageToFirebase(updateImage);
            }else {
                Toast.makeText(getContext(),"Please Check INTERNET_CONNECTION First",Toast.LENGTH_SHORT).show();
            }

        });

        // AlertDialog UPDATE Name OnClick
        updateName.setOnClickListener(view -> {
            // This Method Will Show a Custom AlertDialog for Update User's Name
            updateNameOnClickMethod();
        });

        // AlertDialog SignOut Floating Action BUTTON OnClick
        signOutBtn.setOnClickListener(view -> {
            // This Method Will Show a Custom AlertDialog for Ask SignOut YES or NO
            signOutBtnOnClickFromDialog(profileDialog);
        });

        profileDialog.setCancelable(false);
        profileDialog.show();
        Window window = profileDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }


    //Plus Button OnClick //////////////////////////////////////////////////////////////////////////
    private void addBtnOnclick() {
        DatabaseReference pushIdeaRef = databaseReference.child(userId).child("Ideas").push();
        String IdeaKey = pushIdeaRef.getKey().toString();
        HashMap<String, Object> noteMap = new HashMap<>();
        noteMap.put("Title", "");
        noteMap.put("Idea", "");
        noteMap.put("IdeaKey", IdeaKey);
        noteMap.put("Status", 0);
        noteMap.put("Todo","");
        noteMap.put("Color","");
        ///
        editor.putString("IdeaKey",IdeaKey);
        editor.putString("State", "Add");
        editor.commit();
        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right_bottom,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_right_bottom).replace(R.id.FrameLay, new TaskFragment()).addToBackStack(null).commit();
        ///
        pushIdeaRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) { } else {
                    Toast.makeText(getContext(), "" + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    // Profile AlertDialog // This Method Will Get User PROFILE data FROM FIREBASE
    private void getProfileData(TextView nameTv, TextView emailTv, CircleImageView profileCiv) {
        DatabaseReference profileRef = databaseReference.child(userId).child("Profile");
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ProfileModel profileModel = snapshot.getValue(ProfileModel.class);

                    nameTv.setText(profileModel.getName());
                    emailTv.setText(profileModel.getEmail());

                    Picasso.get()
                            .load(profileModel.getImage())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .error(R.drawable.ic_baseline_person_24).into(profileCiv);
                    Picasso.get()
                            .load(profileModel.getImage()).placeholder(R.drawable.ic_baseline_person_24)
                            .error(R.drawable.ic_baseline_person_24).into(profileIcon);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    // Profile AlertDialog // This method Will show A PopUp And Start Acton Camera Or Gallery
    private void updateImageToFirebase(FloatingActionButton updateImage) {
        PopupMenu popupMenu = new PopupMenu(getContext(), updateImage);
        popupMenu.getMenuInflater().inflate(R.menu.image_picker_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.camera:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activityResultLauncher.launch(intent);
                        ACTION = "camera";
                        break;
                    case R.id.gallery:
                        Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                        activityResultLauncher.launch(intent1);
                        ACTION = "gallery";
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }


    // Profile AlertDialog // UPDATE Name OnClick Method
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
            if (checkInternet()){
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
            }else{
                Toast.makeText(getContext(),"Please Check INTERNET_CONNECTION First",Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(view1 -> {
            nameDialog.dismiss();
        });
        nameDialog.setCancelable(false);
        nameDialog.show();
    }


    // Profile AlertDialog // SignOut Floating Action BUTTON OnClick Method
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


    // User's Selected IMAGE Upload To Firebase
    private void uploadImageFirebaseStorage(Uri imgUri) {
        StorageReference imageRef = storageReference.child(userId);
        imageRef.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                String imgURL = uri.toString();
                                // This Method Will Upload image Downloadable Link to Firebase REALTIME DATABASE
                                uploadImageURLFirebaseDatabase(imgURL);
                            }
                        }
                    });
                }
            }
        });

    }


    //Image Downloadable Link Upload to Firebase REALTIME DATABASE Method
    private void uploadImageURLFirebaseDatabase(String imgURL) {
        DatabaseReference imageURLRef = databaseReference.child(userId).child("Profile").child("Image");
        imageURLRef.setValue(imgURL).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    // Initialization Method
    private void initialize(View view) {
        profileIcon = view.findViewById(R.id.profileIcon);
        searchView = view.findViewById(R.id.searchView);
        recyclerV = view.findViewById(R.id.recyclerV);
        addBtn = view.findViewById(R.id.addBtn);
        emptyNotice = view.findViewById(R.id.emptyNotice);
        recyclerV = view.findViewById(R.id.recyclerV);

        gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerV.setHasFixedSize(true);
        recyclerV.setLayoutManager(gridLayoutManager);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("AllUsersIdea");
        databaseReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference("AllUsersImage");
        list = new ArrayList<>();
        userId = sharedPreferences.getString("userId", "");
    }
}