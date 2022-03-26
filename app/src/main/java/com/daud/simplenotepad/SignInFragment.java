package com.daud.simplenotepad;

import static com.daud.simplenotepad.MainActivity.editor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInFragment extends Fragment {
    private TextInputEditText emailEt, passwordEt;
    private MaterialButton signInBtn, signUpBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progress;
    private String userName = "User";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        initial(view);

        signInBtn.setOnClickListener(view1 -> {
            MainActivity.hideKeyboard(getActivity());
            String emailInSin = emailEt.getText().toString();
            String passwordInSin = passwordEt.getText().toString();
            //checking the validity of the email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInSin).matches()) {
                emailEt.setError("Enter a valid email address");
                emailEt.requestFocus();
                return;
            }
            //checking the validity of the password
            if (passwordInSin.length() < 6) {
                passwordEt.setError("Enter at least 6 (six) character");
                passwordEt.requestFocus();
                return;
            }

            //This Method Will Help To SIGN IN With FirebaseAuth
            ////////////////////////////////////////////////////
            signInWithFirebaseAuth(emailInSin, passwordInSin);
            ////////////////////////////////////////////////////
        });

        // signUpBtn OnClick //
        signUpBtn.setOnClickListener(view1 -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out_left_to_right).replace(R.id.FrameLay, new SignUpFragment())
                    .addToBackStack(null).commit();
        });

        return view;
    }

    // Sign In Button On Click Method
    private void signInWithFirebaseAuth(String emailInSin, String passwordInSin) {
        //FirebaseAuth
        progress.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(emailInSin, passwordInSin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference dataRef = databaseReference.child(userId).child("Profile").child("Name");
                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                userName = snapshot.getValue(String.class);
                            } else {
                                userName = "User";
                            }
                            progress.setVisibility(View.INVISIBLE);

                            // When SignIn Done This Method Will Ask For Password Save Or Not
                            /////////////////////////////////////////////////////////////////
                            passwordSaveDialog(userId);
                            /////////////////////////////////////////////////////////////////
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progress.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // When SignIn Done This Method Will Ask For Password Save Or Not
    private void passwordSaveDialog(String userId) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        View alertView = LayoutInflater.from(getContext()).inflate(R.layout.save_pass_layout, null);
        //initialize alertView Components
        TextView userNameTv = alertView.findViewById(R.id.userNameTv);
        MaterialButton yesBtn = alertView.findViewById(R.id.yesBtn);
        MaterialButton noBtn = alertView.findViewById(R.id.noBtn);
        alertDialog.setView(alertView);
        //Set currentUsers Name
        userNameTv.setText(userName);

        //yesBtn OnClick
        yesBtn.setOnClickListener(view2 -> {
            editor.putString("SignIn", "true");
            editor.commit();
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.fade_out)
                    .replace(R.id.FrameLay, new HomeFragment())
                    .commit();
            alertDialog.dismiss();
        });

        //noBtn OnClick
        noBtn.setOnClickListener(view2 -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.fade_out)
                    .replace(R.id.FrameLay, new HomeFragment())
                    .commit();
            alertDialog.dismiss();
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
        editor.putString("userId", userId);
        editor.commit();
    }

    //initialization
    private void initial(View view) {
        signUpBtn = view.findViewById(R.id.signUpBtn);
        emailEt = view.findViewById(R.id.emailEt);
        passwordEt = view.findViewById(R.id.passwordEt);
        signInBtn = view.findViewById(R.id.signInBtn);
        progress = view.findViewById(R.id.progressSin);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsersIdea");
    }
}