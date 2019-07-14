package com.nz.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
}
