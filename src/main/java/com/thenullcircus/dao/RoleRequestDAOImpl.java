package com.thenullcircus.dao;

import com.thenullcircus.model.RequestedRole;
import com.thenullcircus.model.RoleRequest;
import com.thenullcircus.model.Status;
import com.thenullcircus.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoleRequestDAOImpl implements RoleRequestDAO {
    private static final Logger logger = Logger.getLogger(RoleRequestDAOImpl.class.getName());
    private static final String SUBMIT = "INSERT INTO role_requests(requestId, userId, requestedRole, reason) VALUES (?, ?, ?, ?)";
    private static final String FIND_APPENDING = "SELECT * FROM role_requests WHERE status = 'PENDING'";
    private static final String APPROVE = "UPDATE role_requests SET status = 'APPROVED', ringleaderId =? WHERE requestId = ?";
    private static final String REJECT = "UPDATE role_requests SET status = 'REJECTED', ringleaderId =? WHERE requestId = ?";
    private static final String FIND_BY_ID =  "SELECT * FROM role_requests WHERE requestId = ?";

    @Override
    public boolean submitRequest(RoleRequest request) {
        logger.info("[DAO_ROLE] Submitting new role request for User: " + request.getUserId());
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SUBMIT)){
            ps.setString(1, request.getRequestId().toString());
            ps.setString(2, request.getUserId().toString());
            ps.setString(3, request.getRequestedRole().toString());
            ps.setString(4, request.getReason());
            boolean success = ps.executeUpdate() > 0;
            if(success) logger.fine("[DAO_ROLE] Request " + request.getRequestId() + " successfully persisted.");
            return success;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DAO_ERROR] SQL Exception in submitRequest for user " + request.getUserId() + ": " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public ArrayList<RoleRequest> findAllPending() {
        logger.fine("[DAO_ROLE] Querying all PENDING role requests.");
        ArrayList<RoleRequest> requests = new ArrayList<>();
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(FIND_APPENDING);
            ResultSet rs = ps.executeQuery()){
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
            logger.info("[DAO_ROLE] Retrieved " + requests.size() + " pending requests from DB.");
            return requests;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DAO_ERROR] Failed to retrieve pending requests: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean approveRequest(UUID requestId, UUID ringleaderId) {
        logger.info("[DAO_ROLE] Approving Request ID: " + requestId + " by Ringleader: " + ringleaderId);
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(APPROVE)){
            ps.setString(1, ringleaderId.toString());
            ps.setString(2, requestId.toString());
            boolean success = ps.executeUpdate() > 0;
            if(success) logger.fine("[DAO_ROLE] Request " + requestId + " status updated to APPROVED.");
            return success;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DAO_ERROR] Exception during approval of request " + requestId + ": " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean rejectRequest(UUID requestId, UUID ringleaderId) {
        logger.info("[DAO_ROLE] Rejecting Request ID: " + requestId + " by Ringleader: " + ringleaderId);
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(REJECT)){
            ps.setString(1, ringleaderId.toString());
            ps.setString(2, requestId.toString());
            boolean success = ps.executeUpdate() > 0;
            if(success) logger.fine("[DAO_ROLE] Request " + requestId + " status updated to REJECTED.");
            return success;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DAO_ERROR] Exception during rejection of request " + requestId + ": " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public RoleRequest findById(UUID requestId) {
        logger.fine("[DAO_ROLE] Searching for specific request ID: " + requestId);
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ID)) {
            ps.setString(1, requestId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DAO_ERROR] Failed to find role request by ID " + requestId + ": " + e.getMessage(), e);
        }
        return null;
    }

    private RoleRequest mapRow(ResultSet rs) throws SQLException {
        UUID ringleaderId;
        UUID requestId = UUID.fromString(rs.getString("requestId"));
        UUID userId = UUID.fromString(rs.getString("userId"));
        RequestedRole requestedRole =RequestedRole.valueOf(rs.getString("requestedRole"));
        String reason = rs.getString("reason");
        Status status =Status.valueOf(rs.getString("status"));
        String ringleaderStr = rs.getString("ringleaderId");

        if (ringleaderStr != null) {
            ringleaderId = UUID.fromString(ringleaderStr);
        } else {
            ringleaderId = null;
        }
        return new RoleRequest(requestId, userId, requestedRole,reason,status, ringleaderId);
    }
}