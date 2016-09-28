package org.hacktivity.yellowjacketprng;

import android.app.Service;
import android.content.Intent;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;

import org.hacktivity.BlumBlumShub;

import static android.content.Context.SENSOR_SERVICE;
import static java.sql.Types.NULL;

public class GatherPollen extends AppCompatActivity implements SensorEventListener {

    // TODO: multiple entropy pools.

    // TODO: create and maintain an entropy pool
    private int ENTROPY_POOL_SIZE = 8192;
    private int[] pool;
    private int pool_ctr = 0;

    private SensorManager sensorManager;
    private BlumBlumShub bbs = new BlumBlumShub(2310);

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

    //private final IBinder binder = new LocalBinder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.pool = new int[ENTROPY_POOL_SIZE];

        // Initialize entropy pool
        {
            int i;
            SecureRandom rng = new SecureRandom();

            for (i = 0; i < this.pool.length; i++) {
                this.pool[i] = rng.nextInt();
            }
        }

        // List all sensors available
        //List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        this.sensorAccelerometer =      this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorAmbientTemperature = this.sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        this.sensorGravity =            this.sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        this.sensorGyroscope =          this.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.sensorLight =              this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.sensorLinearAcceleration = this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.sensorMagneticField =      this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.sensorPressure =           this.sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        this.sensorProximity =          this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.sensorRelativeHumidity =   this.sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        this.sensorRotationVector =     this.sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        Toast.makeText(this, "Entropy pool initialized.", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        // TODO: Add other handlers.
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            this.getAccelerometer(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            this.getAmbientTemperature(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            this.getGravity(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            this.getGyroscope(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            this.getLight(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            this.getLinearAcceleration(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            this.getMagneticField(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            this.getPressure(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            this.getProximity(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            this.getRelativeHumidity(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            this.getRotationVector(event);
        }

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onResume() {

        this.sensorManager.registerListener(this, this.sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorAmbientTemperature, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorGravity, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorLight, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorMagneticField, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorPressure, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorProximity, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorRelativeHumidity, SensorManager.SENSOR_DELAY_FASTEST);
        this.sensorManager.registerListener(this, this.sensorRotationVector, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        this.sensorManager.unregisterListener(this);
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
        this.addEntropy(event.values);
    }

    private void getAmbientTemperature(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void getGravity(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void getGyroscope(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void getLight(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void getLinearAcceleration(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void getMagneticField(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void getPressure(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void getProximity(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void getRelativeHumidity(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void getRotationVector(SensorEvent event) {
        // TODO: add entropy to pool.
        this.addEntropy(event.values);
    }

    private void addEntropy (float[] entropy) {

        int i;
        for (i=0;i<entropy.length;i++) {
            this.pool[this.pool_ctr] = Float.floatToIntBits(this.pool[this.pool_ctr]) ^ Float.floatToIntBits(entropy[i]);
            //Toast.makeText(this, "Added Entropy", Toast.LENGTH_SHORT).show();

            this.pool_ctr++;
            if (this.pool_ctr == this.pool.length) {
                this.pool_ctr = 0;

                byte data[] = getRandom().getBytes();
                try {
                    // catches IOException below
                    final String TESTSTRING = data.toString();

                    FileOutputStream fOut = openFileOutput("honeycomb.data", NULL);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    // Write the string to the file
                    osw.write(TESTSTRING);

                    osw.flush();
                    osw.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }


            }
        }
    }

    public String getRandom() {

        // TODO: Something better.
        String ret = "";
        int i;
        for (i = 0; i < this.pool.length; i++) {
            ret += (char) Integer.reverseBytes(this.pool[i]); // Get low bits
        }

        return (ret);
    }
}
