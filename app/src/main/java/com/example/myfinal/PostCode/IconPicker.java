package com.example.myfinal.PostCode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinal.R;

public class IconPicker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_picker);

        // רשימת ה-IDs של כל האייקונים שהגדרנו ב-XML
        int[] iconIds = {
                R.id.ic_hobby, R.id.ic_pet, R.id.ic_outdoor, R.id.ic_work,
                R.id.ic_recommend, R.id.ic_idea, R.id.ic_music, R.id.ic_food
        };

        // לולאה שעוברת על כל האייקונים ומגדירה להם לחיצה
        for (int id : iconIds) {
            View view = findViewById(id);
            if (view != null) {
                view.setOnClickListener(v -> {
                    // שליפת שם ה-ID (למשל "ic_hobby")
                    String selectedIconName = getResources().getResourceEntryName(v.getId());

                    // החזרת השם למסך הקודם
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selected_avatar", selectedIconName);
                    setResult(RESULT_OK, resultIntent);

                    // סגירת המסך וחזרה ל-PostCreator
                    finish();
                });
            }
        }
    }
}