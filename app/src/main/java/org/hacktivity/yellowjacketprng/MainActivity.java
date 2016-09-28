package org.hacktivity.yellowjacketprng;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class MainActivity extends AppCompatActivity {

    private TextView poolTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        poolTextView = (TextView) findViewById(R.id.poolTextView);


        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent gatherIntent = new Intent(MainActivity.this, GatherPollen.class);
                startActivity(gatherIntent);
            }
        });

        /*
        // run in the background.
        final Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                poolTextView.setText(gp.getRandom());
                handler.postDelayed(this, 666);
            }
        };
        handler.removeCallbacks(task);
        handler.post(task);
        */

        // run in the background.
        final Handler buzzHandler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                updateText();
                buzzHandler.postDelayed(this, 666);
            }
        };
        buzzHandler.removeCallbacks(task);
        buzzHandler.post(task);

    }

    public void startActivity(Intent aboutScreen) {

        // do things.

    }

    public void updateText () {

        /*
        // TODO: write entropy pool to disk.
        try {
            // catches IOException below
            final String TESTSTRING = "Hello, Android";

            FileOutputStream fOut = openFileOutput("honeycomb.data", NULL);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(TESTSTRING);

            osw.flush();
            osw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        */


        try {
            FileInputStream fIn = openFileInput("honeycomb.data");
            InputStreamReader isr = new InputStreamReader(fIn);

        /* Prepare a char-Array that will
         * hold the chars we read back in. */
            char[] inputBuffer = new char[8192];

            // Fill the Buffer with data from the file
            isr.read(inputBuffer);

            // Transform the chars to a String
            String readString = new String(inputBuffer);

            poolTextView.setText(readString);

        } catch (IOException ioe)
        {ioe.printStackTrace();}
    }

}
