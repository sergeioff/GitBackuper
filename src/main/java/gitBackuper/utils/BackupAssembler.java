package gitBackuper.utils;

import gitBackuper.models.Backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class BackupAssembler {
    private static final String DIRECTORY_TO_BACKUPS = "Backups";

    public static void assemblyBackup(Backup backup) throws IOException {
        Path destinationDirectory = Paths.get(DIRECTORY_TO_BACKUPS + "/" +
                backup.getRepositoryName() + "/");

        deleteDirectory(destinationDirectory);
        Files.createDirectories(destinationDirectory);


        for (File file : backup.getFiles().values()) {
            Path source = file.toPath();
            Path destination = Paths.get(destinationDirectory.toString(), file.getName());



            Files.copy(source, destination);
        }
    }

    private static void deleteDirectory(Path directory) throws IOException {
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
}
