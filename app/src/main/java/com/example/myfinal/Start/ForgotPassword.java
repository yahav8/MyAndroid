package com.example.myfinal.Start;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myfinal.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText etEmail;
    private Button btnResetPassword;
    private Toolbar toolbar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        auth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnResetPassword.setOnClickListener(v -> resetPassword());
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim().toLowerCase();

        if (email.isEmpty()) {
            Toast.makeText(this, "אנא הזן כתובת אימייל", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    showDialog(
                            "נשלח בהצלחה",
                            "קישור לשחזור סיסמה נשלח לכתובת המייל שלך.\nאנא בדוק גם בתיקיית הספאם."
                    );
                })
                .addOnFailureListener(e -> {
                    showDialog(
                            "שגיאה",
                            "לא ניתן לשלוח מייל שחזור: " + e.getLocalizedMessage()
                    );
                });
    }


    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("אישור", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
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