package io.github.dong4j.coco.plugin.deploy.mojo.frontend;

import com.github.eirslett.maven.plugins.frontend.lib.FrontendPluginFactory;
import com.github.eirslett.maven.plugins.frontend.lib.TaskRunnerException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.09 23:54
 * @since 1.7.0
 */
@Mojo(name = "webpack", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public final class WebpackMojo extends AbstractFrontendMojo {

    /**
     * Webpack arguments. Default is empty (runs just the "webpack" command).
     */
    @Parameter(property = "frontend.webpack.arguments")
    private String arguments;

    /**
     * Files that should be checked for changes, in addition to the srcdir files.
     * Defaults to webpack.config.js in the {@link #workingDirectory}.
     */
    @Parameter(property = "triggerfiles")
    private List<File> triggerfiles;

    /**
     * The directory containing front end files that will be processed by webpack.
     * If this is set then files in the directory will be checked for
     * modifications before running webpack.
     */
    @Parameter(property = "srcdir")
    private File srcdir;

    /**
     * The directory where front end files will be output by webpack. If this is
     * set then they will be refreshed so they correctly show as modified in
     * Eclipse.
     */
    @Parameter(property = "outputdir")
    private File outputdir;

    /**
     * Skips execution of this mojo.
     */
    @Parameter(property = "skip.webpack", defaultValue = "${skip.webpack}")
    private boolean skip;

    /** Build context */
    @Component
    private BuildContext buildContext;

    /**
     * Skip execution
     *
     * @return the boolean
     * @since 1.7.0
     */
    @Override
    protected boolean skipExecution() {
        return this.skip;
    }

    /**
     * Execute
     *
     * @param factory factory
     * @throws TaskRunnerException task runner exception
     * @since 1.7.0
     */
    @Override
    public synchronized void execute(FrontendPluginFactory factory) throws TaskRunnerException {
        if (this.shouldExecute()) {
            factory.getWebpackRunner().execute(this.arguments, this.environmentVariables);

            if (this.outputdir != null) {
                this.getLog().info("Refreshing files after webpack: " + this.outputdir);
                this.buildContext.refresh(this.outputdir);
            }
        } else {
            this.getLog().info("Skipping webpack as no modified files in " + this.srcdir);
        }
    }

    /**
     * Should execute
     *
     * @return the boolean
     * @since 1.7.0
     */
    private boolean shouldExecute() {
        if (this.triggerfiles == null || this.triggerfiles.isEmpty()) {
            this.triggerfiles = Collections.singletonList(new File(this.workingDirectory, "webpack.config.js"));
        }

        return MojoUtils.shouldExecute(this.buildContext, this.triggerfiles, this.srcdir);
    }

}
