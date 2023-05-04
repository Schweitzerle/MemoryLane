package com.example.memorylane.Classes;

import java.io.Serializable;

public class GuestbookInvitation implements Serializable {
    private String guestbookId;
    private String guestbookName;
    private String senderId;
    private String senderName;
    private String recipientId;
    private String recipientName;

    public GuestbookInvitation() {}

    public GuestbookInvitation(String guestbookId, String guestbookName, String senderId, String senderName, String recipientId, String recipientName) {
        this.guestbookId = guestbookId;
        this.guestbookName = guestbookName;
        this.senderId = senderId;
        this.senderName = senderName;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
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

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}


