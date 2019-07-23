package com.example.devicemanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.devicemanager.R;
import com.example.devicemanager.fragment.LoginFragment;
import com.example.devicemanager.fragment.MainFragment;
import com.example.devicemanager.fragment.SummaryFragment;
import com.example.devicemanager.model.TypeItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView tvSummary, tvDetail;

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
                        setStartFragment();
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    private void initInstances() {
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorTheme)));
        tvDetail = findViewById(R.id.tvDetail);
        tvSummary = findViewById(R.id.tvSummary);

        sp = getSharedPreferences("Type", Context.MODE_PRIVATE);
        editor = sp.edit();

        tvDetail.setOnClickListener(onBtnClick);
        tvSummary.setOnClickListener(onBtnClick);
        getType();

    }

    private void getType() {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Type");
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String allType = "";
                String building = "";
                String device = "";
                String furniture = "";
                String other = "";
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    TypeItem typeItem = s.getValue(TypeItem.class);
                    allType = allType + typeItem.getType() + ",";
                    switch (typeItem.getAssetId()) {
                        case "1":
                            building = building + typeItem.getType() + ",";
                            break;
                        case "2":
                            device = device + typeItem.getType() + ",";
                            break;
                        case "3":
                            furniture = furniture + typeItem.getType() + ",";
                            break;
                        case "4":
                            other = other + typeItem.getType() + ",";
                            break;

                    }

                }
                editor.putString("allType", allType);
                editor.putString("building", building);
                editor.putString("device", device);
                editor.putString("other", other);
                editor.putString("furniture", furniture);
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    }

    private void setStartFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentContainer
                        , MainFragment.newInstance()
                        , "MainFragment")
                .commit();
        setBottomNavColor(tvDetail, "selected");
        setBottomNavColor(tvSummary, "none");
    }

    private View.OnClickListener onBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            MainFragment mainFragment = (MainFragment)
                    getSupportFragmentManager().findFragmentByTag("MainFragment");
            SummaryFragment secondFragment = (SummaryFragment)
                    getSupportFragmentManager().findFragmentByTag("SummaryFragment");

            if (view == tvDetail) {
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

                setBottomNavColor(tvDetail, "selected");
                setBottomNavColor(tvSummary, "none");

            } else if (view == tvSummary) {
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
                setBottomNavColor(tvDetail, "none");
                setBottomNavColor(tvSummary, "selected");
            }
        }
    };

    private void setBottomNavColor(TextView textView, String state){
        int color;

        if (state.equals("selected")) {
            color = R.color.white;
        } else {
            color = R.color.mid_grey;
        }

        Drawable[] drawables = textView.getCompoundDrawables();
        if (drawables[1] != null) {
            drawables[1].setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        }
        textView.setTextColor(getResources().getColor(color));
    }
}
