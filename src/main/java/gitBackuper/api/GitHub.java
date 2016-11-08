package gitBackuper.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gitBackuper.api.models.ContentFile;
import gitBackuper.api.models.Repository;
import gitBackuper.api.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GitHub {
    private final String API_ROOT = "https://api.github.com/";
    private final String USER_PATTERN = API_ROOT + "users/%s";
    private final String REPOS_FOR_USER_PATTERN = USER_PATTERN + "/repos";
    private final String REPOSITORY_PATTERN = API_ROOT + "repos/%s/%s";
    private final String CONTENTS_PATTERN = REPOSITORY_PATTERN + "/contents/";
    private final String REQUEST_WITH_ACCESS_TOKEN = "%s?access_token=%s";
    private final String CURRENT_USER = API_ROOT + "user";

    ObjectMapper mapper;

    private String token = null;

    public GitHub() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public GitHub(String token) {
        this();
        this.token = token;
    }

    public User getCurrentUser() throws IOException {
        if (token == null) {
            throw new IllegalStateException();
        }

        String url = getLinkForRequestWitchToken(CURRENT_USER, token);

        return mapper.readValue(new URL(url), User.class);
    }

    public String getLinkForUser(String username) {
        String link = String.format(USER_PATTERN, username);
        if (null == token) {
            return link;
        }

        return getLinkForRequestWitchToken(link, token);
    }

    public String getLinkForUserRepos(String username) {
        String link = String.format(REPOS_FOR_USER_PATTERN, username);

        if (null == token) {
            return link;
        }

        return getLinkForRequestWitchToken(link, token);
    }

    public String getLinkForRepository(String user, String repository) {
        String link = String.format(REPOSITORY_PATTERN, user, repository);

        if (null == token) {
            return link;
        }

        return getLinkForRequestWitchToken(link, token);
    }

    public String getLinkForRepositoryContents(String user, String repository) {
        String link = String.format(CONTENTS_PATTERN, user, repository);

        if (null == token) {
            return link;
        }

        return getLinkForRequestWitchToken(link, token);
    }

    public String getLinkForRequestWitchToken(String request, String token) {
        return String.format(REQUEST_WITH_ACCESS_TOKEN, request, token);
    }

    public List<ContentFile> getRepositoryContentFiles(String user, String repository) throws IOException {
        String linkForContents = getLinkForRepositoryContents(user, repository);

        return getDirectoryContentFiles(linkForContents);
    }

    public List<ContentFile> getDirectoryContentFiles(String url) throws IOException {
        List<ContentFile> filesInDirectory = mapper.readValue(new URL(url),
                mapper.getTypeFactory().constructCollectionType(List.class, ContentFile.class));

        List<ContentFile> directoriesToAdd = new ArrayList<>();

        Iterator<ContentFile> iterator = filesInDirectory.iterator();

        while (iterator.hasNext()) {
            ContentFile currentElement = iterator.next();
            if (currentElement.getType().equalsIgnoreCase("dir")) {
                directoriesToAdd.add(currentElement);
                iterator.remove();
            }
        }

        for (ContentFile dir : directoriesToAdd) {
            filesInDirectory.addAll(getDirectoryContentFiles(dir.getUrl()));
        }

        return filesInDirectory;
    }

    public User getUser(String username) throws IOException {
        String linkForUser = getLinkForUser(username);
        return mapper.readValue(new URL(linkForUser), User.class);
    }

    public Repository getRepository(String user, String repository) throws IOException {
        String linkForRepository = getLinkForRepository(user, repository);
        Repository repo = mapper.readValue(new URL(linkForRepository), Repository.class);
        repo.setContentFiles(getRepositoryContentFiles(user, repository));

        return repo;
    }
}
