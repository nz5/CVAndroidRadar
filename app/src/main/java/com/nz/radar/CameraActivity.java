package com.nz.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OpenCVCamera";
    private CameraBridgeViewBase cameraBridgeViewBase;

    Mat mRgba =  new Mat();
    Mat mRgb = new Mat();
    Mat mHsv = new Mat();
    Mat mMask = new Mat();
    Mat mResult = new Mat();

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    cameraBridgeViewBase.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        cameraBridgeViewBase = findViewById(R.id.opencv_camera_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.e(TAG, "@______@__________@________@________@camera started");
        Log.e(TAG, "width=" + width + ", height=" + height);

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba =  inputFrame.rgba();




        Imgproc.cvtColor(mRgba, mRgb, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(mRgb, mHsv, Imgproc.COLOR_RGB2HSV, 3);
//        Imgproc.cvtColor(mHsv, mRgba, Imgproc.COLOR_RGB2RGBA, 4);
//        Imgproc.cvtColor(mHsv, mRgb, Imgproc.COLOR_HSV2RGB, 3);
//        Imgproc.cvtColor(mRgb, mRgba, Imgproc.COLOR_RGB2RGBA);
//        Imgproc.cvtColor(mRgba, mHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Convert to HSV
//        Mat hsvFrame = new Mat(mRgb.rows(), mRgb.cols(), CvType.CV_8U, new Scalar(3));
//        Imgproc.cvtColor(mRgb, hsvFrame, Imgproc.COLOR_RGB2HSV, 3);

//
//
        Scalar lowerBound = new Scalar(11, 0, 0);
        Scalar upperBound = new Scalar(179, 218, 213);
//
        Core.inRange(mHsv, lowerBound, upperBound, mMask);
        Core.bitwise_and(mRgb, mRgb, mResult, mMask);
//        Core.bitwise_and(mRgba, mRgba, mResult, mMask);
//
        Imgproc.cvtColor(mResult, mRgba, Imgproc.COLOR_BGR2RGBA);
//        Imgproc.cvtColor(mRgb, result, Imgproc.COLOR_RGB2RGBA);
//        return mRgb;
        return mRgba;

    }

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
