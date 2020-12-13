package com.example.dailyhealthcheckup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationManager;

import com.example.dailyhealthcheckup.dao.SignAndSymptomsDAO;
import com.example.dailyhealthcheckup.db.CRUDMonitor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity{

    // pre-requisites
    MediaRecorder mediaRecorder;
    Camera camera;
    CameraPreview cameraPreview;
    FrameLayout layout;
    File heartRateOutputFile;

    // parameters
    boolean isCameraRecording = false;
    ArrayList<float[]> accelerometerReadings = new ArrayList<>();
    public static final int RECORDING_TIME =  45000;
    float respiratoryRate = 0f;
    float heartRate = 0f;

    final String username = "vchaudh7";
    final String today = String.valueOf(LocalDate.now());

    private LocationManager locationManager;
    private Context context = this;

    final String UPLOAD_URL = "http://2ba2dd4d3ddf.ngrok.io/db";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * USER PERMISSIONS FOR APP
         */
        handlePermissions();

        final TextView heartRateView = findViewById(R.id.heart_rate);
        final TextView respiratoryRateView = findViewById(R.id.respiratory_rate);
        final TextView latitude = findViewById(R.id.latitude);
        final TextView longitude = findViewById(R.id.longtitude);
        Button uploadSigns = findViewById(R.id.upload_signs);
        Button symptoms = findViewById(R.id.symptoms);
        Button measureHeartRate = findViewById(R.id.measure_heart_rate);
        Button measureRespiratoryRate = findViewById(R.id.measure_respiratory_rate);
        //CRUDMonitor crudMonitor = new CRUDMonitor(MainActivity.this);
        Button getLocation = findViewById(R.id.get_location);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Button gotoContactGraph = findViewById(R.id.goto_contact_graph);
        Button uploadDB = findViewById(R.id.upload);

//        final Float[] signs = crudMonitor.getSignsData(username, today);
//        if(signs != null) {
//
//            heartRate = signs[0];
//            respiratoryRate = signs[1];
//
//            heartRateView.setText(String.format("%.1f", heartRate));
//            respiratoryRateView.setText(String.format("%.1f", respiratoryRate));
//        }

        /**
         * SYMPTOMS BUTTON ACTIVITY
         */
        symptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SymptomsActivity.class);
                startActivity(intent);
            }
        });

        /**
         * CONTACT TRACING ACTIVITY
         */
        gotoContactGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                startActivity(intent);
            }
        });

        /**
         * SAVE SIGNS TO DATABASE
         */
        uploadSigns.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                SignAndSymptomsDAO signAndSymptomsDAO = new SignAndSymptomsDAO();
                signAndSymptomsDAO.setUsername(username);
                signAndSymptomsDAO.setDate(today);

                signAndSymptomsDAO.setHeartRate(heartRate);
                signAndSymptomsDAO.setRespiratoryRate(respiratoryRate);

                CRUDMonitor crudMonitor = new CRUDMonitor(MainActivity.this);
                crudMonitor.insert(signAndSymptomsDAO, "signs");

                Toast.makeText(getApplicationContext(),
                        "Signs data saved!",
                        Toast.LENGTH_LONG)
                        .show();


            }
        });

        /**
         * CAMERA RECORDING
         */
        measureHeartRate.setOnClickListener(new Button.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                if (prepareVideoRecorder()) {

                    mediaRecorder.start();
                    heartRateView.setText(" Please wait. Collecting heart rate data for "
                            + (RECORDING_TIME/1000) + "s...");
                    isCameraRecording = true;
                }
                else {

                    Toast.makeText(getApplicationContext(),
                            "Check if you have camera and flash enabled!",
                            Toast.LENGTH_LONG)
                            .show();
                    releaseMediaRecorder();
                }
                new Handler().postDelayed(new Runnable() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {

                        if (isCameraRecording) {

                            mediaRecorder.stop();
                            releaseMediaRecorder();
                            camera.lock();
                            isCameraRecording = false;

                            Toast.makeText(getApplicationContext(),
                                    "Heart rate data collected!",
                                    Toast.LENGTH_LONG)
                                    .show();

                            heartRateView.setText(" Please wait. Calculating heart rate ...");

                            final Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                public void run() {

                                    HeartRateMonitor heartRateMonitor = new HeartRateMonitor();
                                    heartRateMonitor.setHeartRateOutputFilePath(heartRateOutputFile.toString());
                                    heartRate = (float)heartRateMonitor.calculateHeartRate();

                                    handler.post(new Runnable(){
                                        public void run() {
                                            heartRateView.setText(String.format("%.1f", heartRate));

                                            Toast.makeText(getApplicationContext(),
                                                    "Heart rate calculated!",
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    });

                                }
                            };
                            new Thread(runnable).start();
                        }
                    }
                }, RECORDING_TIME);
            }
        });

        /**
         * ACCELEROMETER
         */
        measureRespiratoryRate.setOnClickListener(new Button.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                Sensor accelerometer;
                final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {

                    final SensorEventListener sensorListener = new SensorEventListener() {

                        public void onSensorChanged(SensorEvent event) {

                            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                                accelerometerReadings.add(new float[]{event.values[0],
                                        event.values[1],
                                        event.values[2]});
                            }
                        }
                        @Override
                        public void onAccuracyChanged(Sensor sensor, int i) {

                        }
                    };

                    respiratoryRateView.setText(" Please wait. Collecting accelerometer data for "
                            + (RECORDING_TIME/1000) + "s...");

                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    sensorManager.registerListener(sensorListener,
                            accelerometer,
                            SensorManager.SENSOR_DELAY_NORMAL);

                    new Handler().postDelayed(new Runnable() {

                        @SuppressLint("DefaultLocale")
                        @Override
                        public void run() {

                            sensorManager.unregisterListener(sensorListener);
                            Toast.makeText(getApplicationContext(),
                                    "Accelerometer data collected!",
                                    Toast.LENGTH_LONG)
                                    .show();
                            System.out.println("Accelerometer readings size: "
                                    + accelerometerReadings.size());

                            respiratoryRate = new RespiratoryRateMonitor()
                                    .calculateRespiratoryRate(accelerometerReadings);
                            respiratoryRateView.setText(String.format("%.1f", respiratoryRate));
                        }
                    }, RECORDING_TIME);
                }
                else {

                    Toast.makeText(getApplicationContext(),
                            "No Sensor!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        /**
         * GPS Location
         */
        getLocation.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {

                ArrayList<Double> latLong;
                LocationMonitor locationMonitor = new LocationMonitor(locationManager, context);
                latLong = locationMonitor.getLocationData();
                System.out.println("GPS location = " + latLong);

                // Latitude & Longitude
                if (latLong.size() == 2) {

                    SignAndSymptomsDAO gpsData = new SignAndSymptomsDAO();
                    gpsData.setUsername("vchaudh7");
                    gpsData.setDate(today);

                    gpsData.setLatitude(latLong.get(0));
                    gpsData.setLongitude(latLong.get(1));

                    CRUDMonitor myDB = new CRUDMonitor(MainActivity.this);
                    myDB.insert(gpsData, "location");

                    Toast.makeText(getApplicationContext(),
                            "Location saved!",
                            Toast.LENGTH_LONG)
                            .show();

                    latitude.setText(String.format("%.5f", gpsData.getLatitude()));
                    longitude.setText(String.format("%.5f", gpsData.getLongitude()));
                }
                else { System.out.println("Location failed to update"); }
            }
        });

        /**
         * UPLOAD DB
         */
        uploadDB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                System.out.println("Uploading DB to - " + UPLOAD_URL);
                System.out.println("DB location - "
                        + new CRUDMonitor(getApplicationContext()).getPath());
                new Uploader(new CRUDMonitor(getApplicationContext())
                        .getPath(),
                        MainActivity.this)
                        .execute(UPLOAD_URL);
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        camera = Camera.open();
        cameraPreview = new CameraPreview(this, camera);
        layout = findViewById(R.id.videoFrame);
        layout.addView(cameraPreview);
    }

    @Override
    protected void onPause() {

        super.onPause();
        releaseMediaRecorder();
    }

    @Override
    protected  void onDestroy(){

        super.onDestroy();
        releaseCamera();
    }

    /**
     * Return file for saving data(camera recording, image, etc.)
     * @param type
     * @return
     */
    private File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DailyHealthCheckup");

        if (! mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("DailyHealthCheckup", "failed to create directory!");
                return null;
            }
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Camera utilities
     */
    private void releaseMediaRecorder(){

        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.lock();
        }
    }

    private void releaseCamera(){

        if (camera != null){
            camera.release();
            camera = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean prepareVideoRecorder() {

        Camera.Parameters p = camera.getParameters();
        if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
            return false;
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(p);
        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

        heartRateOutputFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mediaRecorder.setOutputFile(Objects.requireNonNull(heartRateOutputFile).toString());

        mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
        }
        catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        }
        catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    /**
     * App permissions
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean handlePermissions() {

        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        boolean isGranted = false;

        while(!isGranted) {

            int totalPermissions = 0;

            for (int i = 0; i < permissions.length; ++i) {

                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 1);
                }
                else {
                    totalPermissions++;
                }
            }

            if(totalPermissions == 4) { isGranted = true; }
        }

        return true;
    }
}