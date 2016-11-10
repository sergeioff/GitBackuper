package gitBackuper.models;

import gitBackuper.api.models.ContentFile;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
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

    public void addFile(ContentFile contentFile, File file) {
        files.put(contentFile, file);
    }

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
