package com.manishbsta.easyrecharge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        FloatingActionButton fabRecharge = findViewById(R.id.fabRecharge);

        fabRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SIMChooserDialog chooserDialog = new SIMChooserDialog();
                chooserDialog.show(getSupportFragmentManager(), "sim_chooser_dialog");
            }
        });


    }


}