package com.example.myfinal.PostCode;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinal.R;

public class PostFeed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // השורה הזו מחברת את ה-Java ל-XML
        setContentView(R.layout.activity_post_feed);

        // הודעה קטנה כדי שנדע שהכל עובד
        Toast.makeText(this, "התחברת בהצלחה לפיד!", Toast.LENGTH_SHORT).show();
    }
}