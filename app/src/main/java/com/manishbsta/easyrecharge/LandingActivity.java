package com.manishbsta.easyrecharge;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Toolbar toolbar = findViewById(R.id.toolbar_landing);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("EasyRecharge");

        FloatingActionButton fabRecharge = findViewById(R.id.fabRecharge);

        fabRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SIMChooserDialog chooserDialog = new SIMChooserDialog();
                chooserDialog.show(getSupportFragmentManager(), "sim_chooser_dialog");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menuAboutDeveloper) {
            Toast.makeText(this, "Developer information will be opened in your browser.", Toast.LENGTH_SHORT).show();

            String profileLink = "https://www.linkedin.com/in/m4nish/";
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(profileLink));
                startActivity(myIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Please install a web browser to view developer details!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        return true;
    }
}