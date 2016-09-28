package gitBackuper.Models;

public class ContentFile {
    private String name;
    private String path;
    private String url;
    private String downloadUrl;
    private String type;

    public ContentFile(String name, String path, String url, String downloadUrl, String type) {
        this.name = name;
        this.path = path;
        this.url = url;
        this.downloadUrl = downloadUrl;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getType() {
        return type;
    }

    public boolean isDirectory() {
        return "dir".equals(type);
    }
}
