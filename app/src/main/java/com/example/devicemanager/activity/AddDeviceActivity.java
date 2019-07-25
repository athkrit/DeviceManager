package com.example.devicemanager.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.devicemanager.R;
import com.example.devicemanager.fragment.AddDeviceFragment;

public class AddDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        initInstances();

        if (savedInstanceState == null) {

            Bundle bundle = new Bundle();

            bundle.putString("Path", getIntent().getStringExtra("Path"));
            bundle.putString("Serial", getIntent().getStringExtra("Serial"));
            AddDeviceFragment fragment = AddDeviceFragment.newInstances();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, fragment)
                    .commit();
        }
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
            showAlertDialog("Discard all Changing?");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        showAlertDialog("Discard all Changing?");
    }

    private void showAlertDialog(final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AddDeviceActivity.this, "Discard", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*Toast.makeText(AddDeviceActivity.this, "Cancel", Toast.LENGTH_SHORT).show();*/
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(android.R.color.white));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(android.R.color.white));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}
