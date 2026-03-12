package com.example.myfinal.Start;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myfinal.PostCode.PostFeed;
import com.example.myfinal.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    // הגדרת משתני ה-UI
    private EditText emailInputLogin, passwordInputLogin;
    private Button btnGoToSignUp, loginButton, btnForgotPassword;
    private Toolbar toolbar;

    // משתנה לאימות מול Firebase
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // אתחול Firebase Auth
        auth = FirebaseAuth.getInstance();

        // קישור רכיבי ה-XML לקוד
        emailInputLogin = findViewById(R.id.emailInputLogin);
        passwordInputLogin = findViewById(R.id.passwordInputLogin); // הוספתי קישור לסיסמה שהיה חסר
        loginButton = findViewById(R.id.loginButton);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);

        // הגדרת ה-Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // מאזין לכפתור התחברות
        loginButton.setOnClickListener(v -> loginUser());

        // מעבר למסך הרשמה
        btnGoToSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LogIn.this, SignUp.class);
            startActivity(intent);
        });

        // מעבר למסך שחזור סיסמה
        btnForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LogIn.this, ForgotPassword.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        // שליפת הטקסט מהשדות
        String email = emailInputLogin.getText().toString().trim();
        String password = passwordInputLogin.getText().toString().trim();

        // שלב 1: בדיקה מקומית שהשדות אינם ריקים
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "אנא הזן אימייל וסיסמה", Toast.LENGTH_SHORT).show();
            return;
        }

        // שלב 2: ניסיון התחברות מול השרת
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // הצלחה - מעבר למסך הפיד
                        Toast.makeText(LogIn.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();

                        // יצירת Intent למסך הפיד
                        Intent intent = new Intent(LogIn.this, PostFeed.class);

                        // ניקוי היסטוריית המסכים (כדי שלא יחזרו ל-Login בלחיצה על 'חזור')
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        finish(); // סגירת מסך ההתחברות
                    } else {
                        // כישלון - הצגת הודעה מתאימה
                        Toast.makeText(LogIn.this, "התחברות נכשלה: וודא שהפרטים נכונים", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // חזרה למסך הקודם בלחיצה על החץ ב-Toolbar
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuitemE) {
            showExitDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage("אתה בטוח שאתה רוצה לצאת?")
                .setPositiveButton("כן", (dialog, which) -> {
                    finishAffinity();
                    System.exit(0);
                })
                .setNegativeButton("לא", (dialog, which) -> dialog.dismiss())
                .show();
    }
}