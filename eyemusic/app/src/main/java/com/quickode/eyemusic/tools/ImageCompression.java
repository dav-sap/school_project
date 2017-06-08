package com.quickode.eyemusic.tools;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.quickode.eyemusic.R;
import com.quickode.eyemusic.fragments.PlayCamera;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.*;
import com.quickode.eyemusic.interfaces.AsyncResponse;
import com.quickode.eyemusic.models.AppSettings;

public class ImageCompression extends AsyncTask<String, Void, String>{
    private Context context;
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;
    private ProgressDialog progressDialog;
//    private static final String serverIp = "132.65.251.187";
//    private static final String serverPort = "3123";
    public AsyncResponse delegate = null;


    public ImageCompression(Context context, PlayCamera pc){
        this.context=context;
        this.delegate = pc;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = ProgressDialog.show(this.context, "Please wait...", "Analyzing photo ...", true);

    }
    @Override
    protected String doInBackground(String... strings) {
        if(strings.length == 0 || strings[0] == null)
            return null;
        String filePath = compressImage(strings[0]);


        return this.execPost(filePath);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            if (result != null) {
                delegate.processFinish(result);
            }
        }
    }

    private String execPost(String imagePath){
        // imagePath is path of new compressed image.
        String attachmentName = "my_img";

        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        //Create connection
        try{
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL("http://" + AppSettings.ip +":" + AppSettings.port);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //Paths.get(attachmentFileName)
            File inputFile = new File(imagePath);
            InputStream fileStream = new BufferedInputStream(new FileInputStream(inputFile));
            ByteArrayOutputStream fileBuffer = new ByteArrayOutputStream();
            int nRead;
            byte[] tempData = new byte[16384];
            while ((nRead = fileStream.read(tempData, 0, tempData.length)) != -1) {
                fileBuffer.write(tempData, 0, nRead);
            }
            byte[] byteArray = fileBuffer.toByteArray();
            fileStream.close();
//					byte[] byteArray = Files.readAllBytes(path);

//					Bitmap bitmap = get your bit map;
//					byte[] byteArray = stream.toByteArray();

            DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\"" + crlf);
            request.writeBytes("Content-Type: image/jpeg" + crlf);
            request.writeBytes(crlf);
            request.write(byteArray);
            request.writeBytes(crlf);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"testingName\"" + crlf);
            request.writeBytes(crlf);
            request.writeBytes(imagePath);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens);
            request.flush();
            request.close();
            InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            nRead = 0;
            byte[] data = new byte[16384];
            while ((nRead = responseStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
//					Path file = Paths.get();

            String newFilePath = getFilename("CameraDemoAnalyzed");

            FileOutputStream out = new FileOutputStream(newFilePath);
            out.write(buffer.toByteArray());
            return newFilePath;

        }
        catch (Exception exception){
            Log.e("Error", exception.getMessage());
            return null;
        }

    }

    public String compressImage(String imagePath) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if(bmp!=null)
        {
            bmp.recycle();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filepath = getFilename("CameraDemoCompressed");
        try {
            out = new FileOutputStream(filepath);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filepath;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String getFilename(String folder) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), folder);

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d(folder, "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String mImageName = "IMG_"+ timeStamp + ".jpg";

        String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);
        return uriString;

    }

}
