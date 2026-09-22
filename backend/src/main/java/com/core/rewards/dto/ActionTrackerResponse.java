package com.core.rewards.dto;

public class ActionTrackerResponse {
    private final String traceId;
    private final String status;
    private final String message;

    public ActionTrackerResponse(String traceId, String status, String message) {
        this.traceId = traceId;
        this.status = status;
        this.message = message;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
