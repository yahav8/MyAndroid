package com.example.myfinal.Start;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myfinal.R;

public class OpenPage extends AppCompatActivity {

    private Button btnAboutProject, btnAboutProgrammer, btnSignUp, btnLogIn;
    private Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_page);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        btnAboutProject = findViewById(R.id.btnAboutProject);
        btnAboutProgrammer = findViewById(R.id.btnAboutProgrammer);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogIn);

        btnSignUp.setOnClickListener(v -> startActivity(new Intent(OpenPage.this, SignUp.class)));
        btnLogIn.setOnClickListener(v -> startActivity(new Intent(OpenPage.this, LogIn.class)));
        btnAboutProject.setOnClickListener(v -> startActivity(new Intent(OpenPage.this, AboutProject.class)));
        btnAboutProgrammer.setOnClickListener(v -> startActivity(new Intent(OpenPage.this, AboutProgrammer.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuitemE) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("אתה בטוח שאתה רוצה לצאת מהאפלקציה?");
            alertDialog.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                    finishAndRemoveTask();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            });
            alertDialog.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}