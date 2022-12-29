package io.github.dong4j.coco.plugin.assist.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import io.github.dong4j.coco.plugin.common.util.FileUtils;

/**
 * <p>Description: 删除指定的 maven 依赖 </p>
 * 第一次使用:
 * 1. mvn dependency:get -Dartifact=io.github.dong4j.coco:coco-assist-maven-plugin:2022.1.1-SNAPSHOT
 * 2. mvn io.github.dong4j.coco:coco-assist-maven-plugin:2022.1.1-SNAPSHOT:clear -Dname=指定包名(前缀匹配) -Dversion=指定版本号(前缀匹配)
 * <p>
 * 之后可简化: mvn coco-assist:clear -Dname= -Dversion=
 * 全部功能:
 * 1. 删除 io/github/dong4j/coco 下所有的依赖
 * mvn coco-assist:clear -Dname=all -Dversion=all
 * 2. 删除 io/github/dong4j/coco 下所有包中指定的版本
 * mvn coco-assist:clear -Dname=all -Dversion=x.x.x
 * 3. 删除 io/github/dong4j/coco 下指定的包中指定的版本
 * mvn coco-assist:clear -Dname=xxx -Dversion=x.x.x
 * 4. 删除 io/github/dong4j/coco 下指定的包中所有的版本
 * mvn coco-assist:clear -Dname=xxx -Dversion=all
 * 5. 删除无效的目录和缓存
 * mvn coco-assist:clear
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 18:53
 * @since 1.7.1
 */
@Mojo(name = "clear", requiresProject = false)
public class DeleteMavenDependenceMojo extends AbstractMojo {
    /** ALL_FLAG */
    private static final String ALL_FLAG = "all";
    /** Repo session */
    @Parameter(defaultValue = "${repositorySystemSession}")
    private RepositorySystemSession repoSession;

    /** V5_DEPENDENCE */
    private static final String[] V5_DEPENDENCE = new String[] {
        "ability",
        "coco-supreme",
        "coco-starter",
        "coco-company",
        "coco-distribution",
        "coco-framework",
        "coco-plugin",
        "coco-agent-maven-plugin",
        "coco-assist-maven-plugin",
        "coco-checkstyle-plugin-rule",
        "coco-enforcer-plugin-rule",
        "coco-makeself-maven-plugin",
        "coco-plugin-common",
        "coco-publish-maven-plugin",
        "coco-script-maven-plugin",
        "band",
        "coco-element",
        "coco-sentinel-extend",
        "coffee",
        "coco-agent",
        "coco-auth",
        "coco-boot",
        "coco-cache",
        "coco-captcha",
        "coco-cloud",
        "coco-doc",
        "coco-dubbo",
        "coco-email",
        "coco-endpoint",
        "coco-reactive",
        "coco-servlet",
        "coco-enhance-starter",
        "coco-facade-spring-boot-starter",
        "coco-framework-starter",
        "coco-ssm-spring-boot-starter",
        "coco-state-spring-boot-starter",
        "coco-es",
        "coco-eventbus",
        "coco-feign",
        "coco-example-feign",
        "coco-id",
        "coco-idempotent",
        "coco-ip2region",
        "coco-launcher",
        "coco-logsystem",
        "coco-metrics",
        "coco-mongo",
        "coco-mq-spring-boot",
        "coco-mybatis",
        "coco-openness",
        "coco-pay",
        "coco-qrcode",
        "coco-rest",
        "coco-retry",
        "coco-schedule",
        "coco-security",
        "coco-sms",
        "coco-template",
        "coco-transaction",
        "coco-zookeeper",
        "v5-",
        };

    /** ERROR_DIR */
    private static final String[] ERROR_DIR = new String[] {
        "unknown",
        "${revision}"
    };

    /** 更新错误的缓存文件 */
    private static final String[] ERROR_FILE = new String[] {
        "lastUpdated"
    };

    /**
     * Execute
     *
     * @throws MojoExecutionException mojo execution exception
     * @throws MojoFailureException   mojo failure exception
     * @since 1.7.1
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LocalRepository localRepo = this.repoSession.getLocalRepository();
        File basedir = localRepo.getBasedir();

        String rootPath = basedir.getPath() + File.separator + "io.github.dong4j" + File.separator + "coco";
        this.getLog().info(basedir.getPath());

        File rootFile = new File(rootPath);
        this.clear(rootFile);
    }

    /**
     * Clear
     *
     * @param rootFile root file
     * @since 1.7.1
     */
    private void clear(File rootFile) {
        String artifactId = System.getProperty("name", "");
        String version = System.getProperty("version", "");

        // 删除无效的目录和缓存 (mvn coco-assist:clean)
        if (StringUtils.isBlank(artifactId) && StringUtils.isBlank(version)) {
            this.deleteAllFile(rootFile, new String[] {}, ERROR_DIR);
            this.deleteErrorFile(rootFile);
        } else if (ALL_FLAG.equals(artifactId) && ALL_FLAG.equals(version)) {
            // 删除 io/github/dong4j/coco 下所有的依赖 (mvn coco-assist:clear -Dname=all -Dversion=all)
            this.deleteAllFile(rootFile, V5_DEPENDENCE, new String[] {});
        } else if (ALL_FLAG.equals(artifactId) && StringUtils.isNotBlank(version)) {
            // 删除 io/github/dong4j/coco 下所有包中指定的版本 (mvn coco-assist:clear -Dname=all -Dversion=x.x.x)
            this.deleteByVersion(rootFile, V5_DEPENDENCE, version);
        } else if (StringUtils.isNotBlank(version)
                   && !ALL_FLAG.equals(version)
                   && StringUtils.isNotBlank(artifactId)
                   && !ALL_FLAG.equals(artifactId)) {
            // 删除 io/github/dong4j/coco 下指定的包中指定的版本 (mvn coco-assist:clear -Dname=xxx -Dversion=x.x.x)
            this.deleteByNameAndVersion(rootFile, artifactId, version);
        } else if (ALL_FLAG.equals(version) && StringUtils.isNotBlank(artifactId)) {
            // 删除 io/github/dong4j/coco 下指定的包中所有的版本 (mvn coco-assist:clear -Dname=xxx -Dversion=all)
            this.deleteByName(rootFile, artifactId);
        } else {
            throw new IllegalArgumentException("命名错误: \n" +
                                               "1. 删除 io/github/dong4j/coco 下所有的依赖\n" +
                                               "mvn coco-assist:clear -Dname=all -Dversion=all\n" +
                                               "2. 删除 io/github/dong4j/coco 下所有包中指定的版本\n" +
                                               "mvn coco-assist:clear -Dname=all -Dversion=x.x.x\n" +
                                               "3. 删除 io/github/dong4j/coco 下指定的包中指定的版本\n" +
                                               "mvn coco-assist:clear -Dname=xxx -Dversion=x.x.x\n" +
                                               "4. 删除 io/github/dong4j/coco 下指定的包中所有的版本\n" +
                                               "mvn coco-assist:clear -Dname=xxx -Dversion=all\n" +
                                               "5. 删除无效的目录和缓存\n" +
                                               "mvn coco-assist:clean");
        }
    }

    /**
     * 删除更新的缓存文件
     *
     * @param rootFile root file
     * @since 1.8.0
     */
    @Contract(pure = true)
    private void deleteErrorFile(File rootFile) {
        Collection<File> listFiles = org.apache.commons.io.FileUtils.listFiles(rootFile, ERROR_FILE, true);
        listFiles.forEach(f -> {
            try {
                Files.delete(f.toPath());
            } catch (IOException e) {
                this.getLog().info("delete: " + f);
            }
        });
    }

    /**
     * Delete by name and version
     *
     * @param rootFile   root file
     * @param artifactId artifact id
     * @param version    version
     * @since 1.7.1
     */
    private void deleteByNameAndVersion(File rootFile, String artifactId, String version) {
        Arrays.stream(Objects.requireNonNull(rootFile.listFiles())).filter(File::isDirectory).forEach(nameFile -> {
            if (match(artifactId, nameFile.getName())) {
                Arrays.stream(Objects.requireNonNull(nameFile.listFiles())).filter(File::isDirectory).forEach(versionFile -> {
                    if (match(version, versionFile.getName())) {
                        this.deleteFile(versionFile);
                    }
                });
            }
        });
    }

    /**
     * 删除指定的 version
     *
     * @param rootFile root file
     * @param names    names
     * @param version  version
     * @since 1.7.1
     */
    private void deleteByVersion(File rootFile, String[] names, String version) {
        Arrays.stream(Objects.requireNonNull(rootFile.listFiles()))
            .filter(File::isDirectory)
            .forEach(file -> Arrays.stream(names).forEach(nameFile -> {
                if (match(nameFile, file.getName())) {
                    File[] files = file.listFiles();
                    if (files != null && file.length() > 0) {
                        Arrays.stream(files).forEach(versionFile -> {
                            if (match(version, versionFile.getName())) {
                                this.deleteFile(versionFile);
                            }
                        });
                    }
                }
            }));

    }

    /**
     * 删除指定的模块
     *
     * @param rootFile   root file
     * @param artifactId 支持正则
     * @since 1.7.1
     */
    private void deleteByName(File rootFile, String artifactId) {
        Arrays.stream(Objects.requireNonNull(rootFile.listFiles())).filter(File::isDirectory).forEach(file -> {
            if (match(artifactId, file.getName())) {
                this.deleteFile(file);
            }
        });
    }

    /**
     * 删除 v5 的所有依赖
     *
     * @param rootFile root file
     * @param names    names
     * @param versions versions
     * @since 1.7.1
     */
    @SuppressWarnings("java:S3776")
    private void deleteAllFile(@NotNull File rootFile, String[] names, String[] versions) {
        if (names.length == 0 && versions.length != 0) {
            Arrays.stream(Objects.requireNonNull(rootFile.listFiles())).filter(File::isDirectory).forEach(nameFile -> {
                File[] files = nameFile.listFiles();
                if (files != null && files.length > 0) {
                    Arrays.stream(files).filter(File::isDirectory)
                        .forEach(versionFile -> Arrays.stream(versions)
                            .forEach(version -> {
                                if (match(version, versionFile.getName())) {
                                    this.deleteFile(versionFile);
                                }
                            }));
                }
            });
        } else if (versions.length == 0 && names.length != 0) {
            Arrays.stream(Objects.requireNonNull(rootFile.listFiles()))
                .filter(File::isDirectory)
                .forEach(nameFile ->
                             Arrays.stream(names).forEach(name -> {
                                 if (match(name,
                                           nameFile.getName())) {
                                     this.deleteFile(nameFile);
                                 }
                             }));
        }
    }

    /**
     * Delete file
     *
     * @param file file
     * @since 1.7.1
     */
    private void deleteFile(File file) {
        try {
            boolean b = FileUtils.deleteDir(file);
            if (b) {
                this.getLog().info("delete: " + file);
            }
        } catch (Exception ignored) {
            // nothing to do
        }
    }

    /**
     * 前缀匹配
     *
     * @param regex        regex
     * @param beTestString be test string
     * @return the boolean
     * @since 1.7.1
     */
    public static boolean match(String regex, String beTestString) {
        return beTestString.startsWith(regex);
    }

}
