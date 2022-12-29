# 简介

框架配套的 Maven Plugin 项目, 提供多个插件简化开发, 部署等流程, 并通过一些约束强制规定代码的写法, 提高可维护性.

此模块只是为 `coco-framework-build` 项目提供插件, 业务端不需要引入.

## 构建

```
mvn clean install
```

## 部署

```
mvn clean deploy
```

## 项目结构

```
└── coco-plugin                    # Maven 插件
   ├── coco-plugin-common          # 插件项目基础模块， 提供开发插件的工具包
   ├── coco-assist-maven-plugin    # 框架开发辅助插件
   ├── coco-checkstyle-plugin-rule # 代码格式检查插件
   ├── coco-enforcer-plugin-rule   # 项目依赖检查插件
   ├── coco-publish-maven-plugin   # 后端, 前端, doc, v4 springboot 服务发布插件
   ├── coco-script-maven-plugin    # 启动脚本生成插件， 可根据参数生成项目特定的启动脚本
   └── coco-makeself-maven-plugin  # 项目启动脚本优化插件， 可在 coco-script-maven-plugin 的基础上生成可直接运行的部署包
```

### coco-assist-maven-plugin

项目辅助插件, 主要提供:

1. DeleteMavenDependenceMojo: 删除 maven 依赖(解决每次都需要进入指定目录手动删除多次), 清理 maven 依赖缓存 (解决下载不了依赖的问题);
2. DeleteTempFileMojo: 删除 checkstyle 和 pmd 在 validate 阶段生成的临时文件;
3. GenerateAssemblyConfigFileMojo: 动态生成 assembly.xml 文件, 避免每个项目都需要添加相同的 assembly.xml 文件;
4. GenerateProjectBuildInfoMojo: 用于生成 build-info.properties;
5. SkipPluginMojo: 根据当前模块的类型自动忽略部分插件, 提高编译速度;
6. SpringProfilesActivePropertyMojo: 在 validate 阶段将 maven 的 profile 写入到指定文件 (psark/plugin/spring.profiles.active), 在应用启动时获取此配置;
7. StarterMainClassPropertyMojo: 可部署包的启动主类解析, 用于生成 start.class 配置, 减少 pom 配置, 降低失误的可能性;
8. TimestampPropertyMojo: 会自动注入 `current.time` 环境配置, 解决部分插件不支持自定义时间戳的问题;
9. DeployFileMojo: 一键上传第三方 jar 到公司 maven 私服, 提升效率;

### coco-checkstyle-plugin-rule

根据公司开发规范自定义的代码插件规则.


项目依赖检查插件, 可通过自定义逻辑检查项目是否存在依赖冲突以及出现依赖冲突后是否快速失败等(目前还没有实现).

### coco-publish-maven-plugin

一键部署插件, 可实现后端, 前端和项目知识库的一键部署.

### coco-script-maven-plugin

生成通用的启动脚本, 如果项目需要定制启动脚本, 可将自定义脚本添加到与 src 同级的 bin 目录下, 且脚本名为 `server.sh`;

### coco-makeself-maven-plugin

用于生成「自包含」的可执行运行的部署文件, 用于在线上部署时减少「解压文件, 修改部署目录用户组, 进入部署目录, 启动服务」等步骤 (windows 还需要类 linux 环境才能使用).

使用方式(打包生成环境部署包):

```bash
mvn clean package -U -Dpackage.env.prod=true -DskipTests -Dcheckstyle.skip=true -Dpmd.skip=true -Dmakeself.skip=false
```

## Todo list

- [X] 删除 checkstyle 和 pmd 临时文件
- [ ] 编译成功后自动生成一个标识文件, 使用 coco.kit.commit 检查这个文件, 没有则不能提交
- [ ] 使用插件动态添加依赖
- [X] 删除启动类检查的代码, 迁移到 coco-starter-processor
- [X] 通用启动脚本, 自动解压 zip 并执行 bin/server.sh 脚本 (需要切换用户到 coco)
- [X] 通用启动脚本可配置 JVM 参数, 使用 maven plugin 自定义
