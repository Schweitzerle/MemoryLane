package com.example.memorylane.Classes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GuestEntry implements Serializable {
    public String pictureURL;
    public String userID;
    public String description;
    public String entryID;

    public GuestEntry() {
        // Default constructor required for calls to DataSnapshot.getValue(Guestbook.class)
    }

    public GuestEntry(String entryID, String pictureURL, String description, String userID) {
        this.entryID = entryID;
        this.pictureURL = pictureURL;
        this.userID = userID;
        this.description = description;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("entryID", entryID);
        result.put("pictureURL", pictureURL);
        result.put("userID", userID);
        result.put("description", description);
        return result;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getEntryID() {
        return entryID;
    }

    public void setEntryID(String entryID) {
        this.entryID = entryID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

