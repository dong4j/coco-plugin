package io.github.dong4j.coco.plugin.assist.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

import io.github.dong4j.coco.plugin.common.CocoAbstractMojo;
import io.github.dong4j.coco.plugin.common.FileWriter;
import io.github.dong4j.coco.plugin.common.Plugins;
import lombok.SneakyThrows;

/**
 * <p>Description: 正常编译之后生成一个标识文件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.14 13:05
 * @since 1.5.0
 */
@Mojo(name = "generate-compiled-id", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class CompiledProcessorMojo extends CocoAbstractMojo {
    /** Skip */
    @Parameter(property = Plugins.SKIP_COMPILED_ID, defaultValue = Plugins.TURN_OFF_PLUGIN)
    private boolean skip;

    /**
     * The location of the generated build-info.properties.
     */
    @Parameter(defaultValue = "${project.build.directory}/maven-status/maven-compiler-plugin/compile/identify/checked")
    private File checkFile;

    /**
     * Execute
     *
     * @since 1.5.0
     */
    @SneakyThrows
    @Override
    public void execute() {
        if (this.skip) {
            this.getLog().info("generate-compiled-id is skipped");
            return;
        }

        new FileWriter(this.checkFile).writeContent(String.valueOf(System.currentTimeMillis()));
        this.buildContext.refresh(this.checkFile);
    }
}
