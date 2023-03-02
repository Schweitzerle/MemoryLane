package com.example.memorylane.Classes;

import java.util.HashMap;
import java.util.Map;

public class Guestbook {
    public String id;
    public String name;
    public String pictureUrl;
    public String description;
    public String creatorId;
    public boolean isPublic;

    public Guestbook() {
        // Default constructor required for calls to DataSnapshot.getValue(Guestbook.class)
    }

    public Guestbook(String id, String name, String pictureUrl, String description, String creatorId, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.description = description;
        this.creatorId = creatorId;
        this.isPublic = isPublic;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("pictureUrl", pictureUrl);
        result.put("description", description);
        result.put("creatorId", creatorId);
        result.put("isPublic", isPublic);
        return result;
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

