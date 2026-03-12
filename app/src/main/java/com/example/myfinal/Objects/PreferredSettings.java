package com.example.myfinal.Objects;

public class PreferredSettings {
    protected boolean isDarkMode;
    protected String appTextSize;
    protected int textSize;
    protected String textColor;
    protected String fontType;

    public PreferredSettings(String appTextSize, String fontType, boolean isDarkMode, String textColor, int textSize) {
        this.appTextSize = appTextSize;
        this.fontType = fontType;
        this.isDarkMode = isDarkMode;
        this.textColor = textColor;
        this.textSize = textSize;
    }

    public String getAppTextSize() { return appTextSize; }
    public void setAppTextSize(String appTextSize) { this.appTextSize = appTextSize; }

    public String getFontType() { return fontType; }
    public void setFontType(String fontType) { this.fontType = fontType; }

    public boolean isDarkMode() { return isDarkMode; }
    public void setDarkMode(boolean darkMode) { isDarkMode = darkMode; }

    public String getTextColor() { return textColor; }
    public void setTextColor(String textColor) { this.textColor = textColor; }

    public int getTextSize() { return textSize; }
    public void setTextSize(int textSize) { this.textSize = textSize; }

    @Override
    public String toString() {
        return "PreferredSettings{" +
                "appTextSize='" + appTextSize + '\'' +
                ", isDarkMode=" + isDarkMode +
                ", textSize=" + textSize +
                ", textColor='" + textColor + '\'' +
                ", fontType='" + fontType + '\'' +
                '}';
    }
}