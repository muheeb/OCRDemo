package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.samples.vision.ocrreader.OcrFromImage.MainActivity;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        findViewById(R.id.btnImageScan).setOnClickListener(this);
        findViewById(R.id.btnRealScan).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnImageScan:
                launch(MainActivity.class);
                break;
            case R.id.btnRealScan:
                launch(OcrCaptureActivity.class);
        }
    }

    private void launch(Class targetClass) {
        startActivity(new Intent(getApplicationContext(), targetClass));
    }


}
