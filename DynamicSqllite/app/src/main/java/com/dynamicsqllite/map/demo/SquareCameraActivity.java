package com.dynamicsqllite.map.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.dynamicsqllite.ActBase;
import com.dynamicsqllite.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by prashant.patel on 5/5/2017.
 */

public class SquareCameraActivity extends ActBase implements SurfaceHolder.Callback {
    // a variable to store a reference to the Image View at the main.xml file
// private ImageView iv_image;
// a variable to store a reference to the Surface View at the main.xml file
    private SurfaceView sv;

    // a bitmap to display the captured image
    private Bitmap bmp;
    FileOutputStream fo;

    // Camera variables
// a surface holder
    private SurfaceHolder sHolder;
    // a variable to control the camera
    private Camera mCamera;
    // the camera parameters
    private Camera.Parameters parameters;
    //private String FLASH_MODE;
    private boolean isFrontCamRequest = false;

    String TAG = "ssssssssss";
    int IMAGE_SIZE = 800;
    Button btnCapture;
    /**
     * Called when the activity is first created.
     */

    void onCreateData() {

        Button btnCapture = (Button) findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCamera.takePicture(null, null, mCall);
                mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });
        // check if this device has a camera
        if (checkCameraHardware(this)) {
            // get the Image View at the main.xml file
            // iv_image = (ImageView) findViewById(R.id.imageView);

            // get the Surface View at the main.xml file
           // Bundle extras = getIntent().getExtras();
            //String flash_mode = extras.getString("FLASH");
           // FLASH_MODE = flash_mode;
           /* boolean front_cam_req = extras.getBoolean("Front_Request");
            isFrontCamRequest = front_cam_req;*/

            sv = (SurfaceView) findViewById(R.id.camera_preview);

            // Get a surface
            sHolder = sv.getHolder();

            // add the callback interface methods defined below as the Surface
            // View
            // callbacks
            sHolder.addCallback(this);

            // tells Android that this surface will have its data constantly
            // replaced
            sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        } else {
            // display in long period of time
            Toast.makeText(getApplicationContext(),
                    "Your Device dosen't have a Camera !", Toast.LENGTH_LONG)
                    .show();
        }

    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        // this device has a camera
// no camera on this device
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int arg1, int arg2, int arg3) {
        try {
            // get camera parameters
            parameters = mCamera.getParameters();

       /* if (FLASH_MODE == null || FLASH_MODE.isEmpty()) {
            FLASH_MODE = "auto";
        }*/
            parameters.setFlashMode("auto");


            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } else {
                // This is an undocumented although widely known feature
                parameters.set("orientation", "landscape");
                // For Android 2.2 and above
                mCamera.setDisplayOrientation(0);
                // Uncomment for Android 2.0 and above
                parameters.setRotation(0);
            }


            // set camera parameters
            mCamera.setParameters(parameters);
            //mCamera.startPreview();
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw the preview.

        mCamera = getCameraInstance();
        try {

          //  mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            parameters = mCamera.getParameters();

            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            }
            else {
                // This is an undocumented although widely known feature
                parameters.set("orientation", "landscape");
                // For Android 2.2 and above
                mCamera.setDisplayOrientation(0);
                // Uncomment for Android 2.0 and above
                parameters.setRotation(0);
            }

/*

// Find a preview size that is at least the size of our IMAGE_SIZE
            Camera.Size previewSize = camParams.getSupportedPreviewSizes().get(0);
            for (Camera.Size size : camParams.getSupportedPreviewSizes()) {
                if (size.width >= IMAGE_SIZE && size.height >= IMAGE_SIZE) {
                    previewSize = size;
                    break;
                }
            }
            camParams.setPreviewSize(previewSize.width, previewSize.height);

// Try to find the closest picture size to match the preview size.
            Camera.Size pictureSize = camParams.getSupportedPictureSizes().get(0);
            for (Camera.Size size : camParams.getSupportedPictureSizes()) {
                if (size.width == previewSize.width && size.height == previewSize.height) {
                    pictureSize = size;
                    break;
                }
            }
            camParams.setPictureSize(pictureSize.width, pictureSize.height);
*/


            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
    }














    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    @Override
    protected int baseViewData() {
        return R.layout.square_camera;
    }

    @Override
    protected void baseSetData() {
        onCreateData();

    }

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

    @Override
    protected Void doInBackground(byte[]... data) {
        FileOutputStream outStream = null;

        // Write to SD Card
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/camtest");
            dir.mkdirs();

            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);

            outStream = new FileOutputStream(outFile);
            outStream.write(data[0]);
            outStream.flush();
            outStream.close();

            Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

            refreshGallery(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }
}



    private void resetCam() {
        mCamera.startPreview();
    }
}



/*
{

    private CameraPreview cameraPreview;
    private RelativeLayout overlay;
    Camera camera;

    String TAG = "===========sad=";

    TextView tvPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Optional: Hide the status bar at the top of the window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the content view and get references to our views
        setContentView(R.layout.square_camera);
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        overlay = (RelativeLayout) findViewById(R.id.overlay);
        tvPreview = (TextView) findViewById(R.id.preview);


        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Camera.Parameters camParams = camera.getParameters();

// Find a preview size that is at least the size of our IMAGE_SIZE
        Camera.Size previewSize = camParams.getSupportedPreviewSizes().get(0);
        for (Camera.Size size : camParams.getSupportedPreviewSizes()) {
            if (size.width >= IMAGE_SIZE && size.height >= IMAGE_SIZE) {
                previewSize = size;
                break;
            }
        }
        camParams.setPreviewSize(previewSize.width, previewSize.height);

// Try to find the closest picture size to match the preview size.
        Camera.Size pictureSize = camParams.getSupportedPictureSizes().get(0);
        for (Camera.Size size : camParams.getSupportedPictureSizes()) {
            if (size.width == previewSize.width && size.height == previewSize.height) {
                pictureSize = size;
                break;
            }
        }
        camParams.setPictureSize(pictureSize.width, pictureSize.height);


        cameraPreview.setKeepScreenOn(true);
        tvPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Get the preview size
        int previewWidth = cameraPreview.getMeasuredWidth(),
                previewHeight = cameraPreview.getMeasuredHeight();

        // Set the height of the overlay so that it makes the preview a square
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = previewHeight - previewWidth;
        overlay.setLayoutParams(overlayParams);
    }


    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open(0);
                camera.startPreview();
                //    cameraPreview.setCamera(camera);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.stopPreview();
            //  cameraPreview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }




    int IMAGE_ORIENTATION = 90;
    int IMAGE_SIZE = 800;

    private Bitmap processImage(byte[] data) throws IOException {
        // Determine the width/height of the image
        int width = camera.getParameters().getPictureSize().width;
        int height = camera.getParameters().getPictureSize().height;

        // Load the bitmap from the byte array
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        Matrix matrix = new Matrix();
        matrix.postRotate(IMAGE_ORIENTATION);
        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight, matrix, true);
        bitmap.recycle();

        // Scale down to the output size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, IMAGE_SIZE, IMAGE_SIZE, true);
        cropped.recycle();

        return scaledBitmap;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


}*/

