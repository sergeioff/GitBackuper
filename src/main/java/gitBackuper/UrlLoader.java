package gitBackuper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UrlLoader {
    public static String getContentFromUrl(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();

            for (String line; (line = reader.readLine()) != null; ) {
                sb.append(line);
            }

            return sb.toString();
        } catch (IOException e) {
           System.err.println("Failed to get content from " + url);
        }

        return null;
    }
}
