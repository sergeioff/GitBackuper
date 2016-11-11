package gitBackuper.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Allows to download files from internet
 */
class FileDownloader {

    /**
     * Downloads file from internet to local disk
     * @param url url for download
     * @param pathToSave path to save
     * @throws IOException IOException while creating directories
     */
    static void downloadFile(String url, Path pathToSave) throws IOException {
        Path parentDirectory = pathToSave.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        try (ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fos = new FileOutputStream(pathToSave.toFile())) {
            fos.getChannel().transferFrom(rbc, 0 , Long.MAX_VALUE);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
