package com.example.schneller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class PostNewData extends AsyncTask<String, Void, String> {
    private static final String TAG = PostNewData.class.getSimpleName();
    private String server;

    public PostNewData(final String server) {
        this.server = server;
    }

    @Override
    protected String doInBackground(String... params) {
        // send
        try {
            URL urlObject = new URL(server);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlObject.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setChunkedStreamingMode(0);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");

            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);

            httpURLConnection.connect();

            JSONObject payload = new JSONObject();

            String[] paramOrder = new String[]{"ean", "test_id", "name", "manufacturer", "img"};

            // enter string parameters
            for (int i = 0; i < params.length - 1; i++) {
                try {
                    payload.put(paramOrder[i], params[i]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // load image, encode it, add to json
            /**
            Bitmap bitmap = BitmapFactory.decodeFile(params[params.length - 1]);
            ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getAllocationByteCount());
            bitmap.copyPixelsToBuffer(byteBuffer);
            String encoded = Base64.getEncoder().encodeToString(byteBuffer.array()); */
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(params[params.length - 1]);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
            String encoded = Base64.getEncoder().encodeToString(byteArrayOS.toByteArray());
            try {
                payload.put(paramOrder[params.length -1], encoded);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(payload.toString());
            dataOutputStream.flush();
            dataOutputStream.close();

            // receive
            InputStream inputStream;
            try {
                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            } catch (FileNotFoundException e) {
                inputStream = new BufferedInputStream(httpURLConnection.getErrorStream());
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            // ArrayList<String> result = new ArrayList<String>();
            StringBuilder result = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                result.append("\n").append(line);
            }
            System.out.println(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
