package com.Project_INTELLCAP.Infinitum_Art.auth.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:${user.home}/uploads/profiles}")
    private String uploadDir;

    public String storeProfileImage(MultipartFile imageFile, Long userId) throws IOException {
        // 1. Validate input
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        // 2. Check file type (JPG, PNG, WEBP, etc.)
        List<String> allowedTypes = List.of(
                "image/jpg",
                "image/jpeg",        // .jpg, .jpeg
                "image/pjpeg",       // progressive JPEG
                "image/png",         // .png
                "image/gif",         // .gif
                "image/bmp",         // .bmp
                "image/x-windows-bmp",
                "image/vnd.wap.wbmp" // .wbmp
        );

        if (!allowedTypes.contains(imageFile.getContentType())) {
            throw new IllegalArgumentException("Only jpeg, jpg, PNG, pjpeg, bmp, or GIF images are allowed");
        }

        // 3. Check file size (max 20MB)
        long maxSize = 10 * 1024 * 1024; // 20MB
        if (imageFile.getSize() > maxSize) {
            throw new IllegalArgumentException("Image must be smaller than 20MB");
        }

        // 4. Process image (resize + convert to WEBP)
        BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
        BufferedImage resizedImage = resizeImage(originalImage, 600, 600);
        byte[] jpegImageBytes = convertToJpeg(resizedImage,0.8f);
        if (jpegImageBytes == null || jpegImageBytes.length == 0) System.out.println("Original image is null");
        // 5. Store using the base method (now takes byte[])
        return storeFile(
                jpegImageBytes,      // Processed image bytes
                userId,              // User ID
                "profile",           // Folder name
                null,               // Custom filename (null = auto-generate)
                ".jpeg"             // File extension (WEBP)
        );
    }
    private BufferedImage resizeImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Calculate new dimensions
        if (width > maxWidth || height > maxHeight) {
            double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
            width = (int) (width * ratio);
            height = (int) (height * ratio);
        }

        // Create resized image
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }


    private byte[] convertToJpeg(BufferedImage image, float quality) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext()) {
                throw new IllegalStateException("No JPEG writer found");
            }

            ImageWriter writer = writers.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);  // 0.0f (worst) → 1.0f (best)
            }

            writer.write(null, new IIOImage(image, null, null), param);
            ios.close();
            writer.dispose();
            return baos.toByteArray();
        }
    }


    public String storeFile(
            byte[] fileBytes,
            Long userId,
            String folderName,
            String filename,
            String fileExtension
    ) throws IOException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("File data cannot be empty");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (folderName == null || folderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Folder name cannot be empty");
        }

        Path storagePath = Paths.get(uploadDir)
                .resolve(userId.toString())
                .resolve(folderName)
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(storagePath);

        String finalFilename = (filename != null)
                ? filename + fileExtension
                : UUID.randomUUID().toString() + fileExtension;

        Path targetPath = storagePath.resolve(finalFilename);
        Files.write(targetPath, fileBytes); // Directly write bytes

        return String.format("/images/%s/%s/%s", userId, folderName, finalFilename);
    }
    public String storeFile(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // Copy file to the target location
        Path targetLocation = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Return the relative path that will be stored in the database
        return "/images/" + newFilename;
    }

    public Path getFilePath(String filename) {
        // Remove only the prefix /images
        String relativePath = filename.replaceFirst("^/images/", "");

        System.out.println("relativePath: " + relativePath);
        Path p =  Paths.get(uploadDir).resolve(relativePath).normalize();
        System.out.println("p: " + p);
        // Combine uploadDir with relative path
        return p;
    }


    public void deleteFile(String filename) throws IOException {
        try {
            Path filePath = getFilePath(filename);
            System.out.println("Attempting to delete file at: " + filePath.toAbsolutePath());

            if (Files.exists(filePath)) {
                boolean deleted = Files.deleteIfExists(filePath);
                if (deleted) {
                    System.out.println("File deleted successfully.");
                } else {
                    System.err.println("File exists but could not be deleted.");
                }
            } else {
                System.err.println("File not found: " + filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("IOException occurred while deleting file: " + filename);
            e.printStackTrace();
            throw e; // Re-throw if you want upper layers to handle it
        } catch (Exception e) {
            System.err.println("Unexpected error during file deletion: " + filename);
            e.printStackTrace();
            throw new IOException("Unexpected error while deleting file: " + filename, e);
        }
    }
}