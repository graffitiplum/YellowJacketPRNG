package org.hacktivity.yellowjacketprng;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.hacktivity.Base64;
import org.hacktivity.BlumBlumShub;
import org.hacktivity.Web;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.sql.Types.NULL;

public class WaspActivity extends AppCompatActivity implements SensorEventListener {

    // TODO: multiple entropy pools.

    private static int ENTROPY_POOL_SIZE = 8192;

    private int[] pool = new int[ENTROPY_POOL_SIZE];
    private int pool_ctr = 0;
    private int pool_iter = 0;

    private SensorManager sensorManager;

    // timestamps for sensors
    private Sensor sensorAccelerometer;
    private Sensor sensorAmbientTemperature;
    private Sensor sensorGravity;
    private Sensor sensorGyroscope;
    private Sensor sensorLight;
    private Sensor sensorLinearAcceleration;
    private Sensor sensorMagneticField;
    private Sensor sensorPressure;
    private Sensor sensorProximity;
    private Sensor sensorRelativeHumidity;
    private Sensor sensorRotationVector;

    private TextView poolTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wasp);

        //Intent intent = getIntent();

        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //TextView textView = new TextView(this);
        //textView.setTextSize(40);
        //textView.setText(message);

        //ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        //layout.addView(textView);
        poolTextView = (TextView) findViewById(R.id.poolTextView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        pool = new int[ENTROPY_POOL_SIZE];

        // Initialize entropy pool
        {
            int i;
            SecureRandom rng = new SecureRandom();

            for (i = 0; i < pool.length; i++) {
                pool[i] = rng.nextInt();
            }
        }

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorAmbientTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorLinearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorRelativeHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // List all sensors available
        //List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // run in the background.
        final Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                updateText();
                handler.postDelayed(this, 666);
            }
        };
        handler.removeCallbacks(task);
        handler.post(task);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            getAmbientTemperature(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            getGravity(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            getGyroscope(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            getLight(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            getLinearAcceleration(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            getMagneticField(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            getPressure(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            getProximity(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            getRelativeHumidity(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            getRotationVector(event);
        }

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorAmbientTemperature, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorMagneticField, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorPressure, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorProximity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorRelativeHumidity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorRotationVector, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void getAccelerometer(SensorEvent event) {

        /*
        float[] values = event.values;

        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdateAccelerometer < 200) {
                return;
            }
            lastUpdateAccelerometer = actualTime;

        }
        */

        addEntropy(event.values);
    }

    private void getAmbientTemperature(SensorEvent event) {
        addEntropy(event.values);
    }

    private void getGravity(SensorEvent event) {
        addEntropy(event.values);
    }

    private void getGyroscope(SensorEvent event) {
        addEntropy(event.values);
    }

    private void getLight(SensorEvent event) {
        addEntropy(event.values);
    }

    private void getLinearAcceleration(SensorEvent event) {
        addEntropy(event.values);
    }

    private void getMagneticField(SensorEvent event) {
        addEntropy(event.values);
    }

    private void getPressure(SensorEvent event) {
        addEntropy(event.values);
    }

    private void getProximity(SensorEvent event) {
        addEntropy(event.values);
    }

    private void getRelativeHumidity(SensorEvent event) {
        addEntropy(event.values);
    }

    private void getRotationVector(SensorEvent event) {
        addEntropy(event.values);
    }

    private void addEntropy (float[] entropy) {

        int i;
        for (i=0;i<entropy.length;i++) {
            this.pool[this.pool_ctr] = Float.floatToIntBits(this.pool[this.pool_ctr]) ^ Float.floatToIntBits(entropy[i]);

            this.pool_ctr++;
            if (this.pool_ctr == this.pool.length) {
                this.pool_ctr = 0;
            }
        }
    }

    public String getPool() {

        // TODO: Something better.
        String ret = "";
        int i;
        for (i = 0; i < this.pool.length; i++) {
            ret += (char) Integer.reverseBytes(this.pool[i]); // Get low bits
        }

        return (ret);
    }

    public void updateText () {
        byte data[] = getPool().getBytes();

        if (pool_iter == 30) {

            // Publish randomness to hacktivity.org

            try {
                // catches IOException below
                final String TESTSTRING = new String(data);

                FileOutputStream fOut = openFileOutput("honeycomb.data", NULL);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);

                // Write the string to the file
                osw.write(TESTSTRING);

                osw.flush();
                osw.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            pool_iter = 0;

            {
                // Send data to hacktivity.org;
                final String postData = "pool=" + Base64.encodeBytes(data);
                class SimpleThread extends Thread {
                    public SimpleThread(String str) {
                        super(str);
                    }

                    public void run() {
                        {
                            try { Web.sendPost(
                                    "https://hacktivity.org/yellowjacket/pool.php",
                                    postData); } catch (Exception e) {}
                        }
                    }
                }
                new SimpleThread("st").start();

            }


        }
        else {
            pool_iter++;
        }

        poolTextView.setText(new String(data));

    }
}
