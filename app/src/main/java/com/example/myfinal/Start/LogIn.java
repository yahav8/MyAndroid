package com.example.myfinal.Start;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinal.PostCode.PostFeed; // ודאי שהנתיב הזה נכון אצלך
import com.example.myfinal.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private Button btnLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // אתחול Firebase Auth
        auth = FirebaseAuth.getInstance();

        // קישור רכיבי הממשק (ודאי שה-IDs האלו קיימים ב-XML שלך)
        etLoginEmail = findViewById(R.id.emailInputLogin);
        etLoginPassword = findViewById(R.id.passwordInputLogin);
        btnLogin = findViewById(R.id.loginButton);

        btnLogin.setOnClickListener(v -> {
            String email = etLoginEmail.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "נא למלא אימייל וסיסמה", Toast.LENGTH_SHORT).show();
                return;
            }

            // ביצוע התחברות
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // 1. הודעת הצלחה
                            Toast.makeText(LogIn.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();

                            // 2. מעבר למסך הפיד
                            Intent intent = new Intent(LogIn.this, PostFeed.class);

                            // 3. פקודה שמוחקת את מסך ההתחברות מהזיכרון כדי שלא יחזור אליו בלחיצה על "אחורה"
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                            finish(); // סגירת מסך ההתחברות
                        } else {
                            // הודעת שגיאה במקרה של פרטים לא נכונים
                            Toast.makeText(this, "שגיאה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}