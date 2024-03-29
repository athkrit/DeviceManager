package com.example.devicemanager.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.devicemanager.R;
import com.example.devicemanager.activity.MainActivity;
import com.example.devicemanager.manager.Contextor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private String strEmail, strPassword;
    private AppCompatButton btnSubmit;
    private TextView tvRegister;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar progressBar;
    private View progressDialogBackground;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initInstances(view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        etEmail.getText().clear();
        etPassword.getText().clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void initInstances(View view) {
        progressBar = view.findViewById(R.id.spin_kit);
        progressDialogBackground = view.findViewById(R.id.view);

        etEmail = view.findViewById(R.id.etLoginEmail);
        etPassword = view.findViewById(R.id.etLoginPassword);
        btnSubmit = view.findViewById(R.id.btnLoginSubmit);
        tvRegister = view.findViewById(R.id.tvRegister);

        enableClick();
        btnSubmit.setOnClickListener(onClickBtnSubmit);
        tvRegister.setOnClickListener(onClickRegister);

        mAuth = FirebaseAuth.getInstance();
    }

    private void userLogin() {
        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        if (checkForm(strEmail, strPassword)) {
            mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                enableClick();
                                hideLoading();
                                Toast.makeText(Contextor.getInstance().getContext(),
                                        "Login Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                enableClick();
                                hideLoading();
                                Toast.makeText(Contextor.getInstance().getContext(),
                                        "Failed to Login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private boolean checkForm(String strEmail, String strPassword) {
        if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
            enableClick();
            hideLoading();
            Toast.makeText(getActivity(), "Please insert all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkEmail() {
        String email = etEmail.getText().toString().toLowerCase();
        if (email.contains(getResources().getString(R.string.digio_email))) {
            return true;
        } else {
            enableClick();
            hideLoading();
            Toast.makeText(getContext(), "Incorrect E-mail", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void hideLoading() {
        progressDialogBackground.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void enableClick() {
        btnSubmit.setEnabled(true);
        tvRegister.setEnabled(true);
    }

    private void disableClick() {
        btnSubmit.setEnabled(false);
        tvRegister.setEnabled(false);
    }

    private View.OnClickListener onClickBtnSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            disableClick();
            hideKeyboardFrom(getContext(), view);
            progressDialogBackground.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            if (checkEmail()) {
                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user == null) {
                            userLogin();
                        }
                    }
                };
                mAuth.addAuthStateListener(mAuthListener);
            }
        }
    };

    private View.OnClickListener onClickRegister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            disableClick();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentContainer, RegisterFragment.newInstance())
                    .commit();
            enableClick();
        }
    };

}
