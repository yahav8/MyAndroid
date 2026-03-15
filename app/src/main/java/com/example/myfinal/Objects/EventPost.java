package com.example.myfinal.Objects;

public class EventPost extends Post {
    private String eventDate;
    private String eventTime;
    private String location;
    private int maxParticipants;
    private int registeredUsers;

    // בנאי ריק חובה עבור Firebase
    public EventPost() {
        super();
    }

    // בנאי מלא
    public EventPost(String appTextSize,
                     String fontType,
                     boolean isDarkMode,
                     String textColor,
                     int textSize,
                     String headline,
                     String details,
                     String tags,
                     String authorUid,
                     String authorName,
                     String iconName, // הוספנו את המשתנה הזה כאן
                     long timestamp,
                     String eventDate,
                     String eventTime,
                     String location,
                     int maxParticipants,
                     int registeredUsers) {

        // כאן התיקון הקריטי - הוספתי את iconName ברשימה שנשלחת לאבא (Post)
        super(appTextSize,
                fontType,
                isDarkMode,
                textColor,
                textSize,
                headline,
                details,
                tags,
                authorUid,
                authorName,
                iconName, // זה השדה שהיה חסר וגרם לשגיאה
                timestamp);

        // אתחול המשתנים הייחודיים לאירוע
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.registeredUsers = registeredUsers;
    }

    // Getters and Setters
    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(int registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    @Override
    public String toString() {
        return "EventPost{" +
                "eventDate='" + eventDate + '\'' +
                ", location='" + location + '\'' +
                ", headline='" + headline + '\'' +
                ", isDarkMode=" + isDarkMode +
                ", textColor='" + textColor + '\'' +
                '}';
    }
}