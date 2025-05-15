package com.example.flightprep.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * The `FileUploadService` class provides functionality for handling file uploads
 * in the Flight Preparation application. It includes methods for saving files to
 * temporary directories, moving files to final directories, validating file sizes,
 * and creating necessary directories.
 */
public class FileUploadService {
    // Path relative to the application's execution directory for final customer file uploads.
    private static final String UPLOAD_DIR = "data/uploads";
    // Path relative to the application's execution directory for temporary file storage during upload.
    private static final String TEMP_DIR = "data/temp";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Moves files from the temporary directory to the final upload directory.
     * The files are renamed to include the user ID as a prefix.
     *
     * @param tempFiles A list of temporary files to move.
     * @param userId    The user ID to prefix the file names.
     * @throws IOException If an I/O error occurs during the file move operation.
     */
    public void moveFilesToFinalDirectory(List<File> tempFiles, String userId) throws IOException {
        for (File tempFile : tempFiles) {
            String fileName = userId + "_" + tempFile.getName();
            Path targetPath = Paths.get(UPLOAD_DIR, fileName);
            // Replace the file if it already exists in the target directory.
            Files.move(tempFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Saves a file to the temporary directory with a timestamped name.
     *
     * @param originalFile The original file to save.
     * @return The saved file in the temporary directory.
     * @throws IOException If an I/O error occurs during the file save operation.
     */
    public File saveToTemp(File originalFile) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = timestamp + "_" + originalFile.getName();
        Path tempPath = Paths.get(TEMP_DIR, fileName);

        // Replace the file if it already exists in the temporary directory.
        Files.copy(originalFile.toPath(), tempPath, StandardCopyOption.REPLACE_EXISTING);
        return tempPath.toFile();
    }

    /**
     * Validates if the size of a file is within the allowed limit.
     *
     * @param file The file to validate.
     * @return `true` if the file size is valid, otherwise `false`.
     */
    public boolean isValidFileSize(File file) {
        return file.length() <= MAX_FILE_SIZE;
    }

    /**
     * Creates the necessary directories for file uploads and temporary storage.
     *
     * @throws IOException If an I/O error occurs during directory creation.
     */
    public void createDirectories() throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        Files.createDirectories(Paths.get(TEMP_DIR));
    }
}
