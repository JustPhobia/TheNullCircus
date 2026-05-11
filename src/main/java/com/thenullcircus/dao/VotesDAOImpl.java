package com.thenullcircus.dao;

import com.thenullcircus.model.Post;
import com.thenullcircus.model.Status;
import com.thenullcircus.model.VoteType;
import com.thenullcircus.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VotesDAOImpl implements VotesDAO {
    private static final Logger log = Logger.getLogger(VotesDAOImpl.class.getName());
    private static final String CREATE = "INSERT INTO votes(voteId, postId, userId, type) VALUES (?, ?, ?, ?)";
    private static final String HAS_VOTED = "SELECT type FROM votes WHERE postId = ? AND  userId = ?";
    private static final String DELETE = "DELETE FROM votes WHERE postId = ? AND  userId = ?";
    private static final String GET_UPVOTED_POSTS = "SELECT p.* FROM posts p " +
                    "INNER JOIN votes v ON p.postId = v.postId " +
                    "WHERE v.userId = ? AND v.type = 'UPVOTE' AND p.status = 'approved'";
    private static final String INCREMENT_UPVOTES   = "UPDATE posts SET upvotes = upvotes + 1 WHERE postId = ?";
    private static final String INCREMENT_DOWNVOTES = "UPDATE posts SET downvotes = downvotes + 1 WHERE postId = ?";
    private static final String DECREMENT_UPVOTES   = "UPDATE posts SET upvotes = upvotes - 1 WHERE postId = ?";
    private static final String DECREMENT_DOWNVOTES = "UPDATE posts SET downvotes = downvotes - 1 WHERE postId = ?";

    // VotesDAOImpl.java

    @Override
    public boolean upvotePost(UUID postId, UUID userId) {
        VoteType existing = hasVoted(postId, userId);
        if (existing == VoteType.UPVOTE) return true;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (existing == VoteType.DOWNVOTE) {
                    try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                        ps.setString(1, postId.toString());
                        ps.setString(2, userId.toString());
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement(DECREMENT_DOWNVOTES)) {
                        ps.setString(1, postId.toString());
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(CREATE)) {
                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setString(2, postId.toString());
                    ps.setString(3, userId.toString());
                    ps.setString(4, VoteType.UPVOTE.name());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(INCREMENT_UPVOTES)) {
                    ps.setString(1, postId.toString());
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, e.getMessage(), e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean downvotePost(UUID postId, UUID userId) {
        VoteType existing = hasVoted(postId, userId);
        if (existing == VoteType.DOWNVOTE) return true;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (existing == VoteType.UPVOTE) {
                    try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                        ps.setString(1, postId.toString());
                        ps.setString(2, userId.toString());
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement(DECREMENT_UPVOTES)) {
                        ps.setString(1, postId.toString());
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(CREATE)) {
                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setString(2, postId.toString());
                    ps.setString(3, userId.toString());
                    ps.setString(4, VoteType.DOWNVOTE.name());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(INCREMENT_DOWNVOTES)) {
                    ps.setString(1, postId.toString());
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, e.getMessage(), e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public VoteType hasVoted(UUID postId, UUID userId) {
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(HAS_VOTED)){
            ps.setString(1, postId.toString());
            ps.setString(2, userId.toString());

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return VoteType.valueOf(rs.getString("type"));
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public ArrayList<Post> getUpvotedPostsByUser(UUID userId) {
        ArrayList<Post> posts = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_UPVOTED_POSTS)) {

            ps.setString(1, userId.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(
                            UUID.fromString(rs.getString("postId")),
                            UUID.fromString(rs.getString("userId")),
                            rs.getString("body"),
                            rs.getString("comments"),
                            Status.valueOf(rs.getString("status").toUpperCase()),
                            rs.getString("moderatedBy"),
                            rs.getTimestamp("timestamp").toLocalDateTime(),
                            rs.getInt("upvotes"),
                            rs.getInt("downvotes")
                    ));
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return posts;
    }
}
