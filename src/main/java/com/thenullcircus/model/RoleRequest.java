package com.thenullcircus.model;

import lombok.Data;

import java.util.UUID;
@Data
public class RoleRequest {
    private final UUID requestId;
    private final UUID userId;
    private final RequestedRole requestedRole;
    private final String reason;
    private Status status;
    private UUID ringleaderId;

    public RoleRequest(UUID requestId, UUID userId, RequestedRole requestedRole, String reason, Status status, UUID ringleaderId) {
        this.requestId = requestId;
        this.userId = userId;
        this.requestedRole = requestedRole;
        this.reason = reason;
        this.status = status;
        this.ringleaderId = ringleaderId;
    }

    public RoleRequest(UUID userId, RequestedRole requestedRole, String reason ) {
        this.requestId = UUID.randomUUID();
        this.userId = userId;
        this.requestedRole = requestedRole;
        this.reason = reason;
    }

}
