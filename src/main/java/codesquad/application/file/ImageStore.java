package codesquad.application.file;

import codesquad.server.message.HttpFile;
import codesquad.server.message.RuntimeIOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ImageStore {

    private static final String PATH = "images";

    public String store(HttpFile httpFile) {
        File dir = new File(PATH);
        if(!dir.exists()) {
            if(!dir.mkdir()) {
                throw new RuntimeIOException("디렉터리 생성 불가");
            }
        }
        String extension = httpFile.getFileName().split("\\.")[1];
        String storeFilename = UUID.randomUUID() + "." + extension;
        File file = new File(dir, storeFilename);
        try (
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
        ) {
            bos.write(httpFile.getContent());
        } catch (IOException e) {
            throw new RuntimeIOException("파일 쓰기 실패", e);
        }
        return file.getPath();
    }

    public byte[] getImage(String storeFilename) {
        File file = new File(PATH + "/" + storeFilename);
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            return bis.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
