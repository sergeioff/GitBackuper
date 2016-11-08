package gitBackuper.api;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GitHubTest {
    private GitHub gitHubApi;

    @Before
    public void init() {
        gitHubApi = new GitHub();
    }

    @Test
    public void userLinkTest() {
        assertEquals("https://api.github.com/users/sergeioff", gitHubApi.getLinkForUser("sergeioff"));
    }

    @Test
    public void linkForUserReposTest() {
        assertEquals("https://api.github.com/users/sergeioff/repos", gitHubApi.getLinkForUserRepos("sergeioff"));
    }

    @Test
    public void linkForRepositoryByUrlTest() {
        assertEquals("https://api.github.com/repos/sergeioff/GitBackuper",
                gitHubApi.getLinkForRepository("https://github.com/sergeioff/GitBackuper/tree/master/src"));
    }

    @Test
    public void linkForRepositoryTest() {
        assertEquals("https://api.github.com/repos/sergeioff/GitBackuper",
                gitHubApi.getLinkForRepository("sergeioff", "GitBackuper"));
    }

    @Test
    public void linkForRepositoryContentsTest() {
        assertEquals("https://api.github.com/repos/sergeioff/GitBackuper/contents/",
                gitHubApi.getLinkForRepositoryContents("sergeioff", "GitBackuper"));
    }

    @Test
    public void linkForRequestWithToken() {
        assertEquals("https://api.github.com/user?access_token=0000access0000",
                gitHubApi.getLinkForRequestWitchToken("https://api.github.com/user", "0000access0000"));
    }
}
