package com.example.memorylane.Classes;

public class JoinResponse {
    private String toUserId;
    private String guestbookId;
    private boolean accepted;

    public JoinResponse() {}

    public JoinResponse(String toUserId, String guestbookId, boolean accepted) {
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
