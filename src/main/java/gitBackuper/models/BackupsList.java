package gitBackuper.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackupsList implements Serializable {
    private Map<String, List<Backup>> backups;

    public BackupsList() {
        backups = new HashMap<>();
    }

    public BackupsList(Map<String, List<Backup>> backups) {
        this.backups = backups;
    }

    public void addBackup(String repositoryName, Backup backup) {
        List<Backup> repositoryBackups = backups.get(repositoryName);

        if (repositoryBackups == null) {
            repositoryBackups = new ArrayList<>();
            backups.put(repositoryName, repositoryBackups);
        }

        repositoryBackups.add(backup);
    }

    public void removeBackups(String repositoryName) {
        backups.remove(repositoryName);
    }

    public List<Backup> getBackups(String repositoryName) {
        return backups.get(repositoryName);
    }
}
