package com.daud.simplenotepad;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpFragment extends Fragment {
    private TextInputEditText nameEt, emailEt, passwordEt, phoneEt;
    private MaterialButton signUpnBt, signInBtn;
    private ProgressBar progress;
    public FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        initial(view);

        signUpnBt.setOnClickListener(view1 -> {
            MainActivity.hideKeyboard(getActivity());
            String nameIn = nameEt.getText().toString();
            String emailIn = emailEt.getText().toString();
            String passwordIn = passwordEt.getText().toString();
            if (nameIn.isEmpty()) {
                nameEt.setError("Enter Name");
                nameEt.requestFocus();
                return;
            }
            //checking the validity of the email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailIn).matches()) {
                emailEt.setError("Enter a valid email address");
                emailEt.requestFocus();
                return;
            }
            //checking the validity of the password
            if (passwordIn.length() < 6) {
                passwordEt.setError("Enter at least 6 (six) character");
                passwordEt.requestFocus();
                return;
            }

            //This Method Will Help To SIGN UP With FirebaseAuth
            ////////////////////////////////////////////////////
            SignUpBtnAuth(nameIn, emailIn, passwordIn);
            ////////////////////////////////////////////////////
        });

        signInBtn.setOnClickListener(view1 -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.slide_out_left_to_right)
                    .replace(R.id.FrameLay, new SignInFragment())
                    .commit();
        });

        return view;
    }

    //This Method Will Help To SIGN UP With FirebaseAuth
    private void SignUpBtnAuth(String nameIn, String emailIn, String passwordIN) {
        //FirebaseAuth
        progress.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(emailIn, passwordIN).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userId);
                    DatabaseReference profileRef = databaseReference.child("Profile");
                    HashMap<String, Object> profileMAp = new HashMap<>();
                    profileMAp.put("Name", nameIn);
                    profileMAp.put("Email", emailIn);
                    profileMAp.put("userId", userId);
                    profileRef.setValue(profileMAp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.fade_in, R.anim.slide_out_left_to_right)
                                        .replace(R.id.FrameLay, new SignInFragment())
                                        .commit();
                            } else {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //initialization
    private void initial(View view) {
        nameEt = view.findViewById(R.id.nameEtSup);
        emailEt = view.findViewById(R.id.emailEtSup);
        passwordEt = view.findViewById(R.id.passwordEtSup);
        signUpnBt = view.findViewById(R.id.signUpBtnSup);
        signInBtn = view.findViewById(R.id.signInBtnSup);
        progress = view.findViewById(R.id.progressSup);
        firebaseAuth = FirebaseAuth.getInstance();
    }
}