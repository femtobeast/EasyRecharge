package com.manishbsta.easyrecharge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMG_PERMISSION_CODE = 200;

    private ImageView imgViewCaptured;
    private TextInputEditText etPin;

    private RelativeLayout rlMain;

    private Bitmap img;
    CropImage.ActivityResult result;
    String pin, sim_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        Intent intent = getIntent();
        sim_code = intent.getStringExtra("sim_code");
        pickCamera();
    }

    private void init() {
        imgViewCaptured = findViewById(R.id.imgViewCaptured);
        Button btnRecharge = findViewById(R.id.btnRecharge);
        etPin = findViewById(R.id.etPin);
        rlMain = findViewById(R.id.rlMain);

        btnRecharge.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        pin = etPin.getText().toString().trim();

        if (id == R.id.btnRecharge) {
            if (TextUtils.isEmpty(pin)) {

                showSnackBar("PIN is empty!", "Scan Card");

            } else if (pin.length() < 16) {

                showSnackBar("PIN digits less than 16", "Retake Scan");

            } else {
                //intent to dial number directly
                String pinWithCode = "*" + sim_code + "*" + pin + "#";

                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse(String.format("tel:%s", Uri.encode(pinWithCode))));
                try {
                    startActivity(in);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "Could not find an activity to place the call.", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private void showSnackBar(String message, String action) {
        Snackbar.make(rlMain, message, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickCamera();
                    }
                })
                .setActionTextColor(Color.parseColor("#FFB0D9B9"))
                .show();
    }

    private boolean checkPermission() {
        boolean camPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean galleryPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        boolean phonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == (PackageManager.PERMISSION_GRANTED);

        return camPermission && galleryPermission && phonePermission;
    }


    private void askPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE},
                IMG_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickCamera();
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickCamera() {
        if (!checkPermission()) {
            askPermission();

            if (checkPermission()) {
                CropImage.activity()
                        .setAspectRatio(5, 1)
                        .setFixAspectRatio(true)
                        .setAllowFlipping(false)
                        .start(this);
            }
        } else {
            CropImage.activity()
                    .setAspectRatio(5, 1)
                    .setFixAspectRatio(true)
                    .setAllowFlipping(false)
                    .start(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                try {
                    img = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

                    if (img != null) {
                        convertImageToPIN();
                        imgViewCaptured.setImageBitmap(img);
                    }

                } catch (IOException e) {
                    System.out.println("" + e.toString());
                }
            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Log.d("onActivityResult", "" + result.getError().toString());
        } else {
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
        }

    }

    private void convertImageToPIN() {
        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(img).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            StringBuilder sb = new StringBuilder();

            //get text from string builder until there is no text
            for (int i = 0; i < items.size(); i++) {
                TextBlock chars = items.valueAt(i);
                sb.append(chars.getValue());
            }

            //set text to etPin
            etPin.getText().clear();
            String pin = sb.toString().trim();
            etPin.setText(pin.replaceAll("[^0-9]", ""));
        }
    }
}