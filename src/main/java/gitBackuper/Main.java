package gitBackuper;

import gitBackuper.Models.Repository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = reader.next();

        List<Repository> repositoryList = GitHubApiProvider.getRepositoriesForUser(username);

        System.out.println("Available repositories:");

        int i;
        for (i = 0; i < repositoryList.size(); i++) {
            System.out.printf("%d) %s\n", i, repositoryList.get(i).getName());
        }

        i--;

        System.out.printf("Select repository to clone (%d-%d): ", 0, i);
        int repoIdx = reader.nextInt();

        System.out.println("Cloning...");

        Repository selectedRepository = repositoryList.get(repoIdx);
        GitHubApiProvider.downloadRepositoryFiles(selectedRepository);
        System.out.println("Done.");
    }
}
