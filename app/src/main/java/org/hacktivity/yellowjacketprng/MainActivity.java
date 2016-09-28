package org.hacktivity.yellowjacketprng;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;

import org.hacktivity.Base64;
import org.hacktivity.BlumBlumShub;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.sql.Types.NULL;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // TODO: multiple entropy pools.

    private static int ENTROPY_POOL_SIZE = 8192;
    private static int BBS_KEYLEN = 2310;

    private int[] pool = new int[ENTROPY_POOL_SIZE];
    private int pool_ctr = 0;
    private int pool_iter = 0;

    private SensorManager sensorManager;
    private BlumBlumShub bbs = new BlumBlumShub(BBS_KEYLEN);

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
        setContentView(R.layout.activity_main);

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

        bbs = new BlumBlumShub(BBS_KEYLEN);

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

        // TODO: Add other handlers.
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

        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getAmbientTemperature(SensorEvent event) {
        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getGravity(SensorEvent event) {
        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getGyroscope(SensorEvent event) {
        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getLight(SensorEvent event) {
        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getLinearAcceleration(SensorEvent event) {
        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getMagneticField(SensorEvent event) {
        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getPressure(SensorEvent event) {
        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getProximity(SensorEvent event) {
        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getRelativeHumidity(SensorEvent event) {
        // TODO: add entropy to pool.
        addEntropy(event.values);
    }

    private void getRotationVector(SensorEvent event) {
        // TODO: add entropy to pool.
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

        if (pool_iter == 256) {

            // Publish randomness to hacktivity.org

            // mix all data with BBS.
            byte bbs_data[] = bbs.randBytes(pool.length);
            int i;
            for (i=0;i<pool.length;i++) {
                int j=data[i],k=bbs_data[i];
                data[i] = (byte) (j ^ k);
            }
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

            //poolTextView.setText(data.toString());

            pool_iter = 0;
        }
        else {
            pool_iter++;
        }

        poolTextView.setText(new String(data));

        /*
        try {
            FileInputStream fIn = openFileInput("honeycomb.data");
            InputStreamReader isr = new InputStreamReader(fIn);

            char[] inputBuffer = new char[ENTROPY_POOL_SIZE];

            // Fill the Buffer with data from the file
            isr.read(inputBuffer);

            // Transform the chars to a String
            String readString = new String(inputBuffer);

            poolTextView.setText(readString);

        } catch (IOException ioe)
        {ioe.printStackTrace();}
        */
    }
}
