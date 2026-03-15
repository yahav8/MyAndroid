package com.example.myfinal.Start;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinal.Profile;
import com.example.myfinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private EditText etName, etId, etEmail, etPhone, etRank, etCourse, etDate, etPassword;
    private Button btnRegister, btnBackToLogin;
    private ImageView ivAvatar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String selectedAvatar = "p1";

    private final ActivityResultLauncher<Intent> avatarPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedAvatar = result.getData().getStringExtra("selected_avatar");
                    int resId = getResources().getIdentifier(selectedAvatar, "drawable", getPackageName());
                    ivAvatar.setImageResource(resId);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.nameInput);
        etId = findViewById(R.id.idInput);
        etEmail = findViewById(R.id.emailInput);
        etPhone = findViewById(R.id.phoneInput);
        etRank = findViewById(R.id.rankInput);
        etCourse = findViewById(R.id.courseInput);
        etDate = findViewById(R.id.releaseDateInput);
        etPassword = findViewById(R.id.passwordInput);
        btnRegister = findViewById(R.id.registerButton);
        btnBackToLogin = findViewById(R.id.btnGoToLogIn);
        ivAvatar = findViewById(R.id.uploadImagePlaceholder);

        setupRankPicker();
        setupDatePicker();

        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, AvatarPicker.class);
            avatarPickerLauncher.launch(intent);
        });

        btnRegister.setOnClickListener(v -> performSignUp());
        btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void performSignUp() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("אימייל לא תקין");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveToFirestore();
                    } else {
                        Toast.makeText(this, "שגיאה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveToFirestore() {
        String uid = auth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        user.put("name", etName.getText().toString().trim());
        user.put("email", etEmail.getText().toString().trim());
        user.put("id", etId.getText().toString().trim());
        user.put("phonNum", etPhone.getText().toString().trim());
        user.put("rank", etRank.getText().toString().trim());
        user.put("courseNum", etCourse.getText().toString().trim());
        user.put("releaseDate", etDate.getText().toString().trim());
        user.put("profileImageUrl", selectedAvatar);

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUp.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, Profile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    private void setupRankPicker() {
        // רשימה מלאה של דרגות
        final String[] ranks = {"טוראי", "רב טוראי", "סמל", "סמל ראשון", "רב סמל", "רב סמל ראשון", "רב סמל מתקדם", "רב סמל בכיר", "רב נגד", "סגן משנה", "סגן", "סרן", "רב סרן", "סגן אלוף", "אלוף משנה"};
        etRank.setOnClickListener(v -> {
            new AlertDialog.Builder(this).setTitle("בחר דרגה")
                    .setItems(ranks, (dialog, which) -> etRank.setText(ranks[which])).show();
        });
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                etDate.setText(day + "/" + (month + 1) + "/" + year);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }
}