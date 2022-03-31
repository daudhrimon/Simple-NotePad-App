package com.daud.simplenotepad;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.daud.simplenotepad.HomeFragment.databaseReference;
import static com.daud.simplenotepad.HomeFragment.userId;
import static com.daud.simplenotepad.MainActivity.hideKeyboard;
import static com.daud.simplenotepad.MainActivity.sharedPreferences;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class TaskFragment extends Fragment {
    private TextInputEditText titleEt, ideaEt;
    private ImageButton backIBtn;
    private String State;
    private String IdeaKey;// from home fragment by sharedPref //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        //initialization
        initialize(view);

        // This Method Will Check State that this Fragment use for
        // Add idea or View And Edit Idea
        checkStateAndDoCustomize();

        // ON CLICKS //

        //backIBtn OnClick
        backIBtn.setOnClickListener(view1 -> {
            getParentFragmentManager().popBackStack();
            MainActivity.hideKeyboard(getActivity());
        });

        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String titleIn = charSequence.toString();
                DatabaseReference titleRef = databaseReference.child(userId).child("Ideas").child(IdeaKey).child("Title");
                titleRef.setValue(titleIn).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){                   }
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        ideaEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               String ideaIn = charSequence.toString();
               DatabaseReference ideaRef = databaseReference.child(userId).child("Ideas").child(IdeaKey).child("Idea");
               ideaRef.setValue(ideaIn).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){                   }
                   }
               });
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        return view;
    }



    // METHODS //

    // This Method Will Check State that this Fragment use for
    // Add idea or View And Edit Idea
    private void checkStateAndDoCustomize() {
        if (State.equals("Edit")) {
            String Title = sharedPreferences.getString("Title", "");
            String Idea = sharedPreferences.getString("Idea", "");
            titleEt.setText(Title);
            ideaEt.setText(Idea);
        }
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


    //initialization
    private void initialize(View view) {
        titleEt = view.findViewById(R.id.titleEt);
        ideaEt = view.findViewById(R.id.ideaEt);
        backIBtn = view.findViewById(R.id.backIBtn);
        State = sharedPreferences.getString("State", "Edit");
        IdeaKey = sharedPreferences.getString("IdeaKey","");
    }
}