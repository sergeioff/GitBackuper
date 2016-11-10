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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides access to GitHub API.
 */
public class GitHub {
    private ObjectMapper mapper;
    private UrlCreationHelper urlCreationHelper;

    private String token;

    public GitHub() {
        mapper = new ObjectMapper();
        urlCreationHelper = new UrlCreationHelper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public GitHub(String token) {
        this();
        this.token = token;
    }

    /**
     * Get current logged user (via token)
     * @return current logged user
     * @throws IOException if there are problems with url connection
     */
    public User getCurrentUser() throws IOException {
        if (token == null) {
            throw new IllegalStateException();
        }

        String url = urlCreationHelper.getLinkForCurrentUser();

        return mapper.readValue(new URL(url), User.class);
    }

    /**
     * Get user from GitHub by username
     * @param username username
     * @return user
     * @throws IOException if there are problems with url connection
     */
    public User getUser(String username) throws IOException {
        String linkForUser = urlCreationHelper.getLinkForUser(username);
        return mapper.readValue(new URL(linkForUser), User.class);
    }

    /**
     * Get list of user repositories
     * @param username username
     * @return list of repositories
     * @throws IOException if there are problems with url connection
     */
    public List<Repository> getUserRepositories(String username) throws IOException {
        String linkForRepositories = urlCreationHelper.getLinkForUserRepos(username);
        return mapper.readValue(new URL(linkForRepositories), mapper.getTypeFactory()
                .constructCollectionType(List.class, Repository.class));
    }

    /**
     * Get repository from GitHub by username and repository name
     * @param user repository owner
     * @param repository repository name
     * @return repository
     * @throws IOException if there are problems with url connection
     */
    public Repository getRepository(String user, String repository) throws IOException {
        String linkForRepository = urlCreationHelper.getLinkForRepository(user, repository);
        Repository repo = mapper.readValue(new URL(linkForRepository), Repository.class);
        repo.setContentFiles(getRepositoryContentFiles(user, repository));

        return repo;
    }

    /**
     * Get repository from GitHub by url
     * @param url repository url
     * @return repository
     * @throws IOException if there are problems with url connection
     */
    public Repository getRepository(String url) throws IOException {
        UrlCreationHelper.RepositoryAndOwner parsingResult = urlCreationHelper.getRepositoryAndOwner(url);

        return getRepository(parsingResult.ownerName, parsingResult.repositoryName);
    }

    /**
     * Get content files for repository
     * @param user repository owner username
     * @param repository repository name
     * @return content files
     * @throws IOException if there are problems with url connection
     */
    private List<ContentFile> getRepositoryContentFiles(String user, String repository) throws IOException {
        String linkForContents = urlCreationHelper.getLinkForRepositoryContents(user, repository);

        return getDirectoryContentFiles(linkForContents);
    }

    /**
     * Get content files from specified directory
     * @param url url to directory
     * @return content files from directory
     * @throws IOException if there are problems with url connection
     */
    private List<ContentFile> getDirectoryContentFiles(String url) throws IOException {
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

    /**
     * Used to creating links to GitHub API
     */
    private class UrlCreationHelper {
        private final String API_ROOT = "https://api.github.com/";
        private final String USER_PATTERN = API_ROOT + "users/%s";
        private final String REPOS_FOR_USER_PATTERN = USER_PATTERN + "/repos";
        private final String REPOSITORY_PATTERN = API_ROOT + "repos/%s/%s";
        private final String CONTENTS_PATTERN = REPOSITORY_PATTERN + "/contents/";
        private final String REQUEST_WITH_ACCESS_TOKEN = "%s?access_token=%s";
        private final String CURRENT_USER = API_ROOT + "user";

        /**
         * Makes url to request user via GitHub API
         * @param username username
         * @return url for request
         */
        private String getLinkForUser(String username) {
            String link = String.format(USER_PATTERN, username);
            if (null == token) {
                return link;
            }

            return getLinkForRequestWitchToken(link, token);
        }

        /**
         * Makes url to request current logged user via GitHub API
         * @return url for request
         */
        private String getLinkForCurrentUser() {
            if (null == token) {
                throw new IllegalStateException();
            }

            return getLinkForRequestWitchToken(CURRENT_USER, token);
        }

        /**
         * Makes url to request user repositories via GitHub API
         * @param username username
         * @return url for request
         */
        private String getLinkForUserRepos(String username) {
            String link = String.format(REPOS_FOR_USER_PATTERN, username);

            if (null == token) {
                return link;
            }

            return getLinkForRequestWitchToken(link, token);
        }

        /**
         * Makes url to request repository via GitHub API
         * @param user repository owner
         * @param repository repository name
         * @return url for request
         */
        private String getLinkForRepository(String user, String repository) {
            String link = String.format(REPOSITORY_PATTERN, user, repository);

            if (null == token) {
                return link;
            }

            return getLinkForRequestWitchToken(link, token);
        }

        /**
         * Makes url to request repository contents via GitHub API
         * @param user repository owner
         * @param repository repository name
         * @return url for request
         */
        private String getLinkForRepositoryContents(String user, String repository) {
            String link = String.format(CONTENTS_PATTERN, user, repository);

            if (null == token) {
                return link;
            }

            return getLinkForRequestWitchToken(link, token);
        }

        /**
         * Adds OAuth token to request
         * @param request initial request
         * @param token OAuth token
         * @return authorized url
         */
        private String getLinkForRequestWitchToken(String request, String token) {
            return String.format(REQUEST_WITH_ACCESS_TOKEN, request, token);
        }

        /**
         * Parses username and repository name from url
         * @param url url to parse
         * @return result of parsing
         */
        private RepositoryAndOwner getRepositoryAndOwner(String url) {
            Pattern pattern = Pattern.compile("github.com/([a-zA-Z0-9-]+)/([a-zA-Z0-9-]+)");
            Matcher matcher = pattern.matcher(url);

            String username;
            String repository;

            if (matcher.find()) {
                username = matcher.group(1);
                repository = matcher.group(2);
            } else {
                throw new IllegalArgumentException();
            }

            return new RepositoryAndOwner(repository, username);
        }

        /**
         * Used for parsing urls to username and repository
         */
        private class RepositoryAndOwner {
            String repositoryName;
            String ownerName;

            RepositoryAndOwner(String repositoryName, String ownerName) {
                this.repositoryName = repositoryName;
                this.ownerName = ownerName;
            }
        }
    }
}
