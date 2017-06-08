package com.quickode.eyemusic.fragments;

import android.app.Activity;

import com.quickode.eyemusic.R;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Danny on 17-May-16.
 */
public class FullBodyDetector extends ObjectDetector{

    public FullBodyDetector(Activity activity) {
        super(activity);
    }

    public Mat detectClosestObject(Mat img, boolean isCropped) {
        Mat mGray = new Mat(img.size(), img.type());
        Imgproc.cvtColor(img, mGray, Imgproc.COLOR_RGB2GRAY);

        // set minimal upper body rectangle size (5% of image size)
        int minFaceSize = 0;
        int height = mGray.rows();
        minFaceSize = Math.round(height * 0.05f);

        int[] fileResources = {R.raw.haarcascade_fullbody};

        loadAllDetectors(fileResources);

        Rect[] fullBodies = detectObjects(mGray, classifiers[0], minFaceSize);

        Rect closest = (fullBodies.length > 0) ? findClosestObject(fullBodies) : new Rect();

        if(!isCropped) {
            return (closest.height > 0) ? maskClosestObject(img, closest) : img;
        }
        return (closest.height > 0) ? new Mat(img, closest) : img;
    }
}
