package com.nz.radar;

import android.content.Intent;
import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

/**
 * @author Zamanov Nizami
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (OpenCVLoader.initDebug()){
            Toast.makeText(this, "opencv successfully loaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "opencv load failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void whenGoToCameraActivityButtonPressed(final View view) {
        final Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void whenGoToCalibrationActivityButtonPressed(final View view) {
        final Intent intent = new Intent(this, Calibration.class);
        startActivity(intent);
    }

}
