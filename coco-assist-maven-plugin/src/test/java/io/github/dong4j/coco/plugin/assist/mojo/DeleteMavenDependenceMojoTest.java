package io.github.dong4j.coco.plugin.assist.mojo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import io.github.dong4j.coco.plugin.common.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.25 18:37
 * @since 1.8.0
 */
@Slf4j
class DeleteMavenDependenceMojoTest {

    /**
     * Test 1
     *
     * @since 1.8.0
     */
    @Test
    void test_1() {
        File file = new File("/Users/dong4j/.m2/repository/io/github/dong4j/coco");
        Collection<File> listFiles = FileUtils.listFiles(file, new String[] {"lastUpdated"}, true);
        listFiles.forEach(f -> {
            try {
                Files.delete(f.toPath());
                log.info("delete: " + f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Assertions.assertTrue(FileUtils.listFiles(file, new String[] {"lastUpdated"}, true).isEmpty());
    }

}
