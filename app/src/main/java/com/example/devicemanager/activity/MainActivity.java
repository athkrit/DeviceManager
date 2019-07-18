package com.example.devicemanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.devicemanager.R;
import com.example.devicemanager.fragment.LoginFragment;
import com.example.devicemanager.fragment.MainFragment;
import com.example.devicemanager.fragment.SummaryFragment;
import com.example.devicemanager.manager.LoadData;
import com.example.devicemanager.room.ItemEntity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private LoadData loadData;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private View view;
    private ProgressBar progressBar;
    private Button btnSummary, btnDetail;
    private int insertStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initInstances();

        String logout = getIntent().getStringExtra("logout");

        if (logout != null && logout.matches("true")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentContainer, LoginFragment.newInstance())
                    .commit();
        } else {
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showLoadingView();
                        setUpLoadData();
                        //setStartFragment();
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    private void initInstances() {
        btnDetail = findViewById(R.id.btnDetail);
        btnSummary = findViewById(R.id.btnSummary);
        view = findViewById(R.id.view);
        progressBar = findViewById(R.id.spin_kit);

        btnDetail.setOnClickListener(onBtnClick);
        btnSummary.setOnClickListener(onBtnClick);

        sp = this.getSharedPreferences("DownloadStatus", Context.MODE_PRIVATE);

        loadData = new LoadData(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getStringExtra("itemId") != null) {
            SuccessDialog();
            setUpLoadData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        editor = sp.edit();
        editor.clear();
        editor.apply();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setUpLoadData() {
        Log.d("test1707", sp.getBoolean("downloadStatus", true) + " Status");
        if (sp.getBoolean("downloadStatus", true)) {
            if (loadData.deleteTable() == 1) {
                loadData();
            }
        } else {
            hideLoadingView();
            setStartFragment();
        }
    }

    private void loadData() {
        insertStatus = 0;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Data");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editor = sp.edit();
                editor.putBoolean("downloadStatus", false);
                editor.apply();

                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    ItemEntity item = s.getValue(ItemEntity.class);

                    if (item != null) {
                        if (!item.getPurchasedDate().matches("") &&
                                !item.getPurchasedDate().matches("-")) {
                            item.setPurchasedDate(setDate(item.getPurchasedDate()));
                        }
                        item.setAutoId(Integer.parseInt(s.getKey()));
                        insertStatus = loadData.insert(item);
                    }
                }

                if (insertStatus == 1) {
                    setStartFragment();
                    hideLoadingView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(view, "Download Failed", Snackbar.LENGTH_SHORT)
                        .setAction("Reload", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setUpLoadData();
                            }
                        }).show();
            }
        });
    }

    private void setStartFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentContainer
                        , MainFragment.newInstance()
                        , "MainFragment")
                .commit();
        btnDetail.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void showLoadingView() {
        view.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
        view.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private String setDate(String inputDate) {
        if (inputDate.contains("GMT")) {
            inputDate = inputDate.substring(0, inputDate.indexOf("GMT")).trim();
        }
        String inputFormat = "EEE MMM dd yyyy HH:mm:ss";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(
                inputFormat, Locale.ENGLISH);
        String outputFormat = "yyyy-MM-dd";
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(
                outputFormat, Locale.ENGLISH);

        Date date;
        String str = inputDate;

        try {
            date = inputDateFormat.parse(inputDate);
            str = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;

    }

    private View.OnClickListener onBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            MainFragment mainFragment = (MainFragment)
                    getSupportFragmentManager().findFragmentByTag("MainFragment");
            SummaryFragment secondFragment = (SummaryFragment)
                    getSupportFragmentManager().findFragmentByTag("SummaryFragment");

            if (view == btnDetail) {
                if (mainFragment == null && secondFragment == null) {
                    setStartFragment();
                } else if (mainFragment == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.contentContainer
                                    , MainFragment.newInstance()
                                    , "MainFragment")
                            .detach(secondFragment)
                            .commit();
                } else if (secondFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .attach(mainFragment)
                            .detach(secondFragment)
                            .commit();
                }
                btnDetail.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btnSummary.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else if (view == btnSummary) {
                if (mainFragment == null && secondFragment == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentContainer
                                    , SummaryFragment.newInstance()
                                    , "SummaryFragment")
                            .commit();
                } else if (secondFragment == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.contentContainer
                                    , SummaryFragment.newInstance()
                                    , "SummaryFragment")
                            .detach(mainFragment)
                            .commit();
                } else if (mainFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .attach(secondFragment)
                            .detach(mainFragment)
                            .commit();
                }
                btnSummary.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btnDetail.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        }
    };
    private void SuccessDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}
