package com.example.myfinal.Objects;

public class Post extends PreferredSettings {
    protected String headline;
    protected String details;
    protected String tags;
    protected String authorUid;
    protected String authorName;
    protected long timestamp;

    // Firestore חייב בנאי ריק
    public Post() {
        // ערכי ברירת מחדל לאבא
        super("Medium", "Default", false, "#000000", 18);
    }

    public Post(String appTextSize, String fontType, boolean isDarkMode, String textColor, int textSize,
                String headline, String details, String tags, String authorUid, String authorName, long timestamp) {

        // שליחת הגדרות התצוגה למחלקת האבא PreferredSettings
        super(appTextSize, fontType, isDarkMode, textColor, textSize);

        this.headline = headline;
        this.details = details;
        this.tags = tags;
        this.authorUid = authorUid;
        this.authorName = authorName;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getAuthorUid() { return authorUid; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}