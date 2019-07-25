package com.example.devicemanager.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.devicemanager.R;
import com.example.devicemanager.fragment.SummaryListDetailFragment;

public class SummaryListDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_list_detail);
        setTitle(getIntent().getStringExtra("Type"));
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();

            bundle.putString("Type", getIntent().getStringExtra("Type"));
            bundle.putString("Brand", getIntent().getStringExtra("Brand"));

            SummaryListDetailFragment fragment = SummaryListDetailFragment.newInstance();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, fragment)
                    .commit();
        }
        initInstances();
    }

    private void initInstances() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorTheme)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
