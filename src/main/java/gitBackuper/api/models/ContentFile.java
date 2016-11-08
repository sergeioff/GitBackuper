package gitBackuper.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Content file model. Represents a file from GitHub repository.
 */
public class ContentFile implements Serializable {
    @JsonProperty
    private String path;

    @JsonProperty
    private String sha;

    @JsonProperty
    private String url;

    @JsonProperty("download_url")
    private String downloadUrl;

    @JsonProperty
    private String type;

    public String getPath() {
        return path;
    }

    public String getSha() {
        return sha;
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

    @Override
    public String toString() {
        return "ContentFile {\n" +
                "\tpath: " + path + ",\n" +
                "\tsha: " + sha + ",\n" +
                "\turl: " + url + ",\n" +
                "\tdownloadUrl: " + downloadUrl + ",\n" +
                "\ttype: " + type +
                "\n}";
    }
}
