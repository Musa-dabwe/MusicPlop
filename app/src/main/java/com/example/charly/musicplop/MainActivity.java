package com.example.charly.musicplop;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // OverDrawing permission:
    private static final int CODE_DRAW_OTHER_APP_PERMISSION = 2084;

    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.notify_me);

        // For API23 and > :
        // (We will check if we have a newer API in order to change the default security parameters)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,CODE_DRAW_OTHER_APP_PERMISSION);
        }
        // For < API23:
        else {
            initializeView();
        }
    }

    public void initializeView(){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FloatingViewService.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODE_DRAW_OTHER_APP_PERMISSION){
            // Checking for permission:
            if(resultCode == RESULT_OK){
                initializeView();
            }
            else { // We don't have the permission:
                Toast.makeText(MainActivity.this,"The Application doesn't have the permission to execute. Closing...",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
