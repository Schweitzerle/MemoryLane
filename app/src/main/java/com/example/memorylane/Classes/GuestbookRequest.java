package com.example.memorylane.Classes;

import com.example.memorylane.Database.UserSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuestbookRequest implements Serializable {
    private String requestID;
    private String guestbookId;
    private String guestbookName;
    private String senderId;
    private String senderName;
    private String recipientId;

    public GuestbookRequest() {
    }

    public GuestbookRequest(UserSession userSession, Guestbook guestbook) {
        this.requestID = UUID.randomUUID().toString();
        this.guestbookId = guestbook.getId();
        this.guestbookName = guestbook.getName();
        this.senderId = userSession.getCurrentUser().getUid();
        this.senderName = userSession.getCurrentUser().getDisplayName();
        this.recipientId = guestbook.getCreatorId();
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getGuestbookId() {
        return guestbookId;
    }

    public void setGuestbookId(String guestbookId) {
        this.guestbookId = guestbookId;
    }

    public String getGuestbookName() {
        return guestbookName;
    }

    public void setGuestbookName(String guestbookName) {
        this.guestbookName = guestbookName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("senderId", senderId);
        result.put("guestbookId", guestbookId);
        result.put("sendername", senderName);
        result.put("recipientId", recipientId);
        result.put("guestbookName", guestbookName);
        result.put("requestId", requestID);
        return result;
    }
}

