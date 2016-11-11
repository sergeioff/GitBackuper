package gitBackuper.utils;

import gitBackuper.api.models.ContentFile;
import gitBackuper.api.models.Repository;
import gitBackuper.models.Backup;
import gitBackuper.models.BackupsList;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service that provide operations on backups
 */
public class BackupService {
    private final String PATH_TO_BACKUPS_FILE = ".backups";
    private final String DIRECTORY_FOR_BACKUPS = ".Backups";
    private final String DIRECTORY_FOR_ASSEMBLED_BACKUPS = "Backups";

    private BackupsList backups;

    public BackupService() {
        backups = initBackups();
    }

    /**
     * Checks if the latest backup of repository is already exist
     * @param repository repository
     * @return true if exists, or false if not
     */
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

    /**
     * Makes backup of repository. Downloads files from remote repository to disk
     * @param repository repository
     * @throws IOException IOException while downloading files
     */
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

    /**
     * Removes all backups of repository from disk and list of backups.
     * @param repositoryName repository name
     * @throws IOException IOException while removing backups
     */
    public void removeRepositoryBackups(String repositoryName) throws IOException {
        removeDirectory(Paths.get(DIRECTORY_FOR_BACKUPS + "/" + repositoryName));
        backups.removeBackups(repositoryName);
        saveBackups();
    }

    /**
     * Removes all backups of all repositories
     * @throws IOException IOException while deleting files
     */
    public void removeAllBackups() throws IOException {
        backups.getBackups().clear();
        try {
            removeDirectory(Paths.get(DIRECTORY_FOR_BACKUPS));
            removeDirectory(Paths.get(DIRECTORY_FOR_ASSEMBLED_BACKUPS));
            Files.delete(Paths.get(PATH_TO_BACKUPS_FILE));
        } catch (NoSuchFileException e) {
            //Do nothing
        }
    }

    /**
     * Assembles backup files on disk from backup
     * @param backup backup
     * @throws IOException IOException while copying files
     */
    public void assemblyBackup(Backup backup) throws IOException {
        Path destinationDirectory = Paths.get(DIRECTORY_FOR_ASSEMBLED_BACKUPS + "/" +
                backup.getRepositoryName() + "/");

        removeDirectory(destinationDirectory);
        Files.createDirectories(destinationDirectory);

        Pattern pattern = Pattern.compile(DIRECTORY_FOR_BACKUPS + '/' + backup.getRepositoryName() + '/' +
            backup.getPushedAt() + '/' + "(.*)");

        for (File file : backup.getFiles().values()) {
            Path source = file.toPath();
            Matcher matcher = pattern.matcher(file.getPath());

            if (!matcher.find()) {
                throw new IllegalStateException();
            }

            String filePath = matcher.group(1);
            Path destination = Paths.get(destinationDirectory.toString(), filePath);
            Path destinationFolder = destination.getParent();

            if (Files.notExists(destinationFolder)) {
                Files.createDirectories(destinationFolder);
            }

            Files.copy(source, destination);
        }
    }

    /**
     * Removes directory on disk
     * @param directory directory to remove
     * @throws IOException IOException while removing files
     */
    private void removeDirectory(Path directory) throws IOException {
        if (Files.notExists(directory)) {
            return;
        }

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Gets associated file on disk for content file from repository
     * @param repository repository name
     * @param contentFile content file
     * @return associated file
     */
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

    /**
     * @return list of backups
     */
    public BackupsList getBackups() {
        return backups;
    }

    /**
     * Deserializes list of existing backups from file on disk or if file not exists returns empty backup list
     * @return backup list
     */
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

    /**
     * Serializes list of backups to file on disk
     */
    private void saveBackups() {
        Path path = Paths.get(PATH_TO_BACKUPS_FILE);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(backups);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
