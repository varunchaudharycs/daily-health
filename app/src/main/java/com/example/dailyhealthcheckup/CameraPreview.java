package com.example.dailyhealthcheckup;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/** A basic Camera preview class */

/**
 * Ref: Android studio documentation. Template class.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {

        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {

        try {

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch (IOException e) {

            System.out.println("ERROR: Setting camera preview! Exception - " + e.getMessage());
        }
    }

    /**
     * Placeholder
     * @param holder
     */
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mHolder.getSurface() == null){

            return;
        }

        try {

            mCamera.stopPreview();
        }
        catch (Exception e){

            System.out.println("ERROR: Tried stopping non-existent preview! Exception - " + e.getMessage());
        }

        try {

            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch (Exception e){

            System.out.println("ERROR: Setting camera preview! Exception - " + e.getMessage());
        }
    }
}