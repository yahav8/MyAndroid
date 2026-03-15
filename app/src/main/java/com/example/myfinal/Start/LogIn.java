package com.example.myfinal.Start;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinal.PostCode.PostFeed;
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

        auth = FirebaseAuth.getInstance();

        etLoginEmail = findViewById(R.id.emailInputLogin);
        etLoginPassword = findViewById(R.id.passwordInputLogin);
        btnLogin = findViewById(R.id.loginButton);

        btnLogin.setOnClickListener(v -> {
            String email = etLoginEmail.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "מלא פרטים", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // מעבר למסך הפיד לאחר התחברות
                            Intent intent = new Intent(LogIn.this, PostFeed.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "פרטים לא נכונים", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}