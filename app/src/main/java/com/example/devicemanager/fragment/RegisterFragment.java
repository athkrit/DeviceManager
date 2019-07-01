package com.example.devicemanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devicemanager.R;
import com.example.devicemanager.activity.MainActivity;
import com.example.devicemanager.manager.Contextor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterFragment extends Fragment {

    private EditText etEmail, etPassword, etCode;
    private String strEmail, strPassword, strCode;
    private boolean state;
    private FirebaseAuth mAuth;
    private Button btnSubmit;
    private TextView tvLogin;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context context;
    ProgressBar progressBar ;
    View progressDialogBackground;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initInstances(view);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void initInstances(View view) {

        context = Contextor.getInstance().getContext();

        etEmail = view.findViewById(R.id.etRegEmail);
        etPassword = view.findViewById(R.id.etRegPassword);
        etCode = view.findViewById(R.id.etRegInviteCode);
        btnSubmit = view.findViewById(R.id.btnRegSubmit);
        tvLogin = view.findViewById(R.id.tvRegLogin);

        progressBar = (ProgressBar)view.findViewById(R.id.spin_kit);
        progressDialogBackground = (View) view.findViewById(R.id.view);

        mAuth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(onClickSubmit);
        tvLogin.setOnClickListener(onClickLogin);
    }

    private void registerUser() {
        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("isSuccessful", e.getMessage());
                        progressDialogBackground.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Register Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }

    private boolean checkCode(){
        strCode = etCode.getText().toString().trim();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("InvitationCode");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    String code = s.child("Code").getValue(String.class);
                    if (code != null && strCode.matches(code)){
                        state = true;
                        break;
                    }
                    else {
                        state = false;
                        Toast.makeText(context, "Data not matched",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Cannot connect to Firebase", Toast.LENGTH_SHORT).show();
                Log.d("Firebase", databaseError.getMessage());
                state = false;
            }
        });
        return state;
    }

    private boolean checkForm(){
        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();
        strCode = etCode.getText().toString().trim();
        String regEmail = strEmail.substring(strEmail.indexOf("@") + 1);

        if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword) || TextUtils.isEmpty(strCode)){
            Toast.makeText(context, "Please insert all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (isAdded() && !regEmail.matches(getResources().getString(R.string.digio_email))){
            Toast.makeText(context, "Wrong E-Mail address", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (strPassword.length() < 6) {
            Toast.makeText(context, "Password must contain at least 6 letters", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private View.OnClickListener onClickSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboardFrom(context,view);
            progressDialogBackground.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            registerUser();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null && checkForm() && checkCode()) {
                        registerUser();
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListener);
//            progressDialogBackground.setVisibility(View.INVISIBLE);
//            progressBar.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener onClickLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentContainer, LoginFragment.newInstance())
                    .commit();
        }
    };

}
