package com.nz.radar.recording;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nz.radar.Common;
import com.nz.radar.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class CameraAbsDiffActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = "OpenCVCamera";

    private RadioGroup radioGroupView;
    private RadioGroup radioGroupShape;

    private boolean radioBlackWhite = true;
    private boolean radioOriginal = false;
    private boolean radioCircle = false;
    private boolean radioRectangle = true;




    JavaCameraView javaCameraView;
    Mat mHsv, mMask, mOriginal, mat4, mGaussianBlue, mErode, mDilate;
    Mat frame1, frame2, frame3, delta1, delta2, delta1Thresh, delta2Thresh, melta1, melta2;
    Mat canny;
    Mat houghCircle;
    Scalar scalarLow, scalarHigh;
    int counter;


    private boolean isLeftToRight;
    private int ballToGoalDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera_abs_diff);

        OpenCVLoader.initDebug();
        javaCameraView = findViewById(R.id.opencv_camera_absDiff_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.enableView();

        isLeftToRight = getIntent().getExtras().getBoolean(Common.LEFT_TO_RIGHT);
        ballToGoalDistance = getIntent().getExtras().getInt(Common.DISTANCE);

        configureRadioButtons();

        scalarLow = new Scalar(45, 20, 10);
        scalarHigh = new Scalar(75, 255, 255);
    }

    private void configureRadioButtons(){
        radioGroupView = findViewById(R.id.absDiff_view);
        radioGroupShape = findViewById(R.id.absDiff_shape);

        final RadioButton radioButtonViewBlack = findViewById(R.id.radio_blackWhite);
        radioButtonViewBlack.setChecked(false);
        final RadioButton radioButtonViewOriginal = findViewById(R.id.radio_original);
        radioButtonViewOriginal.setChecked(true);
        final RadioButton radioButtonShapeRect = findViewById(R.id.radio_rectange);
        radioButtonShapeRect.setChecked(true);
        final RadioButton radioButtonShapeCircle = findViewById(R.id.radio_circle);
        radioButtonShapeCircle.setChecked(false);

        radioGroupView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final RadioButton radioButton = findViewById(checkedId);
                final String radioTextValue = radioButton.getText().toString();
                switch (radioTextValue){
                    case "radio-blackWhite":
                        radioBlackWhite = true;
                        radioOriginal = false;
                        Log.w(TAG, "--------------------------BLACK WHITE ");

                        break;

                    case "radio-original":
                        radioBlackWhite = false;
                        radioOriginal = true;
                        Log.w(TAG, "--------------------------ORIGINAL ");

                        break;

                }
            }
        });

        radioGroupShape.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final RadioButton radioButton = findViewById(checkedId);
                final String radioTextValue = radioButton.getText().toString();
                switch (radioTextValue){
                    case "radio-rectange":
                        radioRectangle = true;
                        radioCircle = false;
                        Log.w(TAG, "--------------------------RECT ");
                        break;

                    case "radio-circle":
                        radioRectangle = false;
                        radioCircle = true;
                        Log.w(TAG, "--------------------------CIRCLE ");
                        break;
                }
            }
        });
    }

            @Override
    protected void onPause() {
        super.onPause();
        javaCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        javaCameraView.disableView();
        counter = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        javaCameraView.enableView();
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
        frame1 = new Mat(width, height, CvType.CV_8UC4);
        frame2 = new Mat(width, height, CvType.CV_8UC4);
        frame3 = new Mat(width, height, CvType.CV_8UC4);
        delta1 = new Mat(width, height, CvType.CV_8UC4);
        delta2 = new Mat(width, height, CvType.CV_8UC4);
        delta1Thresh = new Mat(width, height, CvType.CV_8UC4);
        delta2Thresh = new Mat(width, height, CvType.CV_8UC4);
        melta1 = new Mat(width, height, CvType.CV_8UC4);
        melta2 = new Mat(width, height, CvType.CV_8UC4);
        canny = new Mat(width, height, CvType.CV_8UC4);
        houghCircle = new Mat(width, height, CvType.CV_8UC4);
        counter = 0;
    }

    @Override
    public void onCameraViewStopped() {

    }

    private void drawGoalLine(Mat mat){
        if (!isLeftToRight) {
            Imgproc.line(mat,
                    new Point(Common.GOAL_LINE_MARGIN_X, Common.GOAL_LINE_MARGIN_Y),
                    new Point(Common.GOAL_LINE_MARGIN_X, mat.height() - Common.GOAL_LINE_MARGIN_Y),
                    new Scalar(250, 255, 255), Common.GOAL_LINE_THICKNESS);
        } else {
            Imgproc.line(mat,
                    new Point(mat.width() - Common.GOAL_LINE_MARGIN_X, Common.GOAL_LINE_MARGIN_Y),
                    new Point(mat.width() - Common.GOAL_LINE_MARGIN_X, mat.height() - Common.GOAL_LINE_MARGIN_Y),
                    new Scalar(250, 255, 255), Common.GOAL_LINE_THICKNESS);
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mOriginal = inputFrame.rgba();

        Imgproc.cvtColor(mOriginal, mOriginal, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(mOriginal, mOriginal, new Size(15, 15), 0);

        if (counter == 0) {
            frame1 = mOriginal;
            counter++;
            return frame1;
        }
        if (counter == 1) {
            frame2 = mOriginal;
            counter++;
            return frame2;
        }
        frame3 = mOriginal;

        Core.absdiff(frame2, frame3, delta1);
        Imgproc.threshold(delta1, delta1, 5, 255, Imgproc.THRESH_BINARY);

        drawGoalLine(delta1);



        final List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(delta1, contours, delta2, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//        Log.w(TAG, "--------------------------contour length=" + contours.size());

        if (radioBlackWhite) {
            if (radioRectangle) {
                drawBoundingBox(contours, delta1);

            } else {
                drawBoundingCircle(contours, delta1);
            }
            frame1 = frame2;
            frame2 = frame3;
            counter++;
            return delta1;
        } else {
            if (radioRectangle) {
                drawBoundingBox(contours, mOriginal);
            } else {
                drawBoundingCircle(contours, mOriginal);
            }
            frame1 = frame2;
            frame2 = frame3;
            counter++;
            return mOriginal;
        }

//        frame1 = frame2;
//        frame2 = frame3;
//        counter++;
//        return delta1;
    }

    private void drawBoundingBox(final List<MatOfPoint> contours, final Mat mat){
        for (int i=0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            Imgproc.rectangle(mat, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255,255,255), Common.GOAL_LINE_THICKNESS);
        }
    }

    private void drawBoundingCircle(final List<MatOfPoint> contours, final Mat mat){
        float[] radius = new float[1];
        Point center = new Point();

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint c = contours.get(i);
            MatOfPoint2f c2f = new MatOfPoint2f(c.toArray());
            Imgproc.minEnclosingCircle(c2f, center, radius);
            Imgproc.circle(mat, center, (int)radius[0], new Scalar(255, 255, 255), Common.GOAL_LINE_THICKNESS);
        }

    }
}
