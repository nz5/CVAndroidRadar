package com.nz.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Calibration extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OpenCVCamera";
    private static final String H_HIGH = "H-high: ";
    private static final String H_LOW = "H-low: ";
    private static final String S_HIGH = "S-high: ";
    private static final String S_LOW = "S-low: ";
    private static final String V_HIGH = "V-high: ";
    private static final String V_LOW = "V-low: ";
    private static final String ERODE_TEXT = "Erode: ";
    private static final String DILATE_TEXT = "Dilate: ";

    private int erosion_size = 5;
    private int dilation_size = 5;

    JavaCameraView javaCameraView;
    Mat mHsv, mMask, mOriginal, mat4, mGaussianBlue, mErode, mDilate;
    Scalar scalarLow, scalarHigh;

    private SeekBar hHigh;
    private SeekBar hLow;
    private SeekBar sHigh;
    private SeekBar sLow;
    private SeekBar vHigh;
    private SeekBar vLow;
    private SeekBar erode;
    private SeekBar dilate;

    private TextView textHHigh;
    private TextView textHLow;
    private TextView textSHigh;
    private TextView textSLow;
    private TextView textVHigh;
    private TextView textVLow;
    private TextView textErode;
    private TextView textDilate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_calibration);

        OpenCVLoader.initDebug();
        javaCameraView = findViewById(R.id.opencv_camera_view_calibration);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.enableView();

        scalarLow = new Scalar(45, 20, 10);
        scalarHigh = new Scalar(75, 255, 255);

        configureSeekBars();
    }

    private void configureSeekBars(){
        hHigh = findViewById(R.id.seekBar_h_high);
        hLow = findViewById(R.id.seekBar_h_low);
        sHigh = findViewById(R.id.seekBar_s_high);
        sLow = findViewById(R.id.seekBar_s_low);
        vHigh = findViewById(R.id.seekBar_v_high);
        vLow = findViewById(R.id.seekBar_v_low);
        erode = findViewById(R.id.seekBar_erode);
        dilate = findViewById(R.id.seekBar_dilate);

        hHigh.setMax(180);
        hLow.setMax(180);
        sHigh.setMax(360);
        sLow.setMax(360);
        vHigh.setMax(360);
        vLow.setMax(360);
        erode.setMax(20);
        dilate.setMax(20);

        hHigh.setProgress(75);
        hLow.setProgress(45);
        sHigh.setProgress(255);
        sLow.setProgress(30);
        vHigh.setProgress(255);
        vLow.setProgress(30);
        erode.setProgress(5);
        dilate.setProgress(5);

        setSeekBarListeners();
    }



    @Override
    public void onCameraViewStarted(int width, int height) {
        mHsv = new Mat(width, height, CvType.CV_8UC4);
        mMask = new Mat(width, height, CvType.CV_8UC4);
        mOriginal = new Mat(width, height, CvType.CV_8UC4);
        mat4 = new Mat(width, height, CvType.CV_8UC4);
        mGaussianBlue = new Mat(width, height, CvType.CV_8UC4);
        mErode = new Mat(width, height, CvType.CV_8UC4);
        mDilate = new Mat(width, height, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mOriginal = inputFrame.rgba();
        Imgproc.GaussianBlur(mOriginal, mOriginal,new Size(15,15), 0);
        Imgproc.cvtColor(mOriginal, mHsv, Imgproc.COLOR_BGR2HSV);
        Core.inRange(mHsv, scalarLow, scalarHigh, mMask);

//        int erosion_size = 5;
//        int dilation_size = 5;
        Mat strcuturingElementErode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
        Mat strcuturingElementDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
        Imgproc.erode(mMask, mErode, strcuturingElementErode);
        Imgproc.dilate(mErode, mDilate, strcuturingElementDilate);
        return mDilate;
    }

    private void setSeekBarListeners(){


        textErode = findViewById(R.id.textView_erode);
        textErode.setText(ERODE_TEXT.concat(String.valueOf(erode.getProgress())));
        erode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                erode.setProgress(progress);
                textErode.setText(ERODE_TEXT.concat(String.valueOf(progress)));
                erosion_size = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textDilate = findViewById(R.id.textView_dilate);
        textDilate.setText(DILATE_TEXT.concat(String.valueOf(dilate.getProgress())));
        dilate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dilate.setProgress(progress);
                textDilate.setText(DILATE_TEXT.concat(String.valueOf(progress)));
                dilation_size = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        textHHigh = findViewById(R.id.textView_hhigh);
        textHHigh.setText(H_HIGH.concat(String.valueOf(hHigh.getProgress())));
        hHigh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hHigh.setProgress(progress);
                textHHigh.setText(H_HIGH.concat(String.valueOf(progress)));
                double h = scalarHigh.val[0];
                double s = scalarHigh.val[1];
                double v = scalarHigh.val[2];
                scalarHigh = new Scalar(progress, s, v);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textHLow = findViewById(R.id.textView_hlow);
        textHLow.setText(H_LOW.concat(String.valueOf(hLow.getProgress())));
        hLow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hLow.setProgress(progress);
                textHLow.setText(H_LOW.concat(String.valueOf(progress)));
                double h = scalarLow.val[0];
                double s = scalarLow.val[1];
                double v = scalarLow.val[2];
                scalarLow = new Scalar(progress, s, v);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textSHigh = findViewById(R.id.textView_shigh);
        textSHigh.setText(S_HIGH.concat(String.valueOf(sHigh.getProgress())));
        sHigh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sHigh.setProgress(progress);
                textSHigh.setText(S_HIGH.concat(String.valueOf(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textSLow = findViewById(R.id.textView_slow);
        textSLow.setText(S_LOW.concat(String.valueOf(sLow.getProgress())));
        sLow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sLow.setProgress(progress);
                textSLow.setText(S_LOW.concat(String.valueOf(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textVHigh = findViewById(R.id.textView_vhigh);
        textVHigh.setText(V_HIGH.concat(String.valueOf(vHigh.getProgress())));
        vHigh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vHigh.setProgress(progress);
                textVHigh.setText(V_HIGH.concat(String.valueOf(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textVLow = findViewById(R.id.textView_vlow);
        textVLow.setText(V_LOW.concat(String.valueOf(vLow.getProgress())));
        vLow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vLow.setProgress(progress);
                textVLow.setText(V_LOW.concat(String.valueOf(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
