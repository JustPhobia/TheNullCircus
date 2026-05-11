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
    private static final String GET_UPVOTED_POSTS =
            "SELECT p.* FROM posts p " +
                    "INNER JOIN votes v ON p.postId = v.postId " +
                    "WHERE v.userId = ? AND v.type = 'UPVOTE' AND p.status = 'approved'";

    // Returns true if vote was recorded or already exists, false only on failure
    @Override
    public boolean upvotePost(UUID postId, UUID userId) {
        VoteType voteType = VoteType.UPVOTE;
        VoteType existing = hasVoted(postId, userId);

        if(existing == VoteType.UPVOTE) {
            return true;
        }else  if(existing == VoteType.DOWNVOTE) {
            deleteVote(postId, userId);
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(CREATE)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, postId.toString());
            ps.setString(3, userId.toString());
            ps.setString(4, voteType.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean downvotePost(UUID postId, UUID userId) {
        VoteType voteType = VoteType.DOWNVOTE;
        VoteType existing = hasVoted(postId, userId);

        if(existing == VoteType.DOWNVOTE) {
            return true;
        }else  if(existing == VoteType.UPVOTE) {
            deleteVote(postId, userId);
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(CREATE)) {

            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, postId.toString());
            ps.setString(3, userId.toString());
            ps.setString(4, voteType.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
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

    private boolean deleteVote(UUID postId, UUID userId) {
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(DELETE)){
            ps.setString(1, postId.toString());
            ps.setString(2, userId.toString());
            return ps.executeUpdate() > 0;
        }catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
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
