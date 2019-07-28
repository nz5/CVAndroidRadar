package com.nz.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OpenCVCamera";
//    private CameraBridgeViewBase cameraBridgeViewBase;

    JavaCameraView javaCameraView;
    Mat mat1, mat2;
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
        javaCameraView =  findViewById(R.id.opencv_camera_view);
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
    public void onResume(){
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

        mat1 = new Mat(width, height, CvType.CV_16UC4);
        mat2 = new Mat(width, height, CvType.CV_16UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        mRgba =  inputFrame.rgba();
//
//        Imgproc.cvtColor(mRgba, mRgb, Imgproc.COLOR_RGBA2RGB);
//
//        Imgproc.cvtColor(mResult, mRgba, Imgproc.COLOR_BGR2RGBA);

//        Imgproc.cvtColor(mRgba, mRgb, Imgproc.COLOR_RGBA2RGB);
//        Imgproc.cvtColor(mRgb, mHsv, Imgproc.COLOR_RGB2HSV, 3);
//
//        Scalar lowerBound = new Scalar(11, 60, 44);
//        Scalar upperBound = new Scalar(147, 218, 200);
//        Core.inRange(mHsv, lowerBound, upperBound, mMask);
//        Core.bitwise_and(mRgb, mRgb, mResult, mMask);
//
//        Imgproc.cvtColor(mResult, mRgba, Imgproc.COLOR_BGR2RGBA);

//        return mRgba;

        Imgproc.cvtColor(inputFrame.rgba(), mat1, Imgproc.COLOR_BGR2HSV);
        Core.inRange(mat1, scalarLow, scalarHigh, mat2);

        return mat2;
    }

//    public Mat skinDetection(Mat src) {
//        // define the upper and lower boundaries of the HSV pixel
//        // intensities to be considered 'skin'
//        Scalar lower = new Scalar(0, 48, 80);
//        Scalar upper = new Scalar(20, 255, 255);
//
//        // Convert to HSV
//        Mat hsvFrame = new Mat(src.rows(), src.cols(), CvType.CV_8U, new Scalar(3));
//        Imgproc.cvtColor(src, hsvFrame, Imgproc.COLOR_RGB2HSV, 3);
//
//        // Mask the image for skin colors
//        Mat skinMask = new Mat(hsvFrame.rows(), hsvFrame.cols(), CvType.CV_8U, new Scalar(3));
//        Core.inRange(hsvFrame, lower, upper, skinMask);
////        currentSkinMask = new Mat(hsvFrame.rows(), hsvFrame.cols(), CvType.CV_8U, new Scalar(3));
////        skinMask.copyTo(currentSkinMask);
//
//        // apply a series of erosions and dilations to the mask
//        // using an elliptical kernel
//        final Size kernelSize = new Size(11, 11);
//        final Point anchor = new Point(-1, -1);
//        final int iterations = 2;
//
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kernelSize);
//        Imgproc.erode(skinMask, skinMask, kernel, anchor, iterations);
//        Imgproc.dilate(skinMask, skinMask, kernel, anchor, iterations);
//
//        // blur the mask to help remove noise, then apply the
//        // mask to the frame
//        final Size ksize = new Size(3, 3);
//
//        Mat skin = new Mat(skinMask.rows(), skinMask.cols(), CvType.CV_8U, new Scalar(3));
//        Imgproc.GaussianBlur(skinMask, skinMask, ksize, 0);
//        Core.bitwise_and(src, src, skin, skinMask);
//
//        return skin;
//    }

//    private CameraBridgeViewBase mOpenCvCameraView;
//
//    static {
//        if (OpenCVLoader.initDebug()){
//            System.out.println("-----------------------------------yessss-----------------------------------");
//        } else {
//            System.out.println("-----------------------------------noooooo-----------------------------------");
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.activity_camera);
//        mOpenCvCameraView = findViewById(R.id.opencv_camera_view);
//        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
//        mOpenCvCameraView.setCvCameraViewListener(this);
//    }
//
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                {
//                    Log.i("TAG??", "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                } break;
//                default:
//                {
//                    super.onManagerConnected(status);
//                } break;
//            }
//        }
//    };
//
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
//    }
//
//    public void onDestroy() {
//        super.onDestroy();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
////        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
//    }
//
//    @Override
//    public void onCameraViewStarted(int width, int height) {
//
//    }
//
//    @Override
//    public void onCameraViewStopped() {
//
//    }
//
//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        return inputFrame.rgba();
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
}
