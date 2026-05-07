package com.thenullcircus.dao;

import com.thenullcircus.model.RoleRequest;

import java.util.ArrayList;
import java.util.UUID;

public interface RoleRequestDAO {
    boolean submitRequest(RoleRequest request);
    ArrayList<RoleRequest> findAllPending();
    boolean approveRequest(UUID requestId, UUID ringleaderId);
    boolean rejectRequest(UUID requestId, UUID ringleaderId);
}
