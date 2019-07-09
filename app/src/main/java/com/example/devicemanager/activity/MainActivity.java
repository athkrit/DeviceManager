package com.example.devicemanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.devicemanager.R;
import com.example.devicemanager.fragment.LoginFragment;
import com.example.devicemanager.fragment.MainFragment;
import com.example.devicemanager.fragment.SearchFragment;
import com.example.devicemanager.manager.LoadData;
import com.example.devicemanager.room.AppDatabase;
import com.example.devicemanager.room.ItemEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private LoadData loadData;
    private Boolean downloadStatus;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private View view;
    private ProgressBar progressBar;
    AppDatabase database;

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
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.contentContainer, MainFragment.newInstance())
                                .commit();
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    private void initInstances() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        sp = this.getSharedPreferences("DownloadStatus", Context.MODE_PRIVATE);
        editor = sp.edit();
        view = findViewById(R.id.view);
        progressBar = findViewById(R.id.spin_kit);
        view.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        loadData = new LoadData(this);

        downloadStatus = sp.getBoolean("downloadStatus", true);
        if (downloadStatus) {
            loadData();
        } else {
            view.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        downloadStatus = sp.getBoolean("downloadStatus", true);
        if (downloadStatus) {
            loadData();
        } else {
            view.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.clear();
        editor.commit();
    }

    private void loadData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Data");
        Query query = databaseReference.orderByChild("id");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadData.deleteTable();
                List<ItemEntity> i;
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    ItemEntity item = s.getValue(ItemEntity.class);
                    loadData.insert(item);
                }
                editor.putBoolean("downloadStatus", false);
                editor.commit();
                view.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
