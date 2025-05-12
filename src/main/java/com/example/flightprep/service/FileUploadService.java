package com.example.flightprep.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class FileUploadService {
    private static final String UPLOAD_DIR = "data/uploads";
    private static final String TEMP_DIR = "data/temp";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public void moveFilesToFinalDirectory(List<File> tempFiles, String userId) throws IOException {
        for (File tempFile : tempFiles) {
            String fileName = userId + "_" + tempFile.getName();
            Path targetPath = Paths.get(UPLOAD_DIR, fileName);
            Files.move(tempFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public File saveToTemp(File originalFile) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = timestamp + "_" + originalFile.getName();
        Path tempPath = Paths.get(TEMP_DIR, fileName);

        Files.copy(originalFile.toPath(), tempPath, StandardCopyOption.REPLACE_EXISTING);
        return tempPath.toFile();
    }

    public boolean isValidFileSize(File file) {
        return file.length() <= MAX_FILE_SIZE;
    }

    public void createDirectories() throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        Files.createDirectories(Paths.get(TEMP_DIR));
    }
}
