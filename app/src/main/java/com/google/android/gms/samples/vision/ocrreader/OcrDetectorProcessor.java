/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.samples.vision.ocrreader.OcrFromImage.OnTextCaptureListener;
import com.google.android.gms.samples.vision.ocrreader.dialog.ConfirmaDataDialog;
import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;

import static com.google.android.gms.samples.vision.ocrreader.dialog.ConfirmaDataDialog.COUNTER;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 * TODO: Make this implement Detector.Processor<TextBlock> and add text to the GraphicOverlay
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> graphicOverlay;
    private Context context;
    private FragmentManager fragmentManager;
    SparseArray<TextBlock> items = new SparseArray<>();

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, Context context, FragmentManager fragmentManager) {
        graphicOverlay = ocrGraphicOverlay;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public OcrDetectorProcessor() {

    }

    // TODO:  Once this implements Detector.Processor<TextBlock>, implement the abstract methods.

    @Override
    public void release() {
        graphicOverlay.clear();
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        graphicOverlay.clear();
        items = detections.getDetectedItems();
        for (int i=0; i<items.size(); i++) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                Log.d("Processor", "Text detected: !"+item.getValue());
                OcrGraphic  graphic = new OcrGraphic(graphicOverlay, item, context);
                graphicOverlay.add(graphic);
            }
        }
    }
}
