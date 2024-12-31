# spring-web项目脚手架

todo
https://sa-token.cc/doc.html#/more/link

todo
https://jtablesaw.github.io/tablesaw/gettingstarted

## 功能特性

1. web配置、mybatis-plus配置、缓存配置
2. 基础用户、角色、菜单管理
3. 基础工具代码
4. 实体Entity自动生成表结构
5. sql语句数据分页子查询支持
6. 本地文件、OSS、minio文件系统支持
7. 前端文件history模式支持，前端文件放到webapp文件夹下即可。

## 如何使用

1. git clone https://github.com/pdaodao/spring-web-plus.git
2. mvn clean install
3. 引入依赖

```
<dependency>
    <groupId>com.github.pdaodao</groupId>
    <artifactId>spring-web-plus-starter</artifactId>
    <version>3.0</version>
</dependency>
```

参考
https://github.com/geekidea/spring-boot-plus

    @Parameters({
            @Parameter(name = "id",description = "文件id",in = ParameterIn.PATH),
            @Parameter(name = "file",description = "文件",required = true,in=ParameterIn.DEFAULT,
                    schema = @Schema(name = "file",format = "binary")),
            @Parameter(name = "name",description = "文件名称",required = true),
    })