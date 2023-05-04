package com.example.memorylane.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Guestbook {
    public String id;
    public String name;
    public String pictureUrl;
    public String description;
    public String creatorId;
    public String date;
    public List<String> members;
    public boolean isPublic;

    public Guestbook() {
        // Default constructor required for calls to DataSnapshot.getValue(Guestbook.class)
    }

    public Guestbook(String id, String name, String pictureUrl, String description, String creatorId, String date, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.description = description;
        this.creatorId = creatorId;
        this.isPublic = isPublic;
        this.date = date;
        this.members = new ArrayList<>();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("pictureUrl", pictureUrl);
        result.put("description", description);
        result.put("creatorId", creatorId);
        result.put("isPublic", isPublic);
        result.put("date", date);
        return result;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}

