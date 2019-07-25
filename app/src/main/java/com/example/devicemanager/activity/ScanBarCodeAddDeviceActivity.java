package com.example.devicemanager.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.devicemanager.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pax.api.scanner.ScanResult;

import java.io.IOException;

import co.th.digio.sdk.ScanDecoder;
import co.th.digio.sdk.model.IDecoderCallback;

public class ScanBarCodeAddDeviceActivity extends AppCompatActivity implements SurfaceHolder.Callback,IDecoderCallback{

    private boolean scanEnable = true;
    final int RequestCameraPermissionID = 1001;
    private static final int MAX_FRAME_COUNT = 10;
    private final String TAG = "paxscanlitetest";
    // 0:500?? 1:200??
    int cameraId = 0; //?????
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private IDecoderCallback decoderCallback = this;
    private Camera mCamera;
    private TextView tvBarcode;
    private int IMAGE_WIDTH = 1280;//640;
    private int IMAGE_HEIGHT = 720;//480;
    private byte[] preivewBuf;
    private int frameCount = 0;
    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera arg1) {

            if (mCamera == null || arg1 == null) {
                return;
            }

            frameCount++;
            if (frameCount > MAX_FRAME_COUNT) {
                frameCount = 0;
                startAutoFocus();
            }

            new ScanDecoder(ScanBarCodeAddDeviceActivity.this, decoderCallback).execute(data);
            if (mCamera != null) {
                mCamera.addCallbackBuffer(data);
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bar_code_add_device);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ScanBarCodeAddDeviceActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    RequestCameraPermissionID);
            return;
        }
        setContentView(R.layout.activity_scan_qr_code);
        initView();
        preivewBuf = new byte[2 * IMAGE_WIDTH * IMAGE_HEIGHT];
        initCamera(cameraId);
    }

    private void initView() {
        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(3);
        mSurfaceHolder.addCallback(this);

        tvBarcode = findViewById(R.id.text_view);


    }

    private void startAutoFocus() {
        if (mCamera != null) {
            mCamera.autoFocus(mAutoFocusCallback);
        }
    }

    private void initCamera(int cameraId) {
        mCamera = Camera.open(cameraId);
        mCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = mCamera.getParameters();

//		parameters.setExposureCompensation(-3); //???????

        //?????????????
        parameters.setPreviewSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        parameters.setPictureSize(IMAGE_WIDTH, IMAGE_HEIGHT);

        //????????
        int zoomValue = parameters.getZoom();
        parameters.setZoom(zoomValue + 15);
        mCamera.setParameters(parameters);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        synchronized (this) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            startPreview();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
    }

    private void startPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
            mCamera.addCallbackBuffer(preivewBuf);
            mCamera.startPreview();
        }
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
        }
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
        SystemClock.sleep(100);

    }

    @Override
    public void onSuccess(ScanResult scanResult) {
        String result = "Data: " + scanResult.getContent();
        tvBarcode.setText(result);
        if (scanResult.getContent() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            finish();

        } else {
            if(!scanEnable){
                return;
            }
            scanEnable =false;
            Intent intentBack = new Intent();
            intentBack.putExtra("serial", scanResult.getContent());
            setResult(RESULT_OK, intentBack);
            finish();
            scanEnable =true;
        }
    }

    @Override
    public void onFailure(Exception e) {
        Toast.makeText(ScanBarCodeAddDeviceActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
