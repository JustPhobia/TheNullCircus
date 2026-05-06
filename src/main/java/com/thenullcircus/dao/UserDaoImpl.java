package com.thenullcircus.dao;

import com.thenullcircus.model.Gender;
import com.thenullcircus.model.User;
import com.thenullcircus.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDaoImpl implements UserDao{

    @Override
    public boolean registerUser(User user) {
        try(Connection conn = DatabaseConnection.getConnection()){
            String query = "INSERT INTO users" +
                    "(userId, name, surname, email, gender, username, password, clown, ringleader)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            if(user.getUserId() == null){
                user.setUserId(UUID.randomUUID());
            }

            ps.setString(1, user.getUserId().toString());
            ps.setString(2, user.getName());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getGender().toString());
            ps.setString(6, user.getUsername());
            ps.setString(7, user.getPassword());
            ps.setString(8, user.getClown().toString());
            ps.setString(9, user.getRingleader().toString());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getByUserUsername(String username) {
        try(Connection conn = DatabaseConnection.getConnection()){

            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return new User(
                        UUID.fromString(rs.getString("userId")),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        Gender.valueOf(rs.getString("gender")),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBoolean("clown"),
                        rs.getBoolean("ringleader")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean updateRole(UUID userId, String newRole) {
        try(Connection conn = DatabaseConnection.getConnection()){
            String query = "UPDATE users SET clown = ?, ringleader = ? WHERE userId = ?";

            PreparedStatement ps = conn.prepareStatement(query);
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
            throw new RuntimeException(e);
        }
    }
}
