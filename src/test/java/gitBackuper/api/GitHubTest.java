package gitBackuper.api;

import gitBackuper.api.models.Repository;
import gitBackuper.api.models.User;
import gitBackuper.utils.TokenLoader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class GitHubTest {
    private static final String REPOSITORY_NAME = "GitBackuper";
    private static final String REPOSITORY_OWNER = "sergeioff";
    private static final String REPOSITORY_URL = "https://github.com/sergeioff/GitBackuper";
    private static final String TOKEN = TokenLoader.loadToken();

    private GitHub unauthorizedGitHub;
    private GitHub authorizedGitHub;

    @Before
    public void init() {
        unauthorizedGitHub = new GitHub();
        authorizedGitHub = new GitHub(TOKEN);
    }

    @Test
    public void getCurrentUser() throws IOException {
        User user = authorizedGitHub.getCurrentUser();
        assertTrue(user.getLogin().length() > 1);
    }

    @Test(expected = IllegalStateException.class)
    public void getCurrentUserWillFailTest() throws IOException {
        User user = unauthorizedGitHub.getCurrentUser();
    }

    @Test(expected = FileNotFoundException.class)
    public void getUserWillFailTest() throws IOException {
        User user = unauthorizedGitHub.getUser("NonExistingUser");
    }

    @Test
    public void getUserRepositories() throws IOException {
        List<Repository> repos = unauthorizedGitHub.getUserRepositories(REPOSITORY_OWNER);
        assertTrue(repos.size() > 0);
        assertTrue(repos.stream().filter(r -> r.getName().equals(REPOSITORY_NAME)).count() > 0);
    }

    @Test(expected = FileNotFoundException.class)
    public void getRepositoryWillFailTest() throws IOException {
        unauthorizedGitHub.getRepository("nonExistingUser", "nonExistingRepository");
    }

    @Test
    public void getRepositoryTest() {
        try {
            Repository repository = unauthorizedGitHub.getRepository(REPOSITORY_OWNER, REPOSITORY_NAME);

            assertEquals(repository.getName(), REPOSITORY_NAME);

            Repository repositoryByUrl = unauthorizedGitHub.getRepository(REPOSITORY_URL);

            assertEquals(repository.getName(), repositoryByUrl.getName());
            assertEquals(repository.getId(), repositoryByUrl.getId());
            assertEquals(repository.getFullName(), repositoryByUrl.getFullName());
            assertEquals(repository.getDescription(), repositoryByUrl.getDescription());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }


}
