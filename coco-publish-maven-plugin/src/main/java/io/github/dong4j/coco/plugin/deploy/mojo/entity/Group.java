package io.github.dong4j.coco.plugin.deploy.mojo.entity;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.Set;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.20 14:55
 * @since 1.6.0
 */
@Data
public class Group {

    /** Env */
    @Parameter
    private String env;
    /** 是否启用当前 group */
    @Parameter
    private boolean enable;
    /** Servers */
    @Parameter
    private Set<Server> servers;
}
