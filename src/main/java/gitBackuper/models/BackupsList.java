package gitBackuper.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents list of existing backups
 */
public class BackupsList implements Serializable {
    /**
     * Repository -> backups of repository
     */
    private Map<String, List<Backup>> backups;

    public BackupsList() {
        backups = new HashMap<>();
    }

    /**
     * Adds a new backup for repository
     * @param repositoryName repository name
     * @param backup backup
     */
    public void addBackup(String repositoryName, Backup backup) {
        List<Backup> repositoryBackups = backups.get(repositoryName);

        if (repositoryBackups == null) {
            repositoryBackups = new ArrayList<>();
            backups.put(repositoryName, repositoryBackups);
        }

        repositoryBackups.add(backup);
    }

    /**
     * Removes all backups for repository
     * @param repositoryName repository name
     */
    public void removeBackups(String repositoryName) {
        backups.remove(repositoryName);
    }

    /**
     * Gets all backups for repository
     * @param repositoryName repository name
     * @return backups of repository
     */
    public List<Backup> getBackups(String repositoryName) {
        return backups.get(repositoryName);
    }

    /**
     * Gets all backups for all repositories
     * @return map of Repository -> Backup
     */
    public Map<String, List<Backup>> getBackups() {
        return backups;
    }

    @Override
    public String toString() {
        return backups.values().toString();
    }
}
