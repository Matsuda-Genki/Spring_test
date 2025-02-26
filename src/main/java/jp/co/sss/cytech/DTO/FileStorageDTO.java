package jp.co.sss.cytech.DTO;

import org.springframework.web.multipart.MultipartFile;

public class FileStorageDTO {
    private MultipartFile file;

    // getter/setter
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
