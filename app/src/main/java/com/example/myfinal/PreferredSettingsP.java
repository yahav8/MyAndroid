package com.example.myfinal;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.*; // מייבא את כל רכיבי ה-UI (Button, TextView וכו')

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

// ייבוא מנהל ההגדרות מהחבילה שלו - זה פותר את השגיאות האדומות
import com.example.myfinal.PostCode.SettingsManager;

public class PreferredSettingsP extends AppCompatActivity {

    // רכיבי ה-UI במסך
    private SwitchCompat swDarkMode;
    private RadioGroup rgAppTextSize;
    private RadioButton rbSmall, rbMedium, rbLarge;
    private TextView tvPostPreview; // ה-ID שתוקן מה-XML
    private TextView tvPostSizeLabel;
    private SeekBar sbPostTextSize;
    private Spinner spFontType;
    private Button btnPickColor, btnSaveSettings;

    // משתני עזר לשמירת הבחירה הזמנית
    private String selectedColor = "#000000";
    private String selectedFont = "Default";
    private SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_settings_p);

        // אתחול מנהל ההגדרות (Singleton)
        settingsManager = SettingsManager.getInstance(this);

        initViews();       // קישור הרכיבים ל-XML
        setupSpinner();    // הגדרת רשימת הפונטים
        loadSettings();    // טעינת ההגדרות הקיימות למסך
        setListeners();    // הגדרת פעולות ללחיצות ושינויים
    }

    private void initViews() {
        swDarkMode = findViewById(R.id.swDarkMode);
        rgAppTextSize = findViewById(R.id.rgAppTextSize);
        rbSmall = findViewById(R.id.rbSmall);
        rbMedium = findViewById(R.id.rbMedium);
        rbLarge = findViewById(R.id.rbLarge);

        // כאן היה ה-Cannot Resolve Symbol - עכשיו זה תואם ל-XML!
        tvPostPreview = findViewById(R.id.tvPostPreview);

        tvPostSizeLabel = findViewById(R.id.tvPostSizeLabel);
        sbPostTextSize = findViewById(R.id.sbPostTextSize);
        spFontType = findViewById(R.id.spFontType);
        btnPickColor = findViewById(R.id.btnPickColor);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
    }

    private void setupSpinner() {
        String[] fonts = {"Default", "Sans Serif", "Serif", "Monospace"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fonts);
        spFontType.setAdapter(adapter);
    }

    private void loadSettings() {
        // שימוש במנהל ההגדרות כדי לעדכן את הממשק
        swDarkMode.setChecked(settingsManager.isDarkMode());

        int size = settingsManager.getPostTextSize();
        selectedColor = settingsManager.getPostTextColor();
        selectedFont = settingsManager.getPostFontType();

        tvPostPreview.setTextSize(size);
        tvPostPreview.setTextColor(Color.parseColor(selectedColor));
        sbPostTextSize.setProgress(size);
        tvPostSizeLabel.setText("גודל גופן בפוסט: " + size);

        // עדכון כפתורי הרדיו של גודל האפליקציה
        String appSize = settingsManager.getAppTextSize();
        if (appSize.equals("Small")) rbSmall.setChecked(true);
        else if (appSize.equals("Large")) rbLarge.setChecked(true);
        else rbMedium.setChecked(true);
    }

    private void setListeners() {
        // שינוי גודל טקסט בזמן אמת (SeekBar)
        sbPostTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPostPreview.setTextSize(progress);
                tvPostSizeLabel.setText("גודל גופן בפוסט: " + progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // שינוי סוג פונט בזמן אמת (Spinner)
        spFontType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFont = parent.getItemAtPosition(position).toString();
                updateFontPreview(selectedFont);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnPickColor.setOnClickListener(v -> openColorPickerDialog());
        btnSaveSettings.setOnClickListener(v -> saveAllSettings());
    }

    private void updateFontPreview(String font) {
        Typeface tf = Typeface.DEFAULT;
        switch (font) {
            case "Sans Serif": tf = Typeface.SANS_SERIF; break;
            case "Serif": tf = Typeface.SERIF; break;
            case "Monospace": tf = Typeface.MONOSPACE; break;
        }
        tvPostPreview.setTypeface(tf);
    }

    private void openColorPickerDialog() {
        String[] colors = {"שחור", "אדום", "כחול", "ירוק"};
        String[] hexColors = {"#000000", "#FF0000", "#0000FF", "#008000"};

        new AlertDialog.Builder(this)
                .setTitle("בחר צבע לטקסט")
                .setItems(colors, (dialog, which) -> {
                    selectedColor = hexColors[which];
                    tvPostPreview.setTextColor(Color.parseColor(selectedColor));
                }).show();
    }

    private void saveAllSettings() {
        // שמירת כל הנתונים ל-SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();

        editor.putBoolean("isDarkMode", swDarkMode.isChecked());
        editor.putInt("postTextSize", sbPostTextSize.getProgress());
        editor.putString("postTextColor", selectedColor);
        editor.putString("postFontType", selectedFont);

        // בדיקה איזה רדיו-באטן נבחר
        if (rbSmall.isChecked()) editor.putString("appTextSize", "Small");
        else if (rbLarge.isChecked()) editor.putString("appTextSize", "Large");
        else editor.putString("appTextSize", "Medium");

        editor.apply(); // ביצוע השמירה בפועל

        Toast.makeText(this, "ההגדרות נשמרו בהצלחה!", Toast.LENGTH_SHORT).show();
        finish(); // חזרה למסך הקודם
    }
}