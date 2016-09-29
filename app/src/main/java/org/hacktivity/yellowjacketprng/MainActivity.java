package org.hacktivity.yellowjacketprng;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static java.sql.Types.NULL;
import static org.hacktivity.yellowjacketprng.R.id.poolTextView;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "org.hacktivity.MESSAGE";
    public final static int ENTROPY_POOL_SIZE = 8192;

    TextView poolTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        poolTextView = (TextView) findViewById(R.id.poolTextView);
/*
        Intent intent = new Intent(this, WaspActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //mServiceIntent.setData(Uri.parse(dataUrl));
        startActivity(intent);
        */
    }

    // Start the  service
    public void startService(View view) {
        startService(new Intent(this, WaspService.class));
    }
            // Stop the  service
    public void stopService(View view) {
        stopService(new Intent(this, WaspService.class));
    }

}
