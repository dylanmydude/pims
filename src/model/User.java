package model;

public class User {

    private int user_id;
    private String username;
    private String password;
    private String role;
    private String full_name;

    public User() {
        // Default constructor.
    }

    public User(int user_id, String username, String password, String role, String full_name) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.full_name = full_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String fullName) {
        this.full_name = fullName;
    }

    @Override
    public String toString() {
        return "User{"
                + "user_id=" + user_id
                + ", username='" + username + '\''
                + ", password='" + password + '\''
                + ", role='" + role + '\''
                + ", full_name='" + full_name + '\''
                + '}';
    }
}
