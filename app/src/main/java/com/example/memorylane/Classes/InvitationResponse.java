package com.example.memorylane.Classes;

public class InvitationResponse {
    private String toUserId;
    private String guestbookId;
    private boolean accepted;

    public InvitationResponse() {}

    public InvitationResponse(String toUserId, String guestbookId, boolean accepted) {
        this.toUserId = toUserId;
        this.guestbookId = guestbookId;
        this.accepted = accepted;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getGuestbookId() {
        return guestbookId;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
