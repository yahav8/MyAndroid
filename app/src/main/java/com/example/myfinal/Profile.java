package com.example.myfinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.myfinal.PostCode.PostCreator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    // הגדרת רכיבי התצוגה (TextViews)
    private TextView tvProfName;
    private TextView tvProfCourse;
    private TextView tvProfRank;
    private TextView tvProfId;
    private TextView tvProfPhone;
    private TextView tvProfEmail;
    private TextView tvProfDate;

    // הגדרת רכיבי תמונה וכפתור
    private ImageView ivProfImage;
    private CardView cardFab;

    // רכיבי Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // אתחול Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // קישור רכיבים מה-XML ל-Java (IDs מעודכנים)
        tvProfName = findViewById(R.id.tvProfName);
        tvProfCourse = findViewById(R.id.tvProfCourse);
        tvProfRank = findViewById(R.id.tvProfRank);
        tvProfId = findViewById(R.id.tvProfId);
        tvProfPhone = findViewById(R.id.tvProfPhone);
        tvProfEmail = findViewById(R.id.tvProfEmail);
        tvProfDate = findViewById(R.id.tvProfDate);
        ivProfImage = findViewById(R.id.ivProfImage);

        // תיקון השגיאה: חיבור ל-cardFab כפי שמופיע ב-XML
        cardFab = findViewById(R.id.cardFab);

        // הגדרת ה-Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // הגדרת כפתור חזרה ב-Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // הגדרת לחיצה על כפתור הוספת פוסט
        cardFab.setOnClickListener(v -> {
            // מעבר למסך יצירת פוסט
            Intent intent = new Intent(Profile.this, PostCreator.class);
            startActivity(intent);
        });

        // טעינת הנתונים מ-Firestore
        loadData();
    }

    private void loadData() {
        // בדיקה אם המשתמש מחובר
        if (auth.getCurrentUser() == null) {
            return;
        }

        // קבלת ה-UID של המשתמש הנוכחי
        String uid = auth.getCurrentUser().getUid();

        // שליפת המסמך של המשתמש מהאוסף "users"
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // עדכון הטקסט ברכיבים מהנתונים במסמך
                        tvProfName.setText(doc.getString("name"));
                        tvProfEmail.setText(doc.getString("email"));
                        tvProfId.setText(doc.getString("id"));
                        tvProfPhone.setText(doc.getString("phonNum"));
                        tvProfRank.setText(doc.getString("rank"));
                        tvProfCourse.setText(doc.getString("courseNum"));
                        tvProfDate.setText(doc.getString("releaseDate"));

                        // טיפול בתמונת הפרופיל (אייקון)
                        String avatar = doc.getString("profileImageUrl");
                        if (avatar != null) {
                            // המרת שם האייקון למזהה משאב (ID)
                            int resId = getResources().getIdentifier(avatar, "drawable", getPackageName());
                            if (resId != 0) {
                                ivProfImage.setImageResource(resId);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // הצגת הודעת שגיאה במקרה של כישלון
                    Toast.makeText(this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // פונקציה המאפשרת לכפתור החזרה ב-Toolbar לעבוד
        onBackPressed();
        return true;
    }
}