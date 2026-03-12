package com.example.myfinal.PostCode;

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

    // AI Variables using the Java compatibility layer
    private GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String PREF_NAME = "PostDraft";
    private static final long DRAFT_EXPIRY_MS = 5 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creator);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // 1. Initialize the model (Gemini 1.5 Flash is recommended)
        GenerativeModel gm = FirebaseVertexAI.getInstance()
                .generativeModel("gemini-1.5-flash");

        // 2. Wrap it for Java ListenableFuture support
        model = GenerativeModelFutures.from(gm);

        etHeadline = findViewById(R.id.et_post_headline);
        etDetails = findViewById(R.id.et_post_details);
        tvAiTagsDisplay = findViewById(R.id.tv_ai_tags_display);
        btnUpload = findViewById(R.id.btn_upload_post);
        btnAiHashtags = findViewById(R.id.btn_ai_hashtags);
        ivPostImage = findViewById(R.id.iv_new_post_image);

        ivPostImage.setOnClickListener(v -> Toast.makeText(this, "Camera logic coming soon!", Toast.LENGTH_SHORT).show());

        btnAiHashtags.setOnClickListener(v -> generateAiTags());
        btnUpload.setOnClickListener(v -> uploadPost());

        restoreDraft();
    }

    private void generateAiTags() {
        String details = etDetails.getText().toString().trim();
        if (details.isEmpty()) {
            Toast.makeText(this, "אנא כתוב תיאור לפני יצירת תגיות", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAiHashtags.setEnabled(false);
        tvAiTagsDisplay.setText("יוצר תגיות...");

        // 3. Create the AI Prompt
        Content prompt = new Content.Builder()
                .addText("ניתוח טקסט: תן לי בדיוק 3 מילים רלוונטיות כשאילתות חיפוש או תגיות עבור הטקסט הבא. " +
                        "תחזיר רק את 3 המילים מופרדות בפסיקים, ללא הסברים נוספים. הטקסט: " + details)
                .build();

        // 4. Send the request
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);

        // 5. Handle the result using Guava Futures
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
                    Toast.makeText(PostCreator.this, "AI Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
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

        Map<String, Object> post = new HashMap<>();
        post.put("headline", headline);
        post.put("details", details);
        post.put("tags", tags);
        post.put("authorUid", auth.getCurrentUser().getUid());
        post.put("timestamp", System.currentTimeMillis());
        post.put("authorName", getSharedPreferences("prefs", MODE_PRIVATE).getString("username", "Anonymous"));

        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "פוסט פורסם בהצלחה!", Toast.LENGTH_SHORT).show();
                    clearDraft();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveDraft() {
        sp.edit()
                .putString("headline", etHeadline.getText().toString())
                .putString("details", etDetails.getText().toString())
                .putLong("savedAt", System.currentTimeMillis())
                .apply();
    }

    private void restoreDraft() {
        long savedTime = sp.getLong("savedAt", 0);
        if (System.currentTimeMillis() - savedTime < DRAFT_EXPIRY_MS) {
            etHeadline.setText(sp.getString("headline", ""));
            etDetails.setText(sp.getString("details", ""));
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