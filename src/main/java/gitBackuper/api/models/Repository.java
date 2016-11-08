package gitBackuper.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Repository {

    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty
    private String description;

    @JsonProperty("contents_url")
    private String contentsUrl;

    @JsonProperty("pushed_at")
    private String pushedAt;

    private List<ContentFile> contentFiles;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public String getContentsUrl() {
        return contentsUrl;
    }

    public String getPushedAt() {
        return pushedAt;
    }

    public List<ContentFile> getContentFiles() {
        return contentFiles;
    }

    public void setContentFiles(List<ContentFile> contentFiles) {
        this.contentFiles = contentFiles;
    }

    @Override
    public String toString() {
        return "Repository {\n\t" + "id: " + id + ",\n" +
                "\tname: " + name + ",\n" +
                "\tfullName: " + fullName + ",\n" +
                "\tdescription: " + description + ",\n" +
                "\tcontentsUrl: " + contentsUrl + ",\n" +
                "\tpushedAt: " + pushedAt + ",\n" +
                "\tcontentFiles: " + contentFiles +
                "\n}";
    }
}
