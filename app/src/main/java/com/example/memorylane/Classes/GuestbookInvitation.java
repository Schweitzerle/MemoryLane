package com.example.memorylane.Classes;

import com.example.memorylane.Database.UserSession;

import java.io.Serializable;
import java.util.UUID;

public class GuestbookInvitation implements Serializable {
    private String invitationID;
    private String guestbookId;
    private String guestbookName;
    private String senderId;
    private String senderName;
    private String recipientId;
    private String recipientName;

    public GuestbookInvitation() {}

    public GuestbookInvitation(Guestbook guestbook, UserSession userSender, User userReceiver) {
        this.invitationID = UUID.randomUUID().toString();
        this.guestbookId = guestbook.getId();
        this.guestbookName = guestbook.getName();
        this.senderId = userSender.getCurrentUser().getUid();
        this.recipientId = userReceiver.getUserSessionID();
        this.recipientName = userReceiver.getUsername();
    }

    public String getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(String invitationID) {
        this.invitationID = invitationID;
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


