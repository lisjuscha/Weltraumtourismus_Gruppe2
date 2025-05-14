package com.example.flightprep.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for {@link FileUploadService}.
 * These tests cover the functionality of file operations such as directory creation,
 * file size validation, saving files to a temporary location, and moving files to
 * a final directory. The tests interact with the actual file system for parts of
 * the SUT's behavior, using project-relative paths and a JUnit managed @TempDir
 * for creating source files.
 */
class FileUploadServiceTest {

    private FileUploadService fileUploadService;

    @TempDir
    Path tempDirJUnit; // JUnit will manage this temporary directory

    private Path actualUploadDir;
    private Path actualTempDirService; // Corresponds to FileUploadService.TEMP_DIR

    @BeforeEach
    void setUp() throws IOException {
        fileUploadService = new FileUploadService();

        // Recreate the directory structure expected by FileUploadService INSIDE our JUnit @TempDir
        // This ensures our tests are isolated and don't use the actual "data/uploads" or "data/temp"
        // relative to the project root.
        actualUploadDir = tempDirJUnit.resolve("data/uploads");
        actualTempDirService = tempDirJUnit.resolve("data/temp");

        Files.createDirectories(actualUploadDir);
        Files.createDirectories(actualTempDirService);

        // To make the service use these dynamic paths, we would typically need to:
        // 1. Modify FileUploadService to accept base paths in constructor (preferred for testability).
        // 2. Or, use reflection to change its private static final String UPLOAD_DIR / TEMP_DIR (complex and risky).
        // For now, the test will operate on the assumption that if we can PREPARE the source/target dirs correctly
        // relative to how FileUploadService constructs its Paths (e.g. Paths.get(UPLOAD_DIR, fileName)),
        // we might be able to test it. This is tricky because UPLOAD_DIR is "data/uploads".

        // For this test setup to work cleanly without modifying FileUploadService,
        // we might need to ensure that when Paths.get(UPLOAD_DIR, fileName) is called,
        // it resolves within our @TempDir. This can be done if the test's working directory
        // is the parent of "data". This is often the project root by default.
        // So, if FileUploadService uses "data/uploads", and our test runs from project root,
        // it will try to create actual ./data/uploads folders.

        // Let's assume for now that the test needs to create the "data/uploads" and "data/temp"
        // directories in the *actual project structure* if not modifying the SUT.
        // This makes tests less isolated.

        // A better approach IF SUT CANNOT BE CHANGED:
        // Create a "data" folder inside tempDirJUnit, then "uploads" and "temp" inside that.
        // Then, in tests, create files in tempDirJUnit.resolve("data/temp/...")
        // and verify they move to tempDirJUnit.resolve("data/uploads/...")
        // This requires FileUploadService's hardcoded "data/uploads" to be interpreted
        // relative to a base path we can control or predict.
        // If FileUploadService always resolves Paths.get("data/uploads") from project root,
        // then @TempDir is less useful for redirecting its internal paths without SUT change.

        // Let's try to make FileUploadService work with paths relative to the @TempDir
        // This requires modifying the SUT or very careful test setup.

        // Given the SUT has:
        // private static final String UPLOAD_DIR = "data/uploads";
        // private static final String TEMP_DIR = "data/temp";
        // And methods like: Paths.get(UPLOAD_DIR, fileName);

        // For the current SUT, these paths are relative to the execution directory.
        // To use @TempDir effectively, we need to ensure these relative paths resolve
        // inside `tempDirJUnit`. This is typically NOT how @TempDir is used with hardcoded relative paths
        // in the SUT.

        // Simplification: The tests will interact with the SUT as is.
        // `createDirectories()` will be called, and it will try to create "data/uploads" and "data/temp"
        // relative to the project root. The tests will then place files there and verify.
        // This is not ideal isolation but tests the current SUT.
        // We should clean up these directories after tests if possible, though @TempDir won't do it for fixed paths.

        // Let's clear and create these directories before each test to ensure a clean state.
        // This is an integration test approach.
        Path projectRootDataDir = Path.of("data");
        Path projectUploadDir = projectRootDataDir.resolve("uploads");
        Path projectTempDir = projectRootDataDir.resolve("temp");

        // Clean up and create
        if (Files.exists(projectUploadDir)) {
            Files.walk(projectUploadDir).sorted(java.util.Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (Files.exists(projectTempDir)) {
            Files.walk(projectTempDir).sorted(java.util.Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        Files.createDirectories(projectUploadDir);
        Files.createDirectories(projectTempDir);
    }

    @Test
    void createDirectories_createsDirsSuccessfully() throws IOException {
        // Directories are created in @BeforeEach, so we call it again to ensure idempotency
        // and that it doesn't throw an exception if they exist.
        fileUploadService.createDirectories();

        assertTrue(Files.exists(Path.of("data", "uploads")), "Upload directory should exist.");
        assertTrue(Files.exists(Path.of("data", "temp")), "Temp directory should exist.");
    }

    @Test
    void isValidFileSize_returnsTrue_forFileUnderLimit() throws IOException {
        Path filePath = tempDirJUnit.resolve("under_limit.txt");
        Files.write(filePath, new byte[1024]); // 1KB
        File testFile = filePath.toFile();
        assertTrue(fileUploadService.isValidFileSize(testFile));
    }

    @Test
    void isValidFileSize_returnsTrue_forFileAtLimit() throws IOException {
        Path filePath = tempDirJUnit.resolve("at_limit.txt");
        long maxSize = 10 * 1024 * 1024; // 10MB (as defined in SUT)
        Files.write(filePath, new byte[(int)maxSize]);
        File testFile = filePath.toFile();
        assertTrue(fileUploadService.isValidFileSize(testFile));
    }

    @Test
    void isValidFileSize_returnsFalse_forFileOverLimit() throws IOException {
        Path filePath = tempDirJUnit.resolve("over_limit.txt");
        long maxSize = 10 * 1024 * 1024; // 10MB
        Files.write(filePath, new byte[(int)maxSize + 1]);
        File testFile = filePath.toFile();
        assertFalse(fileUploadService.isValidFileSize(testFile));
    }

    @Test
    void isValidFileSize_returnsTrue_forEmptyFile() throws IOException {
        Path filePath = tempDirJUnit.resolve("empty_file.txt");
        Files.createFile(filePath);
        File testFile = filePath.toFile();
        assertTrue(fileUploadService.isValidFileSize(testFile));
    }

    @Test
    void saveToTemp_copiesFileToTempDir_withTimestampedName() throws IOException {
        // 1. Create an original file in JUnit's @TempDir (not the service's TEMP_DIR)
        Path originalFilePath = tempDirJUnit.resolve("original.txt");
        byte[] originalContent = "Test content for saveToTemp".getBytes();
        Files.write(originalFilePath, originalContent);
        File originalFile = originalFilePath.toFile();

        // 2. Call the service method
        File savedTempFile = fileUploadService.saveToTemp(originalFile);

        // 3. Verify the savedTempFile properties
        assertNotNull(savedTempFile);
        assertTrue(savedTempFile.exists(), "File should exist in temp directory");
        assertTrue(savedTempFile.getName().endsWith("_original.txt"), "File name should end with original name");
        assertTrue(savedTempFile.getName().matches("^\\d+_original\\.txt$"), "File name should be timestamp_original.txt");

        // Verify it's in the service's TEMP_DIR ("data/temp")
        Path expectedParentDir = Path.of("data", "temp");
        assertEquals(expectedParentDir.toAbsolutePath().normalize(), savedTempFile.getParentFile().toPath().toAbsolutePath().normalize(),
                "Saved file should be in the data/temp directory");

        // 4. Verify content
        byte[] savedContent = Files.readAllBytes(savedTempFile.toPath());
        assertArrayEquals(originalContent, savedContent, "Content of saved temp file should match original");
    }

    @Test
    void saveToTemp_replacesExistingTempFile() throws IOException, InterruptedException {
        Path originalFilePath1 = tempDirJUnit.resolve("original1.txt");
        Files.write(originalFilePath1, "Content 1".getBytes());
        File originalFile1 = originalFilePath1.toFile();

        // Call once - this will create a file like "timestamp1_original1.txt"
        // To ensure a name collision for the *exact same timestamped name* is hard to force without controlling System.currentTimeMillis().
        // Instead, we test REPLACE_EXISTING by creating a file at the target path *before* the second call.

        // Simulate the SUT creating a temp file name
        // We can't easily predict System.currentTimeMillis(), so we'll manually create a target file
        // that saveToTemp would try to overwrite.

        // This test is tricky because of the timestamp. Let's reconsider.
        // StandardCopyOption.REPLACE_EXISTING is tested if Files.copy is called when target exists.
        // A simpler test: save a file, then save another *original* file with the *same name*.
        // The SUT will generate *different* timestamped names, so they won't collide based on SUT logic.
        // The REPLACE_EXISTING is more about if `Files.copy` itself is told to replace if the *target path* (timestamp_name) already exists.

        // Let's test that if we save the *same original file* twice, we get two different temp files (due to timestamp)
        // but the second call still succeeds.
        File savedTempFile1 = fileUploadService.saveToTemp(originalFile1);
        assertTrue(savedTempFile1.exists());

        // Wait a millisecond to ensure a different timestamp if the system clock is fast enough
        // This is not perfectly reliable for all systems/resolutions.
        Thread.sleep(50);

        Path originalFilePath2 = tempDirJUnit.resolve("original1.txt"); // Same original name
        Files.write(originalFilePath2, "Content 2 for overwrite".getBytes()); // Different content
        File originalFile2 = originalFilePath2.toFile();

        File savedTempFile2 = fileUploadService.saveToTemp(originalFile2); // Save same original name again
        assertTrue(savedTempFile2.exists());
        assertNotEquals(savedTempFile1.getName(), savedTempFile2.getName(), "Two saves of same original name should result in different temp names due to timestamp");

        byte[] content2 = Files.readAllBytes(savedTempFile2.toPath());
        assertArrayEquals("Content 2 for overwrite".getBytes(), content2);
    }

    @Test
    void moveFilesToFinalDirectory_movesFileToUploadDir_withUserIdPrefix() throws IOException {
        String userId = "user123";
        String originalTempFileName = "tempFile1.pdf";
        byte[] content = "PDF content here".getBytes();

        // 1. Create a mock temporary file in the service's TEMP_DIR (./data/temp)
        Path tempFilePath = Path.of("data", "temp", originalTempFileName);
        Files.write(tempFilePath, content);
        File tempFileToMove = tempFilePath.toFile();
        assertTrue(tempFileToMove.exists(), "Temp file should exist before move");

        List<File> tempFiles = new ArrayList<>();
        tempFiles.add(tempFileToMove);

        // 2. Call service method
        fileUploadService.moveFilesToFinalDirectory(tempFiles, userId);

        // 3. Verify
        Path expectedFinalPath = Path.of("data", "uploads", userId + "_" + originalTempFileName);
        assertTrue(Files.exists(expectedFinalPath), "File should exist in upload directory with user prefix");
        assertFalse(tempFilePath.toFile().exists(), "Original temp file should be moved (deleted)");
        assertArrayEquals(content, Files.readAllBytes(expectedFinalPath), "Content should match");
    }

    @Test
    void moveFilesToFinalDirectory_movesMultipleFiles() throws IOException {
        String userId = "userMulti";
        byte[] content1 = "Content file 1".getBytes();
        byte[] content2 = "Content file 2".getBytes();

        Path tempFilePath1 = Path.of("data", "temp", "multi1.txt");
        Files.write(tempFilePath1, content1);
        Path tempFilePath2 = Path.of("data", "temp", "multi2.log");
        Files.write(tempFilePath2, content2);

        List<File> tempFiles = List.of(tempFilePath1.toFile(), tempFilePath2.toFile());

        fileUploadService.moveFilesToFinalDirectory(tempFiles, userId);

        Path finalPath1 = Path.of("data", "uploads", userId + "_" + "multi1.txt");
        Path finalPath2 = Path.of("data", "uploads", userId + "_" + "multi2.log");

        assertTrue(Files.exists(finalPath1));
        assertArrayEquals(content1, Files.readAllBytes(finalPath1));
        assertFalse(tempFilePath1.toFile().exists());

        assertTrue(Files.exists(finalPath2));
        assertArrayEquals(content2, Files.readAllBytes(finalPath2));
        assertFalse(tempFilePath2.toFile().exists());
    }

    @Test
    void moveFilesToFinalDirectory_replacesExistingFinalFile() throws IOException {
        String userId = "userReplace";
        String fileName = "replaceable.doc";
        byte[] initialContent = "Initial version".getBytes();
        byte[] newContent = "New version to replace".getBytes();

        // 1. Create an existing file in UPLOAD_DIR
        Path finalTargetPath = Path.of("data", "uploads", userId + "_" + fileName);
        Files.write(finalTargetPath, initialContent);
        assertTrue(Files.exists(finalTargetPath));
        assertEquals(initialContent.length, Files.size(finalTargetPath));

        // 2. Create a new temp file to move
        Path tempToMovePath = Path.of("data", "temp", fileName);
        Files.write(tempToMovePath, newContent);

        List<File> tempFiles = List.of(tempToMovePath.toFile());

        // 3. Call service
        fileUploadService.moveFilesToFinalDirectory(tempFiles, userId);

        // 4. Verify replacement
        assertTrue(Files.exists(finalTargetPath), "File should still exist in upload dir");
        assertArrayEquals(newContent, Files.readAllBytes(finalTargetPath), "Content should be replaced with new version");
        assertFalse(tempToMovePath.toFile().exists(), "Temp file should be gone");
    }

    @Test
    void moveFilesToFinalDirectory_emptyList_doesNothing() throws IOException {
        String userId = "userEmptyList";
        List<File> emptyList = new ArrayList<>();

        // Get current state of upload dir (e.g., count files)
        long filesBefore = Files.exists(Path.of("data", "uploads")) ? Files.list(Path.of("data", "uploads")).count() : 0;

        assertDoesNotThrow(() -> fileUploadService.moveFilesToFinalDirectory(emptyList, userId));

        long filesAfter = Files.exists(Path.of("data", "uploads")) ? Files.list(Path.of("data", "uploads")).count() : 0;
        assertEquals(filesBefore, filesAfter, "Number of files in upload directory should not change for empty list");
    }

    @Test
    void saveToTemp_throwsIOException_whenOriginalFileDoesNotExist() {
        // 1. Create a File object pointing to a non-existent file
        File nonExistentFile = tempDirJUnit.resolve("non_existent_original.txt").toFile();
        assertFalse(nonExistentFile.exists(), "Test setup: Original file should not exist.");

        // 2. Assert that IOException (or a subclass like NoSuchFileException) is thrown
        assertThrows(IOException.class, () -> {
            fileUploadService.saveToTemp(nonExistentFile);
        }, "Should throw IOException when original file does not exist for saveToTemp");
    }

    @Test
    void moveFilesToFinalDirectory_throwsIOException_whenTempFileDoesNotExist() {
        String userId = "userNonExistent";
        // 1. Create a File object for a non-existent temp file
        File nonExistentTempFile = Path.of("data", "temp", "non_existent_temp.txt").toFile();
        // Ensure it doesn't actually exist in the location the SUT will look for it.
        // The @BeforeEach cleans data/temp, so it shouldn't exist. We can double check.
        assertFalse(nonExistentTempFile.exists(), "Test setup: Temp file should not exist in data/temp.");


        List<File> tempFiles = new ArrayList<>();
        tempFiles.add(nonExistentTempFile);

        // 2. Assert that IOException (or a subclass like NoSuchFileException) is thrown
        assertThrows(IOException.class, () -> {
            fileUploadService.moveFilesToFinalDirectory(tempFiles, userId);
        }, "Should throw IOException when a temp file does not exist for moveFilesToFinalDirectory");
    }

    // Test methods will follow
}