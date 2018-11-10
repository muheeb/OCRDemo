package com.google.android.gms.samples.vision.ocrreader.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.samples.vision.ocrreader.OcrCaptureActivity;
import com.google.android.gms.samples.vision.ocrreader.OcrFromImage.OnTextCaptureListener;
import com.google.android.gms.samples.vision.ocrreader.R;

import java.util.ArrayList;

public class ConfirmaDataDialog extends DialogFragment implements View.OnClickListener
{

    public static int COUNTER = 0;
    EditText et;
    OnTextCaptureListener captureActivity;

    public static ConfirmaDataDialog newInstance() {
        return new ConfirmaDataDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_data_dialog, container, false);

        ArrayList<String> data = new ArrayList<>(getArguments().getStringArrayList("data"));
        Log.i( "onCreateView: " ,"Size: "+data.size());

        LinearLayout linearLayout = v.findViewById(R.id.linearLayout);
        Button btnRetry = v.findViewById(R.id.btn_retry);
        Button btnOk = v.findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(this);

        captureActivity = new OcrCaptureActivity();

        ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        for (String str : data) {
            et =new EditText(getContext());
            et.setLayoutParams(lparams);
            et.setTextColor(getResources().getColor(android.R.color.black));
            et.setText(str);
            linearLayout.addView(et);
        }

        return v;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                captureActivity.onTextCapture(et.getText().toString(), getContext());
                break;
            case R.id.btn_retry:
                dismiss();
                break;
        }
        Log.i("onClick: ", "Here");

    }

}
