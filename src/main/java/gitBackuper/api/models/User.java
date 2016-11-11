package gitBackuper.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a GitHub user.
 */
@SuppressWarnings("UnusedDeclaration")
public class User {
    @JsonProperty
    private String login;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty
    private String name;

    public String getLogin() {
        return login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User {\n" +
                "\tlogin: " + login + "\n" +
                "\tavatarUrl: " + avatarUrl + "\n" +
                "\tname: " + name +
                "\n}";
    }
}
