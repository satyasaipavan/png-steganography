import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Kyle Gough on 27/12/2016.
 * Steganography Embedder and Extractor - ©2016 Kyle Gough
 */
class StegFile {

    private File file;

    StegFile(File _file) {
        this.file = _file;
    }

    Path getFilePath() {
        return file.toPath();
    }

    long getFileSize() {
        return file.length();
    }

    byte[] getAllBytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".")+1);
        }
        else {
            return "";
        }
    }

}
