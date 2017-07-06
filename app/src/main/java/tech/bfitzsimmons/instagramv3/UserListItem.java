package tech.bfitzsimmons.instagramv3;

/**
 * Created by Brian on 7/4/2017.
 */

public class UserListItem {
    private String username, createdAt;

    public UserListItem(String username, String createdAt) {
        this.username = username;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
