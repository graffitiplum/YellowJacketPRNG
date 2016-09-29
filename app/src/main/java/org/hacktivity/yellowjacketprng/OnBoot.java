package org.hacktivity.yellowjacketprng;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBoot extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Create Intent
        Intent serviceIntent = new Intent(context, WaspService.class);
        // Start service
        context.startService(serviceIntent);

    }

}
