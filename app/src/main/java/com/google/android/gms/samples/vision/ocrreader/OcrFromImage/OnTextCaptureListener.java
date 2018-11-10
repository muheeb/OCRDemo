package com.google.android.gms.samples.vision.ocrreader.OcrFromImage;

import android.content.Context;

import com.google.android.gms.samples.vision.ocrreader.dialog.ConfirmaDataDialog;

public interface OnTextCaptureListener {
    public void onTextCapture(String message, Context context);
}
