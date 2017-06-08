package com.quickode.eyemusic.fragments;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.quickode.eyemusic.R;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Danny on 20/04/2016.
 */
public abstract class ObjectDetector {

    private static final String TAG = "OCVSample::Activity";
//    private CascadeClassifier mFrontalDetector, mProfileDetector;
    protected CascadeClassifier[] classifiers;
    private Activity activity;

    public ObjectDetector(Activity activity) {
        this.activity = activity;
    }

    protected void loadAllDetectors(int[] fileResources) {
        classifiers = new CascadeClassifier[fileResources.length];
        for (int i = 0; i < fileResources.length; i++) {
            classifiers[i] = loadClassifier(fileResources[i], "classifier_" + i + ".xml");
        }
    }

    /*
    * Loads cascade file from application resources into a local detector.
    */
    private CascadeClassifier loadClassifier(int resource, String filename) {
        try {
            InputStream is = activity.getResources().openRawResource(resource);
            File cascadeDir = activity.getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, filename);
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            CascadeClassifier classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if (classifier.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                classifier = null;
            } else {
                Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());
            }
            cascadeDir.delete();
            return classifier;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
            return null;
        }
    }

    /**
     * Detects the closest object in an image (if exists), and shows only the object area.
     * @param img input image
     * @param isCropped determines whether to pad the rest of the image with zeros or show the
     *                  object in full size
     * @return An image with the object area
     */
    public abstract Mat detectClosestObject(Mat img, boolean isCropped);

    /*
    * Receives an image and a detector, and returns an array of all the rectangles with at least the
    * minimal size where the detected objects were found.
    */
    protected static Rect[] detectObjects(Mat img, CascadeClassifier detector, int minRectSize) {
        MatOfRect objects = new MatOfRect();
        detector.detectMultiScale(img, objects, 1.1, 2, 2, new Size(minRectSize, minRectSize),
                new Size());
        return objects.toArray();
    }

    /*
    * Receives an array of rectangles with faces, and returns the rectangle where the closest face
    * is lying in. The closest face is determined by the largest height of a rectangle.
    */
    protected static Rect findClosestObject(Rect[] objectsArr) {
        int closest = 0;
        for (int i = 0; i < objectsArr.length; i++) {
            int size = objectsArr[i].height; // height is same scale for frontal and profile
            if(size > objectsArr[closest].height) {
                closest = i;
            }
        }
        return objectsArr[closest];
    }

    protected static Mat maskClosestObject(Mat img, Rect closest) {
        Mat object = new Mat(img, closest);
        Mat result = new Mat(img.size(), img.type(), new Scalar(0, 0, 0, 255));
        Point tl = closest.tl();
        Point br = closest.br();
        object.copyTo(result.colRange((int)tl.x, (int)br.x).rowRange((int) tl.y, (int)br.y));
        return result;
    }
}
