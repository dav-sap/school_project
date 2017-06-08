package com.quickode.eyemusic.fragments;

import android.app.Activity;

import com.quickode.eyemusic.R;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Danny on 16-May-16.
 */
public class FaceDetector extends ObjectDetector{

    public FaceDetector(Activity activity) {
        super(activity);
    }

    public Mat detectClosestObject(Mat img, boolean isCropped) {
        Mat mGray = new Mat(img.size(), img.type());
        Imgproc.cvtColor(img, mGray, Imgproc.COLOR_RGB2GRAY);

        // create flipped image for left rotated faces detection
        Mat flippedImg = new Mat();
        Core.flip(mGray, flippedImg, 1);

        // set minimal face rectangle size (5% of image size)
        int minFaceSize = 0;
        int height = mGray.rows();
        minFaceSize = Math.round(height * 0.05f);

        int[] fileResources = {R.raw.lbpcascade_frontalface, R.raw.lbpcascade_profileface};

        loadAllDetectors(fileResources);

        Rect[] frontals = detectObjects(mGray, classifiers[0], minFaceSize);

        Rect[] profiles = detectObjects(mGray, classifiers[1], minFaceSize);

        Rect[] flipProfiles = detectObjects(flippedImg, classifiers[1], minFaceSize);

        Rect closest = (frontals.length > 0) ? findClosestObject(frontals) : new Rect();
        Rect temp = (profiles.length > 0) ? findClosestObject(profiles) : new Rect();
        closest = (closest.height < temp.height) ? temp : closest;
        temp = (flipProfiles.length > 0) ? findClosestObject(flipProfiles) : new Rect();
        closest = (closest.height < temp.height) ? temp : closest;

        if(!isCropped) {
            return (closest.height > 0) ? maskClosestObject(img, closest) : img;
        }
        return (closest.height > 0) ? new Mat(img, closest) : img;
    }
}
