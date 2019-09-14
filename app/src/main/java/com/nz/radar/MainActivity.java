package com.nz.radar;

import android.content.Intent;
import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nz.radar.recording.CameraAbsDiffActivity;

import org.opencv.android.OpenCVLoader;

/**
 * @author Zamanov Nizami
 */
public class MainActivity extends AppCompatActivity {

    private static final int INIT_DISTANCE = 5;
    private static final int MAX_DISTANCE = 15;

    private SeekBar seekBarDistance;
    private TextView seekBarText;
    private int seekBarValue = INIT_DISTANCE;

    private Switch switchDirection;
    public boolean isLeftToRight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (OpenCVLoader.initDebug()){
            Toast.makeText(this, "opencv successfully loaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "opencv load failed", Toast.LENGTH_SHORT).show();
        }
        setUserInputs();
    }



    public void whenGoToCameraActivityButtonPressed(final View view) {
        final Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void whenGoToCameraAbsDiffButtonPressed(final View view) {
        final Intent intent = new Intent(this, CameraAbsDiffActivity.class);
        intent.putExtra(Common.LEFT_TO_RIGHT, isLeftToRight);
        intent.putExtra(Common.DISTANCE, seekBarValue);

        startActivity(intent);
    }

    public void whenGoToCalibrationActivityButtonPressed(final View view) {
        final Intent intent = new Intent(this, Calibration.class);
        startActivity(intent);
    }

    private void setUserInputs(){
        seekBarDistance = findViewById(R.id.seekBar_distance);
        seekBarDistance.setMax(MAX_DISTANCE);
        seekBarDistance.setProgress(INIT_DISTANCE);
        seekBarText = findViewById(R.id.text_distance);
        seekBarText.setText(INIT_DISTANCE + " meters");
        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarDistance.setProgress(progress);
                seekBarText.setText(progress + " meters");
                seekBarValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        switchDirection = findViewById(R.id.switch_dir);
        isLeftToRight = switchDirection.isChecked();
        switchDirection.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLeftToRight = isChecked;
            }
        });
    }

}
