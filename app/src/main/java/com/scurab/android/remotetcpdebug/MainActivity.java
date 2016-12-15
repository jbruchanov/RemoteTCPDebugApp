package com.scurab.android.remotetcpdebug;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends Activity {

    private TextView mIps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIps = (TextView) findViewById(R.id.ip);
        findViewById(R.id.btn_enable_tcp_debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEnableTCPDebug();
            }
        });
        mIps.setText("IP Address: " + Arrays.toString(ShellHelper.getIpAddresses().toArray()));
    }

    protected void onEnableTCPDebug() {
        String[] cmds = new String[]{
                "setprop service.adb.tcp.port 5555",
                "stop adbd",
                "start adbd",
        };
        try {
            ShellHelper.execute("su", cmds);
            showToast("OK");
        } catch (Throwable e) {
            showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg != null ? msg : "NULL_MSG", Toast.LENGTH_LONG).show();
    }
}
