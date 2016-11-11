package gitBackuper.models;

import gitBackuper.api.models.ContentFile;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a backup contains content files from GitHub repository
 */
public class Backup implements Serializable {
    /**
     * Content file -> local file on disk
     */
    private Map<ContentFile, File> files;
    private String repositoryName;
    private String pushedAt;

    public Backup(String repositoryName, String pushedAt) {
        files = new HashMap<>();
        this.repositoryName = repositoryName;
        this.pushedAt = pushedAt;
    }

    /**
     * Adds association content file -> file on disk to backup
     * @param contentFile content file
     * @param file file on disk
     */
    public void addFile(ContentFile contentFile, File file) {
        files.put(contentFile, file);
    }

    /**
     * Getter for backup files
     * @return content files of backup associated to files on disk
     */
    public Map<ContentFile, File> getFiles() {
        return files;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getPushedAt() {
        return pushedAt;
    }

    @Override
    public String toString() {
        return "Backup {\n\tpushedAt: " + pushedAt + "\n}";
    }
}
