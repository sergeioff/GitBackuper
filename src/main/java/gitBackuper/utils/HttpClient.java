package gitBackuper.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpClient {
    /**
     * Makes http get request to specified url
     * @param url url to request
     * @return response
     */
    public static String makeGetRequest(String url) {
        String response = null;

        try {
            URLConnection connection = new URL(url).openConnection();
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();

            for (String line; (line = reader.readLine()) != null; ) {
                responseBuilder.append(line);
            }

            response = responseBuilder.toString();
            reader.close();
        } catch (IOException e) {
            System.err.println("Failed to get: " + url);
        }

        return response;
    }
}
