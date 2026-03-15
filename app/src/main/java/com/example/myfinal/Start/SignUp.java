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
import androidx.appcompat.widget.Toolbar;

import com.example.myfinal.Profile; // ייבוא של מסך הפרופיל
import com.example.myfinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    // רכיבי ממשק המשתמש
    private EditText etSignUpName;
    private EditText etSignUpId;
    private EditText etSignUpEmail;
    private EditText etSignUpPhone;
    private EditText etSignUpRank;
    private EditText etSignUpCourse;
    private EditText etSignUpDate;
    private EditText etSignUpPassword;
    private Button btnRegister;
    private Button btnGoToLogIn;
    private ImageView ivAvatarPlaceholder;

    // רכיבי Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String selectedAvatar = "p1";

    private final ActivityResultLauncher<Intent> avatarPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedAvatar = result.getData().getStringExtra("selected_avatar");
                    int resId = getResources().getIdentifier(selectedAvatar, "drawable", getPackageName());
                    ivAvatarPlaceholder.setImageResource(resId);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // קישור IDs לפי הסטנדרט החדש
        etSignUpName = findViewById(R.id.nameInput);
        etSignUpId = findViewById(R.id.idInput);
        etSignUpEmail = findViewById(R.id.emailInput);
        etSignUpPhone = findViewById(R.id.phoneInput);
        etSignUpRank = findViewById(R.id.rankInput);
        etSignUpCourse = findViewById(R.id.courseInput);
        etSignUpDate = findViewById(R.id.releaseDateInput);
        etSignUpPassword = findViewById(R.id.passwordInput);
        btnRegister = findViewById(R.id.registerButton);
        btnGoToLogIn = findViewById(R.id.btnGoToLogIn);
        ivAvatarPlaceholder = findViewById(R.id.uploadImagePlaceholder);

        setupRankPicker();
        setupDatePicker();

        ivAvatarPlaceholder.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, AvatarPicker.class);
            avatarPickerLauncher.launch(intent);
        });

        btnRegister.setOnClickListener(v -> performSignUp());

        btnGoToLogIn.setOnClickListener(v -> finish());
    }

    private void performSignUp() {
        String email = etSignUpEmail.getText().toString().trim();
        String password = etSignUpPassword.getText().toString().trim();

        // בדיקת תקינות מייל בסיסית ופשוטה
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etSignUpEmail.setError("כתובת אימייל לא תקינה");
            return;
        }

        if (password.length() < 6) {
            etSignUpPassword.setError("סיסמה חייבת להכיל לפחות 6 תווים");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToFirestore();
                    } else {
                        Toast.makeText(this, "שגיאה: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore() {
        String uid = auth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();

        user.put("name", etSignUpName.getText().toString().trim());
        user.put("email", etSignUpEmail.getText().toString().trim());
        user.put("id", etSignUpId.getText().toString().trim());
        user.put("phonNum", etSignUpPhone.getText().toString().trim());
        user.put("rank", etSignUpRank.getText().toString().trim());
        user.put("courseNum", etSignUpCourse.getText().toString().trim());
        user.put("releaseDate", etSignUpDate.getText().toString().trim());
        user.put("profileImageUrl", selectedAvatar);

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    // מעבר למסך פרופיל בסיום ההרשמה
                    Intent intent = new Intent(SignUp.this, Profile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    private void setupDatePicker() {
        etSignUpDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                etSignUpDate.setText(day + "/" + (month + 1) + "/" + year);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupRankPicker() {
        final String[] ranks = {"טוראי", "רב טוראי", "סמל", "סמל ראשון", "קצין"};
        etSignUpRank.setOnClickListener(v -> {
            new AlertDialog.Builder(this).setTitle("בחר דרגה")
                    .setItems(ranks, (dialog, which) -> etSignUpRank.setText(ranks[which])).show();
        });
    }
}