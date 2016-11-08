package gitBackuper.models;

import gitBackuper.api.models.ContentFile;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Backup implements Serializable {
    private long id;
    private Map<ContentFile, File> files;
    private String pushedAt;

    public Backup() {
        files = new HashMap<>();
    }

    public void addFile(ContentFile contentFile, File file) {
        files.put(contentFile, file);
    }

    public long getId() {
        return id;
    }

    public Map<ContentFile, File> getFiles() {
        return files;
    }

    public String getPushedAt() {
        return pushedAt;
    }
}
