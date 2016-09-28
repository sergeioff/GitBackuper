package gitBackuper;

import gitBackuper.Models.ContentFile;
import gitBackuper.Models.Repository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class GitHubApiProvider {
    private static final String API_URL = "https://api.github.com";

    public static List<Repository> getRepositoriesForUser(String username) {
            String url = API_URL + "/users/" + username + "/repos";
            JSONArray jsonRepositories = new JSONArray(UrlLoader.getContentFromUrl(url));

            ArrayList<Repository> repositories = new ArrayList<>();

            for (Object jsonRepoObj : jsonRepositories) {
                JSONObject jsonRepository = (JSONObject) jsonRepoObj;
                String name = jsonRepository.getString("name");
                String fullName = jsonRepository.getString("full_name");

                repositories.add(new Repository(name, fullName));
            }

            return repositories;
    }

    public static List<ContentFile> getContentsByUrl(String url) {
        String json = UrlLoader.getContentFromUrl(url);

        JSONArray jsonContentsArray = new JSONArray(json);

        ArrayList<ContentFile> contentFiles = new ArrayList<>();

        for (Object jsonContentFileObj : jsonContentsArray) {
            JSONObject jsonContentFile = (JSONObject) jsonContentFileObj;

            String name = jsonContentFile.getString("name");
            String path = jsonContentFile.getString("path");
            String contentUrl = jsonContentFile.getString("url");
            String type = jsonContentFile.getString("type");
            String downloadUrl;

            try {
                downloadUrl = jsonContentFile.getString("download_url");
            } catch (JSONException e) {
                downloadUrl = "null";
            }

            ContentFile contentFile = new ContentFile(name, path, contentUrl, downloadUrl, type);

            if (contentFile.isDirectory()) {
                contentFiles.addAll(getContentsByUrl(contentFile.getUrl()));
            } else {
                contentFiles.add(contentFile);
            }
        }

        return contentFiles;
    }

    public static List<ContentFile> getRepositoryContents(Repository repository) {
        String url = API_URL + "/repos/" + repository.getFullName() + "/contents";
        return getContentsByUrl(url);
    }

    public static void downloadRepositoryFiles(Repository repository) {
        List<ContentFile> repositoryContents = getRepositoryContents(repository);

        for (ContentFile contentFile : repositoryContents) {
            try {
                URL url = new URL(contentFile.getDownloadUrl());
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                File file = new File(repository.getName() + "/" + contentFile.getPath());

                File parentDir = file.getParentFile();
                if (parentDir != null) {
                    parentDir.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(file);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                rbc.close();
                fos.close();
            } catch (MalformedURLException e) {
                System.err.print("Wrong url!");
            } catch (IOException e) {
                System.err.print(e.toString());
            }
        }
    }
}
