package com.example.myfinal.PostCode;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myfinal.Profile;
import com.example.myfinal.R;
import com.example.myfinal.Start.OpenPage;

public class PostFeed extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_feed);

        // אתחול הטולבר
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // מבטל את כותרת ברירת המחדל של האפליקציה כדי שה-TextView שלנו יהיה באמצע
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    // יצירת התפריט
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feed, menu);
        return true;
    }

    // טיפול בלחיצות על התפריט
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // יצירת Intent למעבר למסך הפרופיל
            Intent intent = new Intent(PostFeed.this, Profile.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_summary) {
            // מעבר למסך דף מסכם (נניח ששמו SummaryActivity)
            // Intent intent = new Intent(this, SummaryActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "מעבר לדף מסכם...", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("האם אתה בטוח שברצונך להתנתק?");
        builder.setPositiveButton("כן", (dialog, which) -> {
            // חזרה למסך הפתיחה וניקוי המחסנית
            Intent intent = new Intent(PostFeed.this, OpenPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}