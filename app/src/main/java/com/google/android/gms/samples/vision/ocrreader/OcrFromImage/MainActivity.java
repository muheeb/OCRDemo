package com.google.android.gms.samples.vision.ocrreader.OcrFromImage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.android.gms.samples.vision.ocrreader.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView tvScanned;
    Button btnScanBack, btnScanFront;
    ImageView img;
    static final int REQUEST_IMAGE_CAPTURE_FRONT = 1;
    static final int REQUEST_IMAGE_CAPTURE_BACK = 11;
    public static int CODE;
    private static int REQUEST_PERMISSION_STORAGE = 2;

    static Bitmap bitmap;
    public static final String TAG = MainActivity.class.getName();
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    public static String mCurrentPhotoPath;
    public LinearLayout llScannedText;

    public ArrayList<String> listOfData = new ArrayList<>();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llScannedText = findViewById(R.id.ll_scanned_text);
        btnScanFront = findViewById(R.id.btn_camera);
        btnScanBack = findViewById(R.id.btn_camera2);

        img = findViewById(R.id.img);

        btnScanFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRequiredPermission(REQUEST_IMAGE_CAPTURE_FRONT);
            }
        });

        btnScanBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRequiredPermission(REQUEST_IMAGE_CAPTURE_BACK);
            }
        });



    }

    private void checkRequiredPermission(int code) {
        CODE = code;
        Log.i(TAG, "checkRequiredPermission: CODE"+CODE);
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        }
        else
            dispatchCameraIntent();
    }

    private void dispatchCameraIntent() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePic.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        if (takePic.resolveActivity(getPackageManager()) != null){
            File photo = null;
            try {
                photo = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(photo != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photo);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                Log.i(TAG, "dispatchCameraIntent: CODE"+CODE);
                startActivityForResult(takePic, CODE );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE_FRONT) {
            Bundle extras = data.getExtras();
//            bitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(bitmap);

            Log.i(TAG, "onActivityResult: FRONT IMAGE");

            File f = new File(mCurrentPhotoPath);
            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                img.setImageBitmap(bitmap);
                getImageText(bitmap);
            }
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE_BACK) {
            img.setImageBitmap(bitmap);

            Log.i(TAG, "onActivityResult: BACK IMAGE");

            File f = new File(mCurrentPhotoPath);
            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                img.setImageBitmap(bitmap);
                getImageText(bitmap);
            }
        }
    }

    private void getFrontText(FirebaseVisionText imageText) {
        for (FirebaseVisionText.TextBlock textBlock :  imageText.getTextBlocks()) {
            String blockText = textBlock.getText();
            Log.i(TAG, "onSuccess: FRONT Message: "+blockText);

            if (blockText.contains("Name") || blockText.contains("-") || blockText.contains("Nationality:")) {
                listOfData.add(blockText);
            }
        }

        setListFront(listOfData);
    }

    private void getBackText(FirebaseVisionText imageText) {
        for (FirebaseVisionText.TextBlock textBlock :  imageText.getTextBlocks()) {
            String blockText = textBlock.getText();
            Log.i(TAG, "onSuccess: BACK Message: "+blockText);

            if (blockText.contains("Sex") || blockText.contains("Date of Birth") || Utils.validateDate(blockText) || TextUtils.isDigitsOnly(blockText) || TextUtils.isDigitsOnly(blockText)) {
                listOfData.add(blockText);
            }
        }

        setListBack(listOfData);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE && grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
            dispatchCameraIntent();
        }
    }

    //Text-Recognition using Firebase Vision API
    public void getImageText(Bitmap bitmap) {

        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        textRecognizer.processImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String text = firebaseVisionText.getText();
//                        Log.i(TAG, "onSuccess: "+texDt);
                        if (CODE == REQUEST_IMAGE_CAPTURE_FRONT) {
                            Log.i(TAG, "onSuccess: FRONT");
                            getFrontText(firebaseVisionText);

                        }
                        else {
                            Log.i(TAG, "onSuccess: BACK");
                            getBackText(firebaseVisionText);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: "+e.getMessage());
                    }
                });

    }

    private void setListFront(ArrayList<String> listOfData) {
        final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        HashMap<String, String> list = new HashMap<>();
        for (String data : listOfData) {
            if (TextUtils.isDigitsOnly(data.replace("-", "")) && data.length() == 18) {
                list.put("Id", data);
            }
            else if (data.contains("Name:")) {
                list.put("Name", data);
            }
            else if (data.contains("Nationality:")) {
                list.put("Nationality", data);
            }
        }

        for (Map.Entry<String, String> entry : list.entrySet()) {
            Log.i(TAG, "setListFront: "+entry.getValue());
        }
    }

    private void setListBack(ArrayList<String> listOfData) {
        HashMap<String, String> list = new HashMap<>();
        for (String data : listOfData) {
            if (TextUtils.isDigitsOnly(data) && data.length() == 10) {
                list.put("Card Number", data);
            }
            else if (data.contains("Date of Birth")) {
                list.put("Date of Birth", data);
            }
            else if (data.contains("Sex")) {
                list.put("Sex", data);
            }
            else if (Utils.validateDate(data)) {
                list.put("Expiry", data);
            }
            else if (TextUtils.isDigitsOnly(data)) {
                list.put("Card Number", data);
            }
        }

        for (Map.Entry<String, String> entry : list.entrySet()) {
            Log.i(TAG, "setListBack: "+entry.getValue());
        }
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.e("Roatation", "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }

    //Text-Recognition using Google Vision API
//    public void getText(Bitmap bitmap) {
////        Bitmap bmp = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.emirates_id);
//        if (bitmap == null)
//            return;
//
//        Log.i("getText: ", "here");
//
//        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
//
//        if (!textRecognizer.isOperational()) {
//            Toast.makeText(this, "Could not get the text", Toast.LENGTH_SHORT).show();
//        } else {
//            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//            SparseArray<TextBlock> items = textRecognizer.detect(frame);
//            StringBuilder sb = new StringBuilder();
//
//            Log.i("getText: ", ""+items.size());
//            for (int i=0; i<items.size(); i++) {
//                TextBlock textBlock = items.valueAt(i);
//                sb.append(textBlock.getValue());
//                sb.append("\n");
//            }
//
//            tvScanned.setText(sb.toString());
//        }
//    }


}
