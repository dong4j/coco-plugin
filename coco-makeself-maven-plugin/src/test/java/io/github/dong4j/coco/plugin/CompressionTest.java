package io.github.dong4j.coco.plugin;

import org.junit.jupiter.api.Test;

import io.github.dong4j.coco.plugin.common.util.CompressUtils;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.12 11:17
 * @since 1.5.0
 */
@SuppressWarnings("all")
class CompressionTest {

    /** formatter:off */
private final String path = "/Users/dong4j/develop/work/coco/2020/coco-stack/services/coco-framework/ability/coco-plugin/coco-plugin" +
                            "-sample/coco-makeself-maven-plugin-sample/repackage-single-module/target";

    /**
     * Test 1
     *
     * @since 1.5.0
     */
@Test
    void test_1() {
        CompressUtils.decompress(this.path + "/repackage-single-module-06121440.tar.gz",
                                 this.path + "/");
    }

}
