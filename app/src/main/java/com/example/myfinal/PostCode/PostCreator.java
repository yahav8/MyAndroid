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
import com.google.ai.client.generativeai.GenerativeModel; // הספרייה שמתאימה ל-API Key
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private String selectedIconName = "ic_idea";

    // משתני AI - שימוש בגרסה הפשוטה של Google AI
    private GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String PREF_NAME = "PostDraft";
    private static final long DRAFT_EXPIRY_MS = 60 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creator);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // --- כאן הכנס את ה-API KEY שלך ששמרת מקודם ---
        String myApiKey = "AIzaSyB7XoeCIX4gdMuvp59EEVL6VbRMeCQag1s";

        // הגדרת המודל בצורה פשוטה
        // השורה המתוקנת - שימוש בגרסה המדויקת והיציבה
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", myApiKey);
        model = GenerativeModelFutures.from(gm);

        etHeadline = findViewById(R.id.et_post_headline);
        etDetails = findViewById(R.id.et_post_details);
        tvAiTagsDisplay = findViewById(R.id.tv_ai_tags_display);
        btnUpload = findViewById(R.id.btn_upload_post);
        btnAiHashtags = findViewById(R.id.btn_ai_hashtags);
        ivPostImage = findViewById(R.id.iv_new_post_image);

        ivPostImage.setOnClickListener(v -> showIconPickerDialog());
        btnAiHashtags.setOnClickListener(v -> generateAiTags());
        btnUpload.setOnClickListener(v -> uploadPost());

        restoreDraft();
    }

    // הפונקציה שיוצרת את התגיות באמצעות ה-AI
    private void generateAiTags() {
        String details = etDetails.getText().toString().trim();
        if (details.isEmpty()) {
            Toast.makeText(this, "אנא כתוב תיאור לפני יצירת תגיות", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAiHashtags.setEnabled(false);
        tvAiTagsDisplay.setText("יוצר תגיות...");

        // יצירת הבקשה ל-AI (ה-Prompt)
        Content prompt = new Content.Builder()
                .addText("נתח את הטקסט הבא ותן לי רק 3 מילים שמתאימות כתגיות, מופרדות בפסיקים: " + details)
                .build();

        // שליחה לגוגל
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);

        // קבלת התשובה
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText(); // כאן מתקבלות 3 המילים
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

    // שאר הפונקציות (uploadPost, showIconPickerDialog וכו') נשארות בדיוק אותו דבר כפי ששלחת לי
    private void showIconPickerDialog() {
        final String[] iconNames = {"ic_hobby", "ic_pet", "ic_outdoor", "ic_work", "ic_recommend", "ic_idea", "ic_music", "ic_food", "ic_travel", "ic_group"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("בחר אייקון לפוסט");
        builder.setItems(iconNames, (dialog, which) -> {
            selectedIconName = iconNames[which];
            int resId = getResources().getIdentifier(selectedIconName, "drawable", getPackageName());
            ivPostImage.setImageResource(resId);
        });
        builder.show();
    }

    private void uploadPost() {
        String headline = etHeadline.getText().toString().trim();
        String details = etDetails.getText().toString().trim();
        String tags = tvAiTagsDisplay.getText().toString().trim();

        if (headline.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> post = new HashMap<>();
        post.put("headline", headline);
        post.put("details", details);
        post.put("tags", tags);
        post.put("iconName", selectedIconName);
        post.put("authorUid", auth.getCurrentUser().getUid());
        post.put("timestamp", System.currentTimeMillis());

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

    private void saveDraft() {
        sp.edit()
                .putString("headline", etHeadline.getText().toString())
                .putString("details", etDetails.getText().toString())
                .putString("icon", selectedIconName)
                .putLong("savedAt", System.currentTimeMillis())
                .apply();
    }

    private void restoreDraft() {
        long savedTime = sp.getLong("savedAt", 0);
        if (System.currentTimeMillis() - savedTime < DRAFT_EXPIRY_MS) {
            etHeadline.setText(sp.getString("headline", ""));
            etDetails.setText(sp.getString("details", ""));
            selectedIconName = sp.getString("icon", "ic_idea");
            int resId = getResources().getIdentifier(selectedIconName, "drawable", getPackageName());
            ivPostImage.setImageResource(resId);
        } else {
            clearDraft();
        }
    }

    private void clearDraft() {
        sp.edit().clear().apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraft();
    }
}