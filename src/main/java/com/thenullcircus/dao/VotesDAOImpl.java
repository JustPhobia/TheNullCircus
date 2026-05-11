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
    private static final String HAS_VOTED = "SELECT type FROM votes WHERE postId = ? AND userId = ?";
    private static final String DELETE = "DELETE FROM votes WHERE postId = ? AND userId = ?";
    private static final String GET_UPVOTED_POSTS = "SELECT posts.* FROM posts , votes WHERE posts.postId = votes.postId AND votes.userId = ? AND votes.type = 'UPVOTE' AND posts.status = 'approved'";

    private static final String INCREMENT_UPVOTES = "UPDATE posts SET upvotes = upvotes + 1 WHERE postId = ?";
    private static final String INCREMENT_DOWNVOTES = "UPDATE posts SET downvotes = downvotes + 1 WHERE postId = ?";
    private static final String DECREMENT_UPVOTES = "UPDATE posts SET upvotes = upvotes - 1 WHERE postId = ?";
    private static final String DECREMENT_DOWNVOTES = "UPDATE posts SET downvotes = downvotes - 1 WHERE postId = ?";

    @Override
    public boolean upvotePost(UUID postId, UUID userId) {
        return processVote(postId, userId, VoteType.UPVOTE);
    }

    @Override
    public boolean downvotePost(UUID postId, UUID userId) {
        return processVote(postId, userId, VoteType.DOWNVOTE);
    }

    private boolean processVote(UUID postId, UUID userId, VoteType vote) {
        VoteType existing = hasVoted(postId, userId);
        boolean isUpvote = (vote == VoteType.UPVOTE);

        String decTarget = isUpvote ? DECREMENT_UPVOTES : DECREMENT_DOWNVOTES;
        String incTarget = isUpvote ? INCREMENT_UPVOTES : INCREMENT_DOWNVOTES;
        String decOpp    = isUpvote ? DECREMENT_DOWNVOTES : DECREMENT_UPVOTES;

        try (Connection connection = DatabaseConnection.getConnection()) {

            if (existing == vote) {
                // Toggle off existing vote
                try (PreparedStatement psDelete = connection.prepareStatement(DELETE);
                     PreparedStatement psDec = connection.prepareStatement(decTarget)) {

                    psDelete.setString(1, postId.toString());
                    psDelete.setString(2, userId.toString());
                    int deletedRows = psDelete.executeUpdate();

                    // Strict validation: Only decrement if the row was successfully deleted
                    if (deletedRows > 0) {
                        psDec.setString(1, postId.toString());
                        psDec.executeUpdate();
                    }
                }
            } else {
                // Switch vote
                if (existing != null) {
                    try (PreparedStatement psDel = connection.prepareStatement(DELETE);
                         PreparedStatement psDec = connection.prepareStatement(decOpp)) {

                        psDel.setString(1, postId.toString());
                        psDel.setString(2, userId.toString());
                        int deletedRows = psDel.executeUpdate();

                        if (deletedRows > 0) {
                            psDec.setString(1, postId.toString());
                            psDec.executeUpdate();
                        }
                    }
                }

                // Insert new vote
                try (PreparedStatement psIns = connection.prepareStatement(CREATE);
                     PreparedStatement psInc = connection.prepareStatement(incTarget)) {

                    psIns.setString(1, UUID.randomUUID().toString());
                    psIns.setString(2, postId.toString());
                    psIns.setString(3, userId.toString());
                    psIns.setString(4, vote.name());
                    psIns.executeUpdate();

                    psInc.setString(1, postId.toString());
                    psInc.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to process vote for Post ID: " + postId, e);
            return false;
        }
    }

    @Override
    public VoteType hasVoted(UUID postId, UUID userId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(HAS_VOTED)) {
            ps.setString(1, postId.toString());
            ps.setString(2, userId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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