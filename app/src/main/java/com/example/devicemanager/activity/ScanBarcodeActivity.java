package com.example.devicemanager.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.devicemanager.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ScanBarcodeActivity extends AppCompatActivity {
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    Boolean doing = true;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCameraPermissionID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        initInstances();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void initInstances() {
        cameraView = findViewById(R.id.surfaceView);
        textView = findViewById(R.id.text_view);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "Detector dependencies are not yet available", Toast.LENGTH_SHORT).show();
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(5.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(callback);

            textRecognizer.setProcessor(processor);
        }

    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            try {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ScanBarcodeActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            RequestCameraPermissionID);
                    return;
                }
                cameraSource.start(cameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            cameraSource.stop();
        }
    };

    Detector.Processor<TextBlock> processor = new Detector.Processor<TextBlock>() {
        @Override
        public void release() {
        }

        @Override
        public void receiveDetections(Detector.Detections<TextBlock> detections) {
            final SparseArray<TextBlock> items = detections.getDetectedItems();

            if (items.size() != 0 && doing) {
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();

                        for (int i = 0; i < items.size(); ++i) {
                            TextBlock item = items.valueAt(i);
                            stringBuilder.append(item.getValue());
                            stringBuilder.append("\n");
                        }

                        String[] split = stringBuilder.toString().split("\\s+");
                        String text = "";

                        for (String s : split) {
                            text = text + s;
                        }
                        textView.setText(text);

                        if (checkId(text)) {
                            doing = false;
                            showAlertDialog(text);
                        }
                    }
                });
            }

        }
    };

    private void showAlertDialog(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Serial No : " + text).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ScanBarcodeActivity.this, DeviceDetailActivity.class);
                intent.putExtra("serial", text);
                startActivity(intent);
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ScanBarcodeActivity.this, "Scan Again", Toast.LENGTH_SHORT).show();
                doing = true;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkId(String text) {
        if (!text.contains("DGO")) {
            return false;
        } else if (text.trim().length() != 14) {
            return false;
        } else {
            return text.substring(11).matches("\\d+");
        }
    }

}
