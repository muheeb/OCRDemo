package com.google.android.gms.samples.vision.ocrreader.ui.camera;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.OcrCaptureActivity;
import com.google.android.gms.samples.vision.ocrreader.R;

public class TextFields extends AppCompatActivity implements View.OnClickListener{

    TextView tv1, tv2, tv3, tv4;
    public static final int r1 = 1, r2 = 2, r3 = 3, r4 = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_fields);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);

        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(getApplicationContext(), OcrCaptureActivity.class);
        switch (view.getId()) {
            case R.id.tv1:
                startActivityForResult(i, r1);
                break;
            case R.id.tv2:
                startActivityForResult(i, r2);
                break;
            case R.id.tv3:
                startActivityForResult(i, r3);
                break;
            case R.id.tv4:
                startActivityForResult(i, r4);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case r1:
                    tv1.setText(data.getExtras().getString("data"));
                    break;
                case r2:
                    tv2.setText(data.getExtras().getString("data"));
                    break;
                case r3:
                    tv3.setText(data.getExtras().getString("data"));
                    break;
                case r4:
                    tv4.setText(data.getExtras().getString("data"));
                    break;
            }
        }
    }
}
