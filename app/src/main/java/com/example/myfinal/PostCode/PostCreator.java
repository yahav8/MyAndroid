package com.example.myfinal.PostCode;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinal.R;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.vertexai.FirebaseVertexAI;
import com.google.firebase.vertexai.GenerativeModel;
import com.google.firebase.vertexai.java.GenerativeModelFutures;
import com.google.firebase.vertexai.type.Content;
import com.google.firebase.vertexai.type.GenerateContentResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PostCreator extends AppCompatActivity {

    private EditText etHeadline, etDetails;
    private TextView tvAiTagsDisplay;
    private Button btnUpload, btnAiHashtags;
    private ImageView ivPostImage;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SharedPreferences sp;

    // שם האייקון הנבחר - ברירת מחדל היא נורה
    private String selectedIconName = "ic_idea";

    // משתני AI
    private GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String PREF_NAME = "PostDraft";
    // שינוי לשעה אחת (60 דקות * 60 שניות * 1000 מילישניות)
    private static final long DRAFT_EXPIRY_MS = 60 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creator);

        // אתחול Firebase ו-SharedPreferences
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // הגדרת מודל ה-AI (Gemini)
        GenerativeModel gm = FirebaseVertexAI.getInstance().generativeModel("gemini-1.5-flash");
        model = GenerativeModelFutures.from(gm);

        // קישור רכיבי העיצוב (IDs)
        etHeadline = findViewById(R.id.et_post_headline);
        etDetails = findViewById(R.id.et_post_details);
        tvAiTagsDisplay = findViewById(R.id.tv_ai_tags_display);
        btnUpload = findViewById(R.id.btn_upload_post);
        btnAiHashtags = findViewById(R.id.btn_ai_hashtags);
        ivPostImage = findViewById(R.id.iv_new_post_image);

        // לחיצה על התמונה פותחת דיאלוג בחירה (כמו בהרשמה)
        ivPostImage.setOnClickListener(v -> showIconPickerDialog());

        btnAiHashtags.setOnClickListener(v -> generateAiTags());
        btnUpload.setOnClickListener(v -> uploadPost());

        restoreDraft();
    }

    // פונקציה להצגת דיאלוג בחירת אייקון
    private void showIconPickerDialog() {
        // רשימת שמות האייקונים כפי שסיכמנו
        final String[] iconNames = {"ic_hobby", "ic_pet", "ic_outdoor", "ic_work", "ic_recommend", "ic_idea", "ic_music", "ic_food", "ic_travel", "ic_group"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("בחר אייקון לפוסט");
        builder.setItems(iconNames, (dialog, which) -> {
            // שמירת השם הנבחר
            selectedIconName = iconNames[which];
            // עדכון התצוגה הויזואלית
            int resId = getResources().getIdentifier(selectedIconName, "drawable", getPackageName());
            ivPostImage.setImageResource(resId);
        });
        builder.show();
    }

    private void generateAiTags() {
        String details = etDetails.getText().toString().trim();
        if (details.isEmpty()) {
            Toast.makeText(this, "אנא כתוב תיאור לפני יצירת תגיות", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAiHashtags.setEnabled(false);
        tvAiTagsDisplay.setText("יוצר תגיות...");

        Content prompt = new Content.Builder()
                .addText("ניתוח טקסט: תן לי בדיוק 3 מילים רלוונטיות כשאילתות חיפוש או תגיות עבור הטקסט הבא. " +
                        "תחזיר רק את 3 המילים מופרדות בפסיקים, ללא הסברים נוספים. הטקסט: " + details)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                runOnUiThread(() -> {
                    tvAiTagsDisplay.setText(resultText);
                    btnAiHashtags.setEnabled(true);
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                runOnUiThread(() -> {
                    tvAiTagsDisplay.setText("שגיאה ביצירת תגיות");
                    btnAiHashtags.setEnabled(true);
                });
            }
        }, executor);
    }

    private void uploadPost() {
        String headline = etHeadline.getText().toString().trim();
        String details = etDetails.getText().toString().trim();
        String tags = tvAiTagsDisplay.getText().toString().trim();

        if (headline.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        // יצירת מפת הנתונים לשמירה ב-Firestore
        Map<String, Object> post = new HashMap<>();
        post.put("headline", headline);
        post.put("details", details);
        post.put("tags", tags);
        post.put("iconName", selectedIconName); // שמירת שם האייקון בלבד
        post.put("authorUid", auth.getCurrentUser().getUid());
        post.put("timestamp", System.currentTimeMillis());

        // שליפת שם המשתמש מה-Preferences הכלליים של האפליקציה
        String userName = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("username", "Anonymous");
        post.put("authorName", userName);

        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "פוסט פורסם בהצלחה!", Toast.LENGTH_SHORT).show();
                    clearDraft();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בפרסום", Toast.LENGTH_SHORT).show());
    }

    // שמירת טיוטה מקומית
    private void saveDraft() {
        sp.edit()
                .putString("headline", etHeadline.getText().toString())
                .putString("details", etDetails.getText().toString())
                .putString("icon", selectedIconName)
                .putLong("savedAt", System.currentTimeMillis())
                .apply();
    }

    // שחזור טיוטה עם בדיקת זמן (שעה)
    private void restoreDraft() {
        long savedTime = sp.getLong("savedAt", 0);
        if (System.currentTimeMillis() - savedTime < DRAFT_EXPIRY_MS) {
            etHeadline.setText(sp.getString("headline", ""));
            etDetails.setText(sp.getString("details", ""));
            selectedIconName = sp.getString("icon", "ic_idea");

            // עדכון האייקון ב-ImageView
            int resId = getResources().getIdentifier(selectedIconName, "drawable", getPackageName());
            ivPostImage.setImageResource(resId);
        } else {
            clearDraft(); // עברה יותר משעה - מנקים
        }
    }

    private void clearDraft() {
        sp.edit().clear().apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraft(); // שמירה בכל פעם שהמשתמש יוצא מהמסך
    }
}