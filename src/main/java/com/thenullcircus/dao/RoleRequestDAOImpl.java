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
    private static final String SUBMIT = "INSERT INTO role_request(requestId, userId, requestedRole, reason) VALUES (?, ?, ?, ?)";
    private static final String FIND_APPENDING = "SELECT * FROM role_request WHERE status = 'PENDING'";
    private static final String APPROVE = "UPDATE role_request SET status = 'APPROVED', ringleaderId =? WHERE requestId = ?";
    private static final String REJECT = "UPDATE role_request SET status = 'REJECTED', ringleaderId =? WHERE requestId = ?";

    @Override
    public boolean submitRequest(RoleRequest request) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SUBMIT)){
            ps.setString(1, request.getRequestId().toString());
            ps.setString(2, request.getUserId().toString());
            ps.setString(3, request.getRequestedRole().toString());
            ps.setString(4, request.getReason());
            return ps.executeUpdate()>0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    // Returns null on failure, empty list if no pending requests
    @Override
    public ArrayList<RoleRequest> findAllPending() {
        ArrayList<RoleRequest> requests = new ArrayList<>();
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(FIND_APPENDING);
        ResultSet rs = ps.executeQuery()){
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean approveRequest(UUID requestId, UUID ringleaderId) {
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(APPROVE)){
            ps.setString(1, ringleaderId.toString());
            ps.setString(2, requestId.toString());
            return ps.executeUpdate()>0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean rejectRequest(UUID requestId, UUID ringleaderId) {
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(REJECT)){
            ps.setString(1, ringleaderId.toString());
            ps.setString(2, requestId.toString());
            return ps.executeUpdate()>0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
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
