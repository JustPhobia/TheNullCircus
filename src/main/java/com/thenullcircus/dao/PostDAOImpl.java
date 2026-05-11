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
    private static final Logger  logger = Logger.getLogger(PostDAOImpl.class.getName());
    public static final String INSERT = "INSERT INTO posts" +
            "(postId, userId, body, comments, status, moderatedBy, timestamp) VALUES" +
            "(?, ?, ?, ?, ?, ?, ?)";
    public static final String FIND_APPROVED = "SELECT * FROM posts WHERE status = 'approved' ORDER BY timestamp DESC";
    public static final String FIND_PENDING = "SELECT * FROM posts WHERE status = 'pending'";
    public static final String FIND_BY_POST_ID = "SELECT * FROM posts WHERE postId = ?";
    public static final String FIND_BY_USER_ID = "SELECT * FROM posts WHERE userId = ?";
    public static final String JOKE_OF_THE_DAY = "SELECT * FROM posts " +
            "WHERE status = 'approved' " +
            "AND timestamp >= NOW() - INTERVAL 24 HOUR " +
            "ORDER BY (upvotes - downvotes) DESC " +
            "LIMIT 1";
    public static final String APPROVE_OR_REJECT = "UPDATE posts SET status = ?, moderatedBy = ? where postId = ?";


    @Override
    public boolean createPost(Post post) {
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, post.getPostId().toString());
            statement.setString(2, post.getUserId().toString());
            statement.setString(3, post.getBody());
            statement.setString(4, post.getComments());
            statement.setString(5, post.getStatus().toString());
            statement.setString(6, post.getModeratorId());
            statement.setTimestamp(7, Timestamp.valueOf(post.getTimestamp()));

            return statement.executeUpdate()>0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public ArrayList<Post> findAllApproved() {
        ArrayList<Post> posts = new ArrayList<>();
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_APPROVED);
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                posts.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return posts;
    }

    @Override
    public ArrayList<Post> findAllPending() {
        ArrayList<Post> posts = new ArrayList<>();
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_PENDING);
            ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                posts.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return posts;
    }

    @Override
    public Post findPostById(UUID postId) {
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_BY_POST_ID)) {
            statement.setString(1, postId.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapRow(resultSet);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Post findJokeOfTheDay() {
        try(Connection connection = DatabaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(JOKE_OF_THE_DAY)){
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapRow(resultSet);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    private Post mapRow(ResultSet resultSet) throws SQLException {
        UUID postId = UUID.fromString(resultSet.getString("postId"));
        UUID userId = UUID.fromString(resultSet.getString("userId"));
        String body = resultSet.getString("body");
        String comments = resultSet.getString("comments");
        Status status = Status.valueOf(resultSet.getString("status").toUpperCase());
        String moderatorId = resultSet.getString("moderatedBy");
        LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();
        int upvotes = resultSet.getInt("upvotes");
        int downvotes = resultSet.getInt("downvotes");

        return new Post(postId, userId, body, comments, status, moderatorId, timestamp, upvotes, downvotes);
    }

    public boolean approvePost(UUID postId, UUID moderatorId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(APPROVE_OR_REJECT)){
            statement.setString(1, Status.APPROVED.toString().toLowerCase());
            statement.setString(2, moderatorId.toString());
            statement.setString(3, postId.toString());

            return statement.executeUpdate()>0;
        }catch(SQLException e){
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    public  boolean rejectPost(UUID postId, UUID moderatorId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(APPROVE_OR_REJECT) ){
            statement.setString(1, Status.REJECTED.toString().toLowerCase());
            statement.setString(2, moderatorId.toString());
            statement.setString(3, postId.toString());
            return statement.executeUpdate()>0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public ArrayList<Post> findByUserId(UUID userId) {
        ArrayList<Post> posts = new ArrayList<>();
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID)){
            statement.setString(1, userId.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                posts.add(mapRow(resultSet));
            }
            return  posts;
        }catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }
}