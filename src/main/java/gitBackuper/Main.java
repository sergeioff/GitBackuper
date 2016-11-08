package gitBackuper;

import gitBackuper.api.GitHub;
import gitBackuper.api.models.Repository;
import gitBackuper.utils.BackupService;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        String token;



        System.out.println("Enter token or press Enter:");
        token = scanner.nextLine();

        if (token.length() < 40) {
            token = null;
        }

        GitHub gh = new GitHub(token);

        if (token != null) {
            System.out.printf("Hello, %s!\n", gh.getCurrentUser().getName());
        }

        Repository repo = gh.getRepository("sergeioff", "GitBackuper");

        BackupService bs = new BackupService();

        bs.makeBackupOfRepository(repo);

//        String repositoryName = "GitBackuper";
//
//        List<ContentFile> contentFiles = gh.getRepositoryContentFiles("sergeioff", repositoryName);
//
//        for (ContentFile contentFile : contentFiles) {
//            FileDownloader.downloadFile(contentFile.getDownloadUrl(), repositoryName + "/" + contentFile.getPath());
//        }

    }
}
