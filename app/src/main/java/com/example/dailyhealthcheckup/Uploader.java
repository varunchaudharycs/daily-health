package com.example.dailyhealthcheckup;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.RequiresApi;

import static android.content.ContentValues.TAG;

public class Uploader extends AsyncTask<String, Void, String> {

    private String src = null;
    ProgressDialog dialog = null;
    private String url = "";
    @SuppressLint("StaticFieldLeak")
    private Context context;

    public Uploader(String filePath, Context context) {
        this.src = filePath;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        this.url = params[0];
        if (uploadFile(this.src) == 200) {
            return "SUCCESS";
        }
        return "FAIL";
    }

    @Override
    protected void onPostExecute(String result) {
        dialog.dismiss();
        if (result.compareTo("SUCCESS") == 0) {
            Toast.makeText(this.context, "Upload Successful!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.context, "Upload Fail!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "", "Uploading database...", false);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    public int uploadFile(String sourceFilePath) {

        int serverResponseCode = 0;
        HttpURLConnection connection = null;
        DataOutputStream dataOutputStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(sourceFilePath);

        if (!sourceFile.isFile()) {
            Log.e("File", "does not exist");
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(this.url);

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("uploadedfile", sourceFilePath);

                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=" + sourceFilePath + "" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                if (serverResponseCode == 200) {
                    Log.i(TAG, serverResponseMessage);
                }

                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Upload file to server Exception : " + e.getMessage(), e);
            }
            return serverResponseCode;
        }
    }

}