package com.distributedexam.dao;

import com.distributedexam.common.User;
import com.distributedexam.common.UserRole;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UserDAOImpl implements UserDAO {
    public void create(User user, String password) throws Exception {
        if (password == null || password.length() < 8) throw new IllegalArgumentException("Password must contain at least 8 characters");
        String sql = "INSERT INTO users (username,password_hash,role,room_id) VALUES (?,?,?,?)";
        try (Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1,user.getUsername()); s.setString(2, BCrypt.hashpw(password, BCrypt.gensalt(12))); s.setString(3,user.getRole().name());
            if(user.getRoomId()==null)s.setNull(4,Types.INTEGER); else s.setInt(4,user.getRoomId()); s.executeUpdate();
            try(ResultSet k=s.getGeneratedKeys()){if(k.next()) user.setUserId(k.getInt(1));}
        }
    }
    public User findByUsername(String username) throws Exception {
        String sql="SELECT user_id,username,role,room_id,created_at FROM users WHERE username=?";
        try(Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql)){s.setString(1,username);try(ResultSet r=s.executeQuery()){return r.next()?map(r):null;}}
    }
    public boolean authenticate(String username,String password) throws Exception {
        if(username==null||password==null) return false;
        try(Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement("SELECT password_hash FROM users WHERE username=?")){s.setString(1,username);try(ResultSet r=s.executeQuery()){return r.next() && BCrypt.checkpw(password,r.getString(1));}}
    }
    private User map(ResultSet r)throws SQLException { User u=new User();u.setUserId(r.getInt("user_id"));u.setUsername(r.getString("username"));u.setRole(UserRole.valueOf(r.getString("role")));int room=r.getInt("room_id");u.setRoomId(r.wasNull()?null:room);u.setCreatedAt(r.getTimestamp("created_at"));return u; }
}
