package gitBackuper.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TokenLoader {
    private static final String TOKEN_FILE_NAME = ".token";

    public static String loadToken() {
        Path path = Paths.get(TOKEN_FILE_NAME);

        if (Files.exists(path)) {
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(path.toFile()));
                String token = fileReader.readLine();

                if (token.length() != 40) {
                    throw new InvalidTokenException();
                }

                return token;
            } catch (InvalidTokenException e) {
                System.err.print(e.getMessage());
                System.err.println(" Continuing as anonymous user.");
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }

        return null;
    }

    private static class InvalidTokenException extends RuntimeException {
        @Override
        public String getMessage() {
            return "Invalid token!";
        }
    }
}
