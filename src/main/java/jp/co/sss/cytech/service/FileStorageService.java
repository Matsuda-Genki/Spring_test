package jp.co.sss.cytech.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    
    public FileStorageService(@Value("${app.file.upload-dir:./uploads}") String uploadDir) {
    	try {
    		this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    		Files.createDirectories(this.fileStorageLocation);
    	} catch (IOException ex) {
            throw new RuntimeException(
                    "Could not create upload directory: " + uploadDir, ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        
        // ファイル名の衝突回避
        String uniqueFileName = UUID.randomUUID() + "_" + fileName;
        
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return uniqueFileName;
    }
}
