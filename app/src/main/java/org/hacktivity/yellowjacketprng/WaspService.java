package org.hacktivity.yellowjacketprng;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import org.hacktivity.Base64;
import org.hacktivity.Web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class WaspService extends Service implements SensorEventListener {

    private static int ENTROPY_POOL_SIZE = 4096;

    private int[] pool = new int[ENTROPY_POOL_SIZE];
    private int pool_ctr = 0;
    private int pool_iter = 0;

    private SensorManager sensorManager;

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

    public WaspService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

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

                // POST data to hacktivity.org
                {
                    // Send data to hacktivity.org;
                    String data = "";
                    /*
                    try {
                        //data = "pool=" + URLEncoder.encode(getPool(), "UTF-8");

                    } catch (UnsupportedEncodingException uee) {}
                    */
                    MessageDigest md;
                    String hash = "";
                    try {
                        md = MessageDigest.getInstance("SHA-256");
                        md.update(getPool().getBytes());
                        hash = new String(md.digest());
                    } catch (NoSuchAlgorithmException nsae) {}
                    if (! hash.equals("")) {
                        data = "pool=" + hash;
                        final String postData = data;
                        class SimpleThread extends Thread {
                            public SimpleThread(String str) {
                                super(str);
                            }

                            public void run() {
                                {
                                    try {
                                        Web.sendPost(
                                                "https://hacktivity.org/yellowjacket/pool.php",
                                                postData);
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                        new SimpleThread("st").start();
                    }

                }
                handler.postDelayed(this, 2310);
            }
        };
        handler.removeCallbacks(task);
        handler.post(task);

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

        Toast.makeText(this, "Yellow Jacket Started", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        Toast.makeText(this, "Yellow Jacket Stopped", Toast.LENGTH_LONG).show();

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
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

    private void getAccelerometer(SensorEvent event) { addEntropy(event.values); }

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

    private String getPool() {

        String ret = "";
        int i;
        for (i = 0; i < this.pool.length; i++) {
            ret += (char) (Integer.reverseBytes(this.pool[i]) % 256); // Get low bits
        }

        return (ret);
    }

}
