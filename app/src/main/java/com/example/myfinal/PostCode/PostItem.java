package com.example.myfinal.PostCode;

import java.io.Serializable;

public class PostItem implements Serializable {
    private String postId;      // מזהה ייחודי
    private String headline;    // כותרת
    private String details;     // תוכן
    private String authorName;  // שם הכותב
    private String authorUid;   // מזהה כותב (ללייקים)
    private String hashtags;    // תגיות
    private long timestamp;     // זמן העלאה
    private String iconName;    // שם האייקון שנבחר (למשל "love")
    private int likes;          // מספר לייקים
    private boolean isEvent;    // האם זה אירוע?

    // שדות עיצוב (עבודה עם עצמים)
    private String textColor;
    private String fontType;
    private int textSize;

    // קונסטרקטור ריק חובה עבור Firebase
    public PostItem() {}

    // קונסטרקטור מלא ומעודכן
    public PostItem(String postId, String headline, String details, String authorName, String authorUid,
                    String hashtags, long timestamp, String iconName, boolean isEvent,
                    String textColor, String fontType, int textSize) {
        this.postId = postId;
        this.headline = headline;
        this.details = details;
        this.authorName = authorName;
        this.authorUid = authorUid;
        this.hashtags = hashtags;
        this.timestamp = timestamp;
        this.iconName = iconName;
        this.isEvent = isEvent;
        this.likes = 0;
        this.textColor = textColor;
        this.fontType = fontType;
        this.textSize = textSize;
    }

    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorUid() { return authorUid; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }
    public String getHashtags() { return hashtags; }
    public void setHashtags(String hashtags) { this.hashtags = hashtags; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public boolean isEvent() { return isEvent; }
    public void setEvent(boolean event) { isEvent = event; }
    public String getTextColor() {return textColor; }
    public void setTextColor(String textColor) { this.textColor = textColor; }
    public String getFontType() { return fontType; }
    public void setFontType(String fontType) { this.fontType = fontType; }
    public int getTextSize() { return textSize; }
    public void setTextSize(int textSize) { this.textSize = textSize; }
}