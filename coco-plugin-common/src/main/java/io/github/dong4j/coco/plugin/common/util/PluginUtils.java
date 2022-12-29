package io.github.dong4j.coco.plugin.common.util;

import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.NotNull;

import io.github.dong4j.coco.plugin.common.Plugins;
import io.github.dong4j.coco.plugin.common.enums.ModuleType;
import lombok.experimental.UtilityClass;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.05.01 20:15
 * @since 1.0.0
 */
@UtilityClass
public class PluginUtils {

    /**
     * Module type
     *
     * @return the module type
     * @since 1.5.0
     */
    public static ModuleType moduleType() {
        String moduleType = System.getProperty(Plugins.MODULE_TYPE);
        return EnumUtils.getEnum(ModuleType.class, moduleType);
    }

    /**
     * Module type
     *
     * @param type type
     * @since 1.5.0
     */
    public static void moduleType(@NotNull ModuleType type) {
        System.setProperty(Plugins.MODULE_TYPE, type.name());
    }
}
