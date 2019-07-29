package com.nz.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OpenCVCamera";
//    private CameraBridgeViewBase cameraBridgeViewBase;

    JavaCameraView javaCameraView;
    Mat mHsv, mMask, mOriginal, mat4, mGaussianBlue, mErode, mDilate;
    Scalar scalarLow, scalarHigh;

//    Mat mRgba =  new Mat();
//    Mat mRgb = new Mat();
//    Mat mHsv = new Mat();
//    Mat mMask = new Mat();
//    Mat mResult = new Mat();

//    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                    cameraBridgeViewBase.enableView();
//                    break;
//                default:
//                    super.onManagerConnected(status);
//                    break;
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
//        cameraBridgeViewBase = findViewById(R.id.opencv_camera_view);
//        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
//        cameraBridgeViewBase.setCvCameraViewListener(this);

        OpenCVLoader.initDebug();
        javaCameraView = findViewById(R.id.opencv_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
//        javaCameraView.setCameraIndex(0);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        javaCameraView.enableView();
//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, baseLoaderCallback);
//        } else {
//            Log.d(TAG, "OpenCV library found inside package. Using it!");
//            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.e(TAG, "@______@__________@________@________@camera started");

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

        int erosion_size = 5;
        int dilation_size = 5;
        Mat strcuturingElementErode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
        Mat strcuturingElementDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
        Imgproc.erode(mMask, mErode, strcuturingElementErode);
        Imgproc.dilate(mErode, mDilate, strcuturingElementDilate);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mDilate, contours, mat4, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Log.w(TAG, "--------------------------contour length=" + contours.size());

        int maxIndex = 0;
        double maxArea = 0;
        for (int i=0; i < contours.size(); i++) {
            double tempArea = Imgproc.contourArea(contours.get(i));
            if (tempArea > maxArea) {
                maxArea = tempArea;
                maxIndex = i;

                //TODO find max of bounding box and draw only that
                Rect rect = Imgproc.boundingRect(contours.get(i));
                Imgproc.rectangle(mOriginal, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,255,0));
            }
//            if (Imgproc.contourArea(contours.get(i)) > 3 ){
//                Rect rect = Imgproc.boundingRect(contours.get(i));
//                Imgproc.rectangle(mOriginal, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,255,0));
//            }
        }

//        Rect rect = Imgproc.boundingRect(contours.get(maxIndex));
//        Imgproc.rectangle(mOriginal, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0));

        return mOriginal;
    }



}
