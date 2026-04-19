package fr.notri1.minewolves.pack;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackGenerator {

    private static final String RESOURCE_PACK_DIR = "/pack";
    private static final Path OUTPUT_PATH = Path.of("pack.zip");

    public static String getPackHash() throws IOException, NoSuchAlgorithmException {
        if (!Files.exists(OUTPUT_PATH)) {
            throw new IOException("pack.zip not found. Please generate it first.");
        }
        return sha1(new FileInputStream(OUTPUT_PATH.toFile()));
    }

    public static byte[] getIcon() throws IOException {
        try (InputStream is = Objects.requireNonNull(PackGenerator.class.getResource(RESOURCE_PACK_DIR + "/pack.png")).openStream()) {
            if (is == null) {
                throw new IOException("icon.png not found in resources");
            }
            return is.readAllBytes();
        }
    }

    /**
     * Generates pack.zip from the resources/pack folder and places it in the working directory.
     */
    public static Path generate() throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(OUTPUT_PATH))) {
            URI uri = PackGenerator.class.getResource(RESOURCE_PACK_DIR).toURI();

            if (uri.getScheme().equals("jar")) {
                // Running from a JAR – open the JAR filesystem
                try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    Path packRoot = fs.getPath(RESOURCE_PACK_DIR);
                    zipDirectory(packRoot, zos);
                }
            } else {
                // Running from IDE / exploded classes
                Path packRoot = Paths.get(uri);
                zipDirectory(packRoot, zos);
            }
        } catch (URISyntaxException e) {
            throw new IOException("Failed to locate pack resources", e);
        }

        System.out.println("Resource pack generated at " + OUTPUT_PATH.toAbsolutePath());
        return OUTPUT_PATH;
    }

    public static Path getOutputPath() {
        return OUTPUT_PATH;
    }

    private static void zipDirectory(Path root, ZipOutputStream zos) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String entryName = root.relativize(file).toString().replace('\\', '/');
                zos.putNextEntry(new ZipEntry(entryName));
                try (InputStream is = Files.newInputStream(file)) {
                    is.transferTo(zos);
                }
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!dir.equals(root)) {
                    String entryName = root.relativize(dir).toString().replace('\\', '/') + "/";
                    zos.putNextEntry(new ZipEntry(entryName));
                    zos.closeEntry();
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static String sha1(FileInputStream fis) throws IOException, NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

        byte[] data = new byte[1024];
        int read = 0;
        while ((read = fis.read(data)) != -1) {
            sha1.update(data, 0, read);
        }
        ;
        byte[] hashBytes = sha1.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hashBytes.length; i++) {
            sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        String fileHash = sb.toString();
        return fileHash;
    }

}

