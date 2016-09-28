import gitBackuper.GitHubApiProvider;
import gitBackuper.Models.ContentFile;
import gitBackuper.Models.Repository;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GitHubApiProviderTests {
    @Test
    public void testRepositoryContents() {
        List<Repository> repositoryList = GitHubApiProvider.getRepositoriesForUser("sergeioff");
        List<ContentFile> contents = GitHubApiProvider.getRepositoryContents(repositoryList.get(0));

        Assert.assertTrue(contents.size() > 5);
    }
}
