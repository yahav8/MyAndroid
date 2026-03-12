package com.example.myfinal;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private TextView profName, profCourse, profRank, profId, profPhone, profEmail, profDate;
    private ImageView profImage;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // New Simplified IDs
        profName = findViewById(R.id.profName);
        profCourse = findViewById(R.id.profCourse);
        profRank = findViewById(R.id.profRank);
        profId = findViewById(R.id.profId);
        profPhone = findViewById(R.id.profPhone);
        profEmail = findViewById(R.id.profEmail);
        profDate = findViewById(R.id.profDate);
        profImage = findViewById(R.id.profImage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadData();
    }

    private void loadData() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        profName.setText(doc.getString("name"));
                        profEmail.setText(doc.getString("email"));
                        profId.setText(doc.getString("id"));
                        profPhone.setText(doc.getString("phonNum"));
                        profRank.setText(doc.getString("rank"));
                        profCourse.setText(doc.getString("courseNum"));
                        profDate.setText(doc.getString("releaseDate"));

                        String avatar = doc.getString("profileImageUrl");
                        if (avatar != null) {
                            int resId = getResources().getIdentifier(avatar, "drawable", getPackageName());
                            if (resId != 0) profImage.setImageResource(resId);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}