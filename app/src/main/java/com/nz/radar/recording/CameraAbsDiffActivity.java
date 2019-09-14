package com.nz.radar.recording;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

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

    JavaCameraView javaCameraView;
    Mat mHsv, mMask, mOriginal, mat4, mGaussianBlue, mErode, mDilate;
    Mat frame1, frame2, frame3, delta1, delta2, delta1Thresh, delta2Thresh, melta1, melta2;
    Mat canny;
    Mat houghCircle;
    Scalar scalarLow, scalarHigh;
    int counter;
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

    private static int GOAL_LINE_WIDTH = 3;
    private static int GOAL_LINE_MARGIN_X = 70;
    private static int GOAL_LINE_MARGIN_Y = 50;

    private boolean rightToLeft = true;


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

        scalarLow = new Scalar(45, 20, 10);
        scalarHigh = new Scalar(75, 255, 255);
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
        if (rightToLeft) {
            Imgproc.line(mat,
                    new Point(GOAL_LINE_MARGIN_X, GOAL_LINE_MARGIN_Y),
                    new Point(GOAL_LINE_MARGIN_X, mat.height() - GOAL_LINE_MARGIN_Y),
                    new Scalar(250, 255, 255), GOAL_LINE_WIDTH);
        } else {
            Imgproc.line(mat,
                    new Point(mat.width() - GOAL_LINE_MARGIN_X, GOAL_LINE_MARGIN_Y),
                    new Point(mat.width() - GOAL_LINE_MARGIN_X, mat.height() - GOAL_LINE_MARGIN_Y),
                    new Scalar(250, 255, 255), GOAL_LINE_WIDTH);
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

        frame1 = frame2;
        frame2 = frame3;
        counter++;
        return delta1;
    }

//        Imgproc.threshold(delta2, delta2, 5, 255, Imgproc.THRESH_BINARY);
//        Core.absdiff(delta1Thresh, delta2Thresh, melta1);
//        Imgproc.threshold(melta1, melta2, 2, 255, Imgproc.THRESH_BINARY);
//        Core.absdiff(delta1, delta2, delta1Thresh);
//        Imgproc.threshold(delta1Thresh, delta2Thresh, 1, 255, Imgproc.THRESH_BINARY);


//        Imgproc.Canny(delta1, canny, 1, 3, 3, false);
//        Imgproc.HoughCircles(canny, houghCircle, Imgproc.HOUGH_GRADIENT, (double)canny.size().width/16, 2);
//        Imgproc.findContours(delta1, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//        double maxArea = 0;
//        float[] radius = new float[1];
//        Point center = new Point();
//        for (int i = 0; i < contours.size(); i++) {
//            MatOfPoint c = contours.get(i);
//            if (Imgproc.contourArea(c) > maxArea) {
//                MatOfPoint2f c2f = new MatOfPoint2f(c.toArray());
//                Imgproc.minEnclosingCircle(c2f, center, radius);
//            }
//        }
//        Imgproc.circle(mOriginal, center, (int)radius[0], new Scalar(255, 0, 0), 2);



//        Imgproc.cvtColor(mOriginal, mHsv, Imgproc.COLOR_BGR2HSV);
//        Core.inRange(mHsv, scalarLow, scalarHigh, mMask);
//        int erosion_size = 5;
//        int dilation_size = 5;
//        Mat strcuturingElementErode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
//        Mat strcuturingElementDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
//        Imgproc.erode(mMask, mErode, strcuturingElementErode);
//        Imgproc.dilate(mErode, mDilate, strcuturingElementDilate);
//
//        List<MatOfPoint> contours = new ArrayList<>();
//        Imgproc.findContours(mDilate, contours, mat4, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//        Log.w(TAG, "--------------------------contour length=" + contours.size());
//
//        int maxIndex = 0;
//        double maxArea = 0;
//        for (int i=0; i < contours.size(); i++) {
//            double tempArea = Imgproc.contourArea(contours.get(i));
//            if (tempArea > maxArea) {
//                maxArea = tempArea;
//                maxIndex = i;
//
//                //TODO find max of bounding box and draw only that
//                Rect rect = Imgproc.boundingRect(contours.get(i));
//                Imgproc.rectangle(mOriginal, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,255,0));
//            }
//        }
//
//        return mOriginal;
}
