package com.group17.comic.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

public final class FileUtils {
    public static final List<String> IMAGE_FILE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

    private FileUtils() {}

    public static byte[] readFileToByteArray(Resource resource) throws IOException {
        return StreamUtils.copyToByteArray(resource.getInputStream());
    }

    public static File loadFile(String fileName) throws Exception {
        var classLoader = FileUtils.class.getClassLoader();
        URI fileUri = classLoader.getResource(fileName).toURI();
        return new File(fileUri);
    }

    public static File loadFileV2(String fileName) {
        String projectDir = System.getProperty("user.dir");
        File file = new File("%s/src/main/resources/%s".formatted(projectDir, fileName));
        return file;
    }

    public static boolean validateFile(
            @NotNull MultipartFile file, @NotNull String[] extensions, @Positive long maxSize) {
        if (file.getSize() > maxSize) {
            return false;
        }
        String fileExtension = file.getOriginalFilename().split(".")[1];
        return Arrays.asList(extensions).contains(fileExtension);
    }

    public static boolean validateFile(
            @NotNull MultipartFile file, @NotNull String[] extensions, long maxSize, String contentType) {
        if (!file.getContentType().startsWith(contentType)) {
            return false;
        }
        return validateFile(file, extensions, maxSize);
    }
}
