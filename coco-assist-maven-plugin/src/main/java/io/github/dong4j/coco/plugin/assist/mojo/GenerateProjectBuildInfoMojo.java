package io.github.dong4j.coco.plugin.assist.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import io.github.dong4j.coco.plugin.common.CocoAbstractMojo;
import io.github.dong4j.coco.plugin.common.FileWriter;
import io.github.dong4j.coco.plugin.common.Plugins;
import io.github.dong4j.coco.plugin.common.exception.NullAdditionalPropertyValueException;
import lombok.SneakyThrows;

/**
 * <p>Description: 用于生成 build-info.properties  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.13 07:08
 * @since 1.0.0
 */
@Mojo(name = "generate-build-info", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class GenerateProjectBuildInfoMojo extends CocoAbstractMojo {

    /**
     * The location of the generated build-info.properties.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}/META-INF/build-info.properties")
    private File outputFile;

    /**
     * The value used for the {@code build.time} property in a form suitable for
     * {@link Instant#parse(CharSequence)}. Defaults to {@code session.request.startTime}.
     * To disable the {@code build.time} property entirely, use {@code 'off'}.
     *
     * @since 2.2.0
     */
    @Parameter(defaultValue = "off")
    private String time;

    /**
     * Set this to 'true' to bypass artifact deploy
     *
     * @since 2.4
     */
    @Parameter(property = Plugins.SKIP_BUILD_INFO, defaultValue = Plugins.TURN_OFF_PLUGIN)
    private boolean skip;

    /**
     * 自定义配置, 会写入到 build-info.properties
     */
    @Parameter
    private Map<String, String> additionalProperties;

    /**
     * Execute *
     *
     * @since 1.0.0
     */
    @SneakyThrows
    @Override
    public void execute() {
        if (this.skip) {
            this.getLog().info("generate-build-info is skipped");
            return;
        }
        try {
            Properties properties = new Properties();
            properties.put("build.group", this.project.getGroupId());
            properties.put("build.artifact", this.project.getArtifactId());
            properties.put("build.name", this.project.getName());
            properties.put("build.version", this.project.getVersion());

            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            properties.put("build.time", dateTimeFormatter.format(new Date()));

            if (this.additionalProperties != null) {
                this.additionalProperties.forEach((name, value) -> properties.put("build." + name, value));
            }

            new FileWriter(this.outputFile).write(properties);
            this.buildContext.refresh(this.outputFile);
        } catch (NullAdditionalPropertyValueException ex) {
            throw new MojoFailureException("生成 build-info.properties 失败. " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

}
