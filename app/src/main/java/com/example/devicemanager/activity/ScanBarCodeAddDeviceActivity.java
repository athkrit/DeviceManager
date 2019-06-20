package com.example.devicemanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.devicemanager.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanBarCodeAddDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bar_code_add_device);
        initInstances();
    }
    private void initInstances() {
        IntentIntegrator integrator = new IntentIntegrator(ScanBarCodeAddDeviceActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                finish();

            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Intent intentBack = new Intent();
                intentBack.putExtra("serial",result.getContents());
                setResult(RESULT_OK,intentBack);
                finish();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
