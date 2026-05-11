package com.thenullcircus.dao;

import com.thenullcircus.model.Gender;
import com.thenullcircus.model.User;
import com.thenullcircus.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoImpl implements UserDao{
    private static final Logger logger = Logger.getLogger(UserDaoImpl.class.getName());
    private static final String REGISTER = "INSERT INTO users " +
            "(userId, name, surname, email, gender, username, password, clown, ringleader) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    private static final String UPDATE = "UPDATE users SET clown = ?, ringleader = ? WHERE userId = ?";
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE userId = ?";

    @Override
    public boolean registerUser(User user) {

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(REGISTER)){

            if(user.getUserId() == null){
                user.setUserId(UUID.randomUUID());
            }

            ps.setString(1, user.getUserId().toString());
            ps.setString(2, user.getName());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getGender().toString().toLowerCase());
            ps.setString(6, user.getUsername());
            ps.setString(7, user.getPassword());
            ps.setBoolean(8, user.getClown());
            ps.setBoolean(9, user.getRingleader());

            System.out.println("Attempting DB insert for user: " + user.getUsername());

            int rows = ps.executeUpdate();

            System.out.println("Rows inserted: " + rows);

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error (register): " + e.getMessage());
            return false;
        }
    }

    @Override
    public User findByUsername(String username) {

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(FIND_BY_USERNAME)){


            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new User(
                            UUID.fromString(rs.getString("userId")),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            Gender.valueOf(rs.getString("gender").toUpperCase()),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBoolean("clown"),
                            rs.getBoolean("ringleader")
                    );
            }

            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean updateRole(UUID userId, String newRole) {
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(UPDATE)){

            boolean isClown = false;
            boolean isRingleader = false;

            switch (newRole.toUpperCase()) {
                case "CLOWN":
                    isClown = true;
                    break;

                case "RINGLEADER":
                    isRingleader = true;
                    break;

                case "USER":
                default:
                    break;
            }

            ps.setBoolean(1, isClown);
            ps.setBoolean(2, isRingleader);
            ps.setString(3, userId.toString());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            return false;
    }

    @Override
    public boolean updateProfile(UUID userId, String field, String value) {
        String column;
        switch (field) {
            case "Username" -> column = "username";
            case "Email" -> column = "email";
            case "Name" -> column = "name";
            case "Surname" -> column = "surname";
            default -> {return false;}
        }

        String sql = "UPDATE users SET " + column + " = ? WHERE userId = ?";

        try(Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setString(2, userId.toString());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return false;

    }

    @Override
    public User findById(UUID userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setString(1, userId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            UUID.fromString(rs.getString("userId")),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            Gender.valueOf(rs.getString("gender").toUpperCase()),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBoolean("clown"),
                            rs.getBoolean("ringleader")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }
}
