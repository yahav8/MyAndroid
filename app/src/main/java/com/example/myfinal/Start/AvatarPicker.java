package com.example.myfinal.Start;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinal.R;

public class AvatarPicker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_picker);

        int[] ids = {R.id.p1, R.id.p2, R.id.p3, R.id.p4, R.id.p5, R.id.p6, R.id.p7, R.id.p8};

        for (int id : ids) {
            ImageView img = findViewById(id);
            img.setOnClickListener(v -> {
                String name = getResources().getResourceEntryName(v.getId());
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_avatar", name);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        }
    }
}