package gitBackuper.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloader {
    public static void downloadFile(String url, Path pathToSave) {
        File parentDirectory = pathToSave.toFile().getParentFile();
        if (parentDirectory != null) {
            parentDirectory.mkdirs(); //Creation of parent directories
        }

        try (ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fos = new FileOutputStream(pathToSave.toFile())) {
            fos.getChannel().transferFrom(rbc, 0 , Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
