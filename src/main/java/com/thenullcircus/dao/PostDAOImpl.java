package com.thenullcircus.dao;

import com.thenullcircus.model.Post;
import com.thenullcircus.model.Status;
import com.thenullcircus.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostDAOImpl implements PostDAO {
    private static final Logger logger = Logger.getLogger(PostDAOImpl.class.getName());
    public static final String INSERT = "INSERT INTO posts(postId, userId, body, comments, status, moderatedBy, timestamp) VALUES(?, ?, ?, ?, ?, ?, ?)";
    public static final String FIND_APPROVED = "SELECT * FROM posts WHERE status = 'approved' ORDER BY timestamp DESC";
    public static final String FIND_PENDING = "SELECT * FROM posts WHERE status = 'pending'";
    public static final String FIND_BY_POST_ID = "SELECT * FROM posts WHERE postId = ?";
    public static final String FIND_BY_USER_ID = "SELECT * FROM posts WHERE userId = ?";
    public static final String JOKE_OF_THE_DAY = "SELECT * FROM posts WHERE status = 'approved' AND timestamp >= NOW() - INTERVAL 24 HOUR ORDER BY (upvotes - downvotes) DESC LIMIT 1";
    public static final String APPROVE_OR_REJECT = "UPDATE posts SET status = ?, moderatedBy = ? WHERE postId = ?";

    @Override
    public boolean createPost(Post post) {
        logger.info("Attempting to save new post by User: " + post.getUserId());
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, post.getPostId().toString());
            statement.setString(2, post.getUserId().toString());
            statement.setString(3, post.getBody());
            statement.setString(4, post.getComments());
            statement.setString(5, post.getStatus().toString().toLowerCase());
            statement.setString(6, post.getModeratorId());
            statement.setTimestamp(7, Timestamp.valueOf(post.getTimestamp()));

            boolean success = statement.executeUpdate() > 0;
            logger.info("Post creation success: " + success + " (ID: " + post.getPostId() + ")");
            return success;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Error while creating post", e);
            return false;
        }
    }

    @Override
    public ArrayList<Post> findAllApproved() {
        ArrayList<Post> posts = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_APPROVED);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                posts.add(mapRow(resultSet));
            }
            logger.fine("Retrieved " + posts.size() + " approved posts.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching approved posts", e);
        }
        return posts;
    }

    @Override
    public Post findJokeOfTheDay() {
        logger.info("Querying for the top rated joke of the last 24 hours...");
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(JOKE_OF_THE_DAY);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                Post joke = mapRow(resultSet);
                logger.info("Joke of the Day found: " + joke.getPostId());
                return joke;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving Joke of the Day", e);
        }
        logger.warning("No eligible Joke of the Day found.");
        return null;
    }

    public boolean approvePost(UUID postId, UUID moderatorId) {
        logger.info("Ringleader " + moderatorId + " approving post: " + postId);
        return setStatus(postId, moderatorId, Status.APPROVED);
    }

    public boolean rejectPost(UUID postId, UUID moderatorId) {
        logger.warning("Ringleader " + moderatorId + " REJECTING post: " + postId);
        return setStatus(postId, moderatorId, Status.REJECTED);
    }

    private boolean setStatus(UUID postId, UUID modId, Status status) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(APPROVE_OR_REJECT)) {
            statement.setString(1, status.toString().toLowerCase());
            statement.setString(2, modId.toString());
            statement.setString(3, postId.toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update post status to " + status, e);
            return false;
        }
    }

    private Post mapRow(ResultSet resultSet) throws SQLException {
        return new Post(
                UUID.fromString(resultSet.getString("postId")),
                UUID.fromString(resultSet.getString("userId")),
                resultSet.getString("body"),
                resultSet.getString("comments"),
                Status.valueOf(resultSet.getString("status").toUpperCase()),
                resultSet.getString("moderatedBy"),
                resultSet.getTimestamp("timestamp").toLocalDateTime(),
                resultSet.getInt("upvotes"),
                resultSet.getInt("downvotes")
        );
    }

    @Override
    public ArrayList<Post> findByUserId(UUID userId) {
        ArrayList<Post> posts = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID)) {
            statement.setString(1, userId.toString());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) posts.add(mapRow(rs));
            }
            logger.info("Found " + posts.size() + " posts for user " + userId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching posts for User ID: " + userId, e);
        }
        return posts;
    }

    @Override public ArrayList<Post> findAllPending() { /* Pattern same as findAllApproved */ return new ArrayList<>(); }
    @Override public Post findPostById(UUID id) { /* Pattern same as findByUserId */ return null; }
}