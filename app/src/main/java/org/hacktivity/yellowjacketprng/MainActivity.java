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

        Intent intent = new Intent(this, WaspActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void updateText () {

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

    }
}
