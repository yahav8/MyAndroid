package com.example.myfinal.Start;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myfinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private EditText nameInput, idInput, emailInput, phoneInput, rankInput, courseInput, releaseDateInput, passwordInput;
    private Button registerButton, btnGoToLogIn;
    private ImageView uploadImagePlaceholder;
    private Toolbar toolbar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Track chosen avatar name (default p1)
    private String selectedAvatar = "p1";

    // Launcher for the Avatar Selection
    private final ActivityResultLauncher<Intent> avatarPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedAvatar = result.getData().getStringExtra("selected_avatar");
                    // Change the placeholder to the chosen image
                    int resId = getResources().getIdentifier(selectedAvatar, "drawable", getPackageName());
                    uploadImagePlaceholder.setImageResource(resId);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameInput = findViewById(R.id.nameInput);
        idInput = findViewById(R.id.idInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        rankInput = findViewById(R.id.rankInput);
        courseInput = findViewById(R.id.courseInput);
        releaseDateInput = findViewById(R.id.releaseDateInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        btnGoToLogIn = findViewById(R.id.btnGoToLogIn);
        uploadImagePlaceholder = findViewById(R.id.uploadImagePlaceholder);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupRankPicker();
        setupDatePicker();

        // New Click Listener to open the Avatar Picker
        uploadImagePlaceholder.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, AvatarPicker.class);
            avatarPickerLauncher.launch(intent);
        });

        registerButton.setOnClickListener(v -> performSignUp());
        btnGoToLogIn.setOnClickListener(v -> finish());
    }

    private void performSignUp() {
        String name = nameInput.getText().toString().trim();
        String id = idInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String rank = rankInput.getText().toString().trim();
        String course = courseInput.getText().toString().trim();
        String date = releaseDateInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (name.isEmpty() || id.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                rank.isEmpty() || course.isEmpty() || date.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Pattern.compile("^[a-zA-Zא-ת]{2,}\\s[a-zA-Zא-ת]{2,}$").matcher(name).matches()) {
            nameInput.setError("נא להזין שם פרטי ושם משפחה (לפחות 2 תווים לכל אחד)");
            return;
        }

        if (id.length() != 9 || !id.matches("\\d+")) {
            idInput.setError("תעודת זהות חייבת להכיל 9 ספרות");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("כתובת אימייל לא תקינה");
            return;
        }

        if (phone.length() != 10 || !phone.matches("\\d+")) {
            phoneInput.setError("מספר טלפון חייב להכיל בדיוק 10 ספרות");
            return;
        }

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
        if (!Pattern.compile(passwordRegex).matcher(password).matches()) {
            passwordInput.setError("סיסמה חייבת להכיל 6 תווים, אות, מספר וסימן מיוחד");
            return;
        }

        Toast.makeText(this, "מתחיל תהליך הרשמה...", Toast.LENGTH_SHORT).show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToFirestore(name, email);
                    } else {
                        Toast.makeText(this, "שגיאה: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String name, String email) {
        String uid = auth.getCurrentUser().getUid();

        getSharedPreferences("prefs", MODE_PRIVATE)
                .edit()
                .putString("username", name)
                .putInt("stars", 0)
                .putInt("trophies", 0)
                .apply();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("id", idInput.getText().toString().trim());
        user.put("phonNum", phoneInput.getText().toString().trim());
        user.put("rank", rankInput.getText().toString().trim());
        user.put("courseNum", courseInput.getText().toString().trim());
        user.put("releaseDate", releaseDateInput.getText().toString().trim());
        user.put("stars", 0);
        user.put("trophies", 0);
        user.put("profileImageUrl", selectedAvatar); // Saves "p1", "p2", etc.

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUp.this, "הנתונים נשמרו בהצלחה!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, OpenPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בשמירת נתונים: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setupDatePicker() {
        releaseDateInput.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                releaseDateInput.setText(day + "/" + (month + 1) + "/" + year);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupRankPicker() {
        final String[] ranks = {"טוראי", "רב טוראי", "סמל", "סמל ראשון", "סגן משנה", "סגן", "סרן", "רב סרן", "סגן אלוף", "אלוף משנה", "תת אלוף", "אלוף", "רב אלוף"};
        rankInput.setOnClickListener(v -> {
            new AlertDialog.Builder(this).setTitle("בחר דרגה")
                    .setItems(ranks, (dialog, which) -> rankInput.setText(ranks[which])).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuitemE) {
            new AlertDialog.Builder(this)
                    .setMessage("אתה בטוח שאתה רוצה לצאת מהאפליקציה?")
                    .setPositiveButton("כן", (dialog, which) -> {
                        finishAffinity();
                        System.exit(0);
                    })
                    .setNegativeButton("לא", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}