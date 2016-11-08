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

public class BackupService {
    private final String PATH_TO_BACKUPS_FILE = ".backups";

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

    public void makeBackupOfRepository(Repository repository) {
        Backup backup = new Backup();
        List<ContentFile> contentFiles = repository.getContentFiles();
        for (ContentFile contentFile : contentFiles) {
            Path filePath = Paths.get(repository.getName() + "/" + contentFile.getPath());

            FileDownloader.downloadFile(contentFile.getDownloadUrl(), filePath);
            backup.addFile(contentFile, filePath.toFile());
        }

        backups.addBackup(repository.getName(), backup);

        saveBackups();
    }
}
