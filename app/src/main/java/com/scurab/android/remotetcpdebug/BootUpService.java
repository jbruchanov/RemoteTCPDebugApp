package com.scurab.android.remotetcpdebug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by JBruchanov on 15/12/2016.
 */

public class BootUpService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        Log.d("BootUpService", String.format("Action:'%s'", String.valueOf(action)));
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.d("BootUpService", "Starting su for TCP Debug");
            try {
                ShellHelper.execute("su", ShellHelper.ENABLE_TCP_DEBUG_CMDS);
                Log.d("BootUpService", "Finished su for TCP Debug");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
