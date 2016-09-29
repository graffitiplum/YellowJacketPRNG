package org.hacktivity.yellowjacketprng;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    //public final static String EXTRA_MESSAGE = "org.hacktivity.MESSAGE";

    boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Timer to start and stop the service every half hour or so?

        startService(new Intent(this, WaspService.class));

        this.finish();

    }

    // Start the  service

    /*
    public void startService(View view) {
        startService(new Intent(this, WaspService.class));
    }
            // Stop the  service
    public void stopService(View view) {
        stopService(new Intent(this, WaspService.class));
    }
    */

}
