package gitBackuper.api;

public class GitHub {
    private final String API_ROOT = "https://api.github.com/";
    private final String USER_PATTERN = API_ROOT + "users/%s";
    private final String REPOS_FOR_USER_PATTERN = USER_PATTERN + "/repos";
    private final String REPOSITORY_PATTERN = API_ROOT + "repos/%s/%s";
    private final String CONTENTS_PATTERN = REPOSITORY_PATTERN + "/contents/";
    private final String REQUEST_WITH_ACCESS_TOKEN = "%s?access_token=%s";

    private String TOKEN = null;

    public GitHub() {}

    public GitHub(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public String getLinkForUser(String username) {
        String link = String.format(USER_PATTERN, username);
        if (null == TOKEN) {
            return link;
        }

        return getLinkForRequestWitchToken(link, TOKEN);
    }

    public String getLinkForUserRepos(String username) {
        String link = String.format(REPOS_FOR_USER_PATTERN, username);

        if (null == TOKEN) {
            return link;
        }

        return getLinkForRequestWitchToken(link, TOKEN);
    }

    public String getLinkForRepository(String user, String repository) {
        String link = String.format(REPOSITORY_PATTERN, user, repository);

        if (null == TOKEN) {
            return link;
        }

        return getLinkForRequestWitchToken(link, TOKEN);
    }

    public String getLinkForRepositoryContents(String user, String repository) {
        String link = String.format(CONTENTS_PATTERN, user, repository);

        if (null == TOKEN) {
            return link;
        }

        return getLinkForRequestWitchToken(link, TOKEN);
    }

    public String getLinkForRequestWitchToken(String request, String token) {
        return String.format(REQUEST_WITH_ACCESS_TOKEN, request, token);
    }
}
