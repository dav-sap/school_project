package com.quickode.eyemusic.fragments;


import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by davidgefen on 22/03/2016.
 */
public class Cartoonize {

    //size of kernal for median blur -- should be uneven
    private static final int MEDIAN_KERNEL_SIZE = 11;
    //first threshold for the hysteresis procedure in canny in edge detection
    private static final int THRESHOLD1 = 10;
    //second threshold for the hysteresis procedure in canny in edge detection
    private static final int THRESHOLD2 = 100;
    //aperture size for the Sobel() operator in canny in edge detection.
    //should be uneven
    private static final int APERTURE_SIZE = 3;
    //number of color per chanel in quantization
    private static final int NUM_COLORS = 3;

    /**
     * this method cartoonizes an image
     * @param image a matrix of the received image
     * @return the matrix after cartoonization
     */
    public Mat cartoonize(Mat image){

        //cartoonize matrix
        Mat derived = edgeDetection(image);
        Log.i("cartoonize", "Got Edges of image");
        image = MedianBlur(image);
        Log.i("cartoonize", "Blured image");
        image = quantizeColor(image, derived);
        Log.i("cartoonize", "Quantized image");

        return image;
    }

    /**
     * Blures an image using median blure
     * @param original image to blur
     * @return blured image
     */
    private Mat MedianBlur(Mat original) {
        Mat blured = new Mat(original.size(), original.type());
        Imgproc.medianBlur(original, blured, MEDIAN_KERNEL_SIZE);
        return blured;
    }

    /**
     * detects the edges in the image by deriving it
     * @param original image to detect edges of
     * @return matrix with edges
     */
    private Mat edgeDetection(Mat original){
        Mat gray = new Mat(original.size(), original.type());
        Imgproc.cvtColor(original, gray, Imgproc.COLOR_RGB2GRAY);
        Mat edges = new Mat(original.size(), original.type());
//        Imgproc.adaptiveThreshold(gray, gray, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 4);
        Imgproc.Canny(gray, edges, THRESHOLD1, THRESHOLD2, APERTURE_SIZE, true);
        return gray;
    }

    /**
     * Quantizes the image with truncation,and adds the edges
     * @param image image to quantize
     * @param edges edges to be added
     * @return resulting image
     */
    private Mat quantizeColor(Mat image, Mat edges){
        Mat temp1 = new Mat(image.size(), image.type());
        Mat temp2 = new Mat(image.size(), image.type());
        Mat derived = new Mat(image.size(), image.type());
        Mat result = new Mat(image.size(), image.type());

        double firstScalar = 1;
        firstScalar /= NUM_COLORS;
        Scalar fraction = new Scalar(firstScalar, firstScalar, firstScalar, firstScalar);
        Core.multiply(image, fraction, temp1);
        Scalar normalizer = new Scalar(NUM_COLORS, NUM_COLORS, NUM_COLORS, NUM_COLORS);
        Core.multiply(temp1, normalizer, temp2);

//        Imgproc.cvtColor(edges, derived, Imgproc.COLOR_GRAY2BGRA);
//        Mat thresh = new Mat();
//        Imgproc.threshold(derived, thresh, 127, 255, Imgproc.THRESH_BINARY_INV);
//        Core.subtract(temp2, thresh, result);

        return temp2;
    }

//    private void printMat(Mat mat) {
//        for (int row = 0; row < mat.rows(); row++) {
//            for (int col = 0; col < mat.cols(); col++) {
//                Log.i("readImage", "image has been read. row: " + row + " col: " + col + " is: " + mat.get(row, col).toString());
//            }
//        }
//    }
}
