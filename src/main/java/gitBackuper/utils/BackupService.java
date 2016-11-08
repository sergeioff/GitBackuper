package gitBackuper.utils;

import gitBackuper.api.models.ContentFile;
import gitBackuper.api.models.Repository;
import gitBackuper.models.Backup;
import gitBackuper.models.BackupsList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class BackupService {
    private final String PATH_TO_BACKUPS_FILE = ".backups";
    private final String DIRECTORY_FOR_BACKUPS = ".Backups";

    private BackupsList backups;

    public BackupService() {
        backups = initBackups();
    }

    private BackupsList initBackups() {
        Path path = Paths.get(PATH_TO_BACKUPS_FILE);

        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return (BackupsList) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace(System.err);
            }
        }

        return new BackupsList();
    }

    private void saveBackups() {
        Path path = Paths.get(PATH_TO_BACKUPS_FILE);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(backups);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public boolean isBackupExists(Repository repository) {
        List<Backup> existingBackups = backups.getBackups(repository.getName());

        if (existingBackups != null) {
            for (Backup backup : existingBackups) {
                if (backup.getPushedAt().equalsIgnoreCase(repository.getPushedAt())) {
                    return true;
                }
            }
        }

        return false;
    }

    public void makeBackupOfRepository(Repository repository) throws IOException {
        Path backupsDirectory = Paths.get(DIRECTORY_FOR_BACKUPS);

        if (Files.notExists(backupsDirectory)) {
            Files.createDirectory(backupsDirectory);
        }

        String repositoryName = repository.getName();

        Backup backup = new Backup(repositoryName, repository.getPushedAt());
        List<ContentFile> contentFiles = repository.getContentFiles();

        for (ContentFile contentFile : contentFiles) {
            if (backups.getBackups(repositoryName) != null) {
                File backupedFile = getBackupedFile(repositoryName, contentFile);

                if (backupedFile != null) {
                    backup.addFile(contentFile, backupedFile);
                    continue;
                }
            }

            Path filePath = Paths.get(backupsDirectory + "/" +
                    repositoryName + "/" +
                    repository.getPushedAt() + "/" +
                    contentFile.getPath());

            FileDownloader.downloadFile(contentFile.getDownloadUrl(), filePath);
            backup.addFile(contentFile, filePath.toFile());
        }

        backups.addBackup(repositoryName, backup);

        saveBackups();
    }

/*    private boolean isFileAlreadyBackuped(String repository, ContentFile contentFile) {
        List<Backup> olderBackups = backups.getBackups(repository);

        for (Backup backup : olderBackups) {
            for (ContentFile fileFromBackup : backup.getFiles().keySet()) {
                if ((contentFile.getPath().equals(fileFromBackup.getPath())) &&
                        (fileFromBackup.getSha().equals(contentFile.getSha()))) {
                    return true;
                }
            }
        }

        return false;
    }*/

    private File getBackupedFile(String repository, ContentFile contentFile) {
        List<Backup> olderBackups = backups.getBackups(repository);

        for (Backup backup : olderBackups) {
            for (ContentFile fileFromBackup : backup.getFiles().keySet()) {
                if ((contentFile.getPath().equals(fileFromBackup.getPath())) &&
                        (fileFromBackup.getSha().equals(contentFile.getSha()))) {
                    return backup.getFiles().get(fileFromBackup);
                }
            }
        }

        return null;
    }

    public BackupsList getBackups() {
        return backups;
    }
}
