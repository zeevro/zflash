package com.zeevro.zflash;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {

    static final int cameraPermissionRequestCode = 1234;

    Button myButton;
    boolean isTurnedOn = false;
    String myCameraId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "This device has no flash!", Toast.LENGTH_LONG).show();
            finish();
        }

        myButton = findViewById(R.id.powerButton);

        requestPermissions(new String[] {Manifest.permission.CAMERA}, cameraPermissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case cameraPermissionRequestCode:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "No permission to access camera!", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                CameraManager cameraManager = (CameraManager)getSystemService(CAMERA_SERVICE);
                try {
                    for (String cameraId : cameraManager.getCameraIdList()) {
                        CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                        if (cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                            myCameraId = cameraId;
                            break;
                        }
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.powerButton:
                handlePowerButton();
                break;
        }
    }

    private void handlePowerButton() {
        isTurnedOn = !isTurnedOn;
        CameraManager cameraManager = (CameraManager)getSystemService(CAMERA_SERVICE);
        try {
            cameraManager.setTorchMode(myCameraId, isTurnedOn);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
