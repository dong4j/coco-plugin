package io.github.dong4j.coco.plugin.common.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.12 11:39
 * @since 1.5.0
 */
@Slf4j
@UtilityClass
public class CompressUtils {

    /** BUFFER_SIZE */
    private static final int BUFFER_SIZE = 1024 * 100;

    /**
     * Gets files *
     *
     * @param path path
     * @return the files
     * @since 1.5.0
     */
    public static @NotNull List<File> getFiles(String path) {
        List<File> list = new LinkedList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (null != tempList && tempList.length > 0) {
            for (File value : tempList) {
                if (value.isFile()) {
                    list.add(new File(value.getPath()));
                }
                if (value.isDirectory()) {
                    List<File> tmpList = getFiles(value.getPath());
                    if (!tmpList.isEmpty()) {
                        list.addAll(tmpList);
                    }
                }
            }
        }
        return list;
    }

    /**
     * ????????????????????????????????????tar????????????
     *
     * @param files     ????????????????????????
     * @param inPutPath in put path
     * @param target    tar ????????????????????????
     * @return File ???????????????????????????
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Contract("_, _, _ -> param3")
    public static File pack(List<File> files, String inPutPath, File target) throws IOException {
        try (FileOutputStream out = new FileOutputStream(target)) {
            try (BufferedOutputStream bos = new BufferedOutputStream(out, BUFFER_SIZE)) {
                try (TarArchiveOutputStream os = new TarArchiveOutputStream(bos)) {
                    // ???????????????????????????
                    os.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
                    for (File file : files) {
                        // ???????????????????????????
                        os.putArchiveEntry(new TarArchiveEntry(file, file.getAbsolutePath().replace(inPutPath, "")));
                        try (FileInputStream fis = new FileInputStream(file)) {
                            IOUtils.copy(fis, os);
                            os.closeArchiveEntry();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return target;
    }

    /**
     * Compress
     *
     * @param source   source
     * @param target   target
     * @param fileName file name
     * @throws Exception exception
     * @since 1.5.0
     */
    public static void compress(String source, String target, String fileName) throws IOException {

        List<File> list = getFiles(source);
        if (list.isEmpty()) {
            log.info("source file is empty , please check [{}]", source);
            return;
        }
        File file = new File(target);
        if (!file.exists()) {
            file.mkdirs();
        }

        compressTar(list, source, target, fileName);
    }

    /**
     * ??????tar??????
     *
     * @param list       list
     * @param inPutPath  in put path
     * @param outPutPath out put path
     * @param fileName   file name
     * @return the file
     * @throws Exception exception
     * @since 1.5.0
     */
    public static @NotNull File compressTar(List<File> list, String inPutPath, String outPutPath, String fileName) throws IOException {
        File outPutFile = new File(outPutPath + File.separator + fileName + ".tar.gz");
        File tempTar = new File("temp.tar");
        try (FileInputStream fis = new FileInputStream(pack(list, inPutPath, tempTar))) {
            try (BufferedInputStream bis = new BufferedInputStream(fis, BUFFER_SIZE)) {
                try (FileOutputStream fos = new FileOutputStream(outPutFile)) {
                    try (GZIPOutputStream gzp = new GZIPOutputStream(fos)) {
                        int count;
                        byte[] data = new byte[BUFFER_SIZE];
                        while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                            gzp.write(data, 0, count);
                        }
                    }
                }
            }
        }
        Files.deleteIfExists(tempTar.toPath());
        return outPutFile;
    }

    /**
     * Decompress
     *
     * @param filePath  file path
     * @param outputDir output dir
     * @return the boolean
     * @since 1.5.0
     */
    public static void decompress(String filePath, String outputDir) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("decompress file not exist.");
            return;
        }
        try {
            if (filePath.endsWith(".zip")) {
                unZip(file, outputDir);
            }
            if (filePath.endsWith(".tar.gz") || filePath.endsWith(".tgz")) {
                decompressTarGz(file, outputDir);
            }
            if (filePath.endsWith(".tar.bz2")) {
                decompressTarBz2(file, outputDir);
            }
            filterFile(new File(outputDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????? .zip ??????
     *
     * @param file      ????????????zip????????????
     * @param outputDir ????????????????????????????????????
     * @throws IOException io exception
     * @since 1.5.0
     */
    public static void unZip(File file, String outputDir) throws IOException {
        try (ZipFile zipFile = new ZipFile(file, StandardCharsets.UTF_8)) {
            // ??????????????????
            createFile(outputDir, null);
            Enumeration<?> enums = zipFile.entries();
            while (enums.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) enums.nextElement();
                if (entry.isDirectory()) {
                    // ???????????????
                    createFile(outputDir, entry.getName());
                } else {
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        try (OutputStream out = new FileOutputStream(
                            outputDir + File.separator + entry.getName())) {
                            writeFile(in, out);
                        }
                    }
                }
            }
        }
    }

    /**
     * Decompress tar gz
     *
     * @param sourceFile file
     * @param outputDir  output dir
     * @throws IOException io exception
     * @since 1.5.0
     */
    public static void decompressTarGz(File sourceFile, String outputDir) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(
            new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(sourceFile))))) {
            // ??????????????????
            createFile(outputDir, null);
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                // ?????????
                if (entry.isDirectory()) {
                    // ???????????????
                    createFile(outputDir, entry.getName());
                } else {
                    // ?????????
                    File file = createFile(outputDir + File.separator + entry.getName(), null);
                    try (OutputStream out = new FileOutputStream(file)) {
                        writeFile(tarIn, out);

                        if (file.getName().endsWith(".sh")) {
                            io.github.dong4j.coco.plugin.common.util.FileUtils.setFilePermissions(file);
                            io.github.dong4j.coco.plugin.common.util.FileUtils.setPosixFilePermissions(file.getAbsoluteFile().toPath());
                        }
                    }
                }
            }
        }

    }

    /**
     * ?????????tar.bz2??????
     *
     * @param file      ???????????????
     * @param outputDir ???????????????
     * @throws IOException io exception
     * @since 1.5.0
     */
    public static void decompressTarBz2(File file, String outputDir) throws IOException {
        try (TarArchiveInputStream tarIn =
                 new TarArchiveInputStream(
                     new BZip2CompressorInputStream(
                         new FileInputStream(file)))) {
            createFile(outputDir, null);
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    createFile(outputDir, entry.getName());
                } else {
                    try (OutputStream out = new FileOutputStream(
                        outputDir + File.separator + entry.getName())) {
                        writeFile(tarIn, out);
                    }
                }
            }
        }
    }

    /**
     * ?????????
     *
     * @param in  in
     * @param out out
     * @throws IOException io exception
     * @since 1.5.0
     */
    public static void writeFile(@NotNull InputStream in, OutputStream out) throws IOException {
        int length;
        byte[] b = new byte[BUFFER_SIZE];
        while ((length = in.read(b)) != -1) {
            out.write(b, 0, length);
        }
    }

    /**
     * ?????? Mac ???????????????????????? __MACOSX ???????????? . ?????????????????????
     *
     * @param filteredFile filtered file
     * @since 1.5.0
     */
    public static void filterFile(File filteredFile) {
        if (filteredFile != null) {
            File[] files = filteredFile.listFiles();
            Arrays.stream(Objects.requireNonNull(files)).forEach(file -> {
                if (file.getName().startsWith(".") ||
                    (file.isDirectory() && "__MACOSX".equals(file.getName()))) {
                    FileUtils.deleteQuietly(file);
                }
            });

        }
    }

    /**
     * ????????????
     *
     * @param outputDir ????????????
     * @param subDir    ?????????
     * @return the file
     * @since 1.5.0
     */
    private static @NotNull File createFile(String outputDir, String subDir) {
        File file = new File(outputDir);
        // ??????????????????
        if (!(subDir == null || subDir.trim().equals(""))) {
            file = new File(outputDir + File.separator + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.isDirectory()) {
                file.mkdirs();
            }
        }
        return file;
    }

}
