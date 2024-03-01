package com.github.apengda.springwebplus.starter.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.apengda.springwebplus.starter.auth.LoginInterceptor;
import com.github.apengda.springwebplus.starter.config.support.CurrentUserInfoParamResolver;
import com.github.apengda.springwebplus.starter.config.support.HolderClearInterceptor;
import com.github.apengda.springwebplus.starter.config.support.PageRequestParamResolver;
import com.github.apengda.springwebplus.starter.config.support.WebappFile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final SysConfigProperties configProperties;
    private final LoginInterceptor loginInterceptor;

    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageRequestParamResolver());
        resolvers.add(new CurrentUserInfoParamResolver());
    }


    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/api/**", "/*/api/**");
        registry.addInterceptor(new HolderClearInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "file:" + configProperties.getStaticPath().trim();
        if (!path.endsWith("/")) {
            path += "/";
        }
        fileResourceMap(registry, "", path);
        // 处理子应用
        webappSubPathProcess(path, registry);
    }

    /**
     * 处理前端子应用的情况
     *
     * @param rootPath
     * @param registry
     */
    protected void webappSubPathProcess(final String rootPath, final ResourceHandlerRegistry registry) {
        if (!FileUtil.exist(configProperties.getStaticPath())) {
            return;
        }
        final List<File> fs = FileUtil.loopFiles(FileUtil.newFile(configProperties.getStaticPath()), 1, null);
        if (CollUtil.isEmpty(fs)) {
            return;
        }
        for (final File f : fs) {
            if (f.isDirectory() && !WebappFile.FileNames.contains(f.getName())) {
                final String subAppName = f.getName();
                log.info("add sub  app:{}", subAppName);
                WebappFile.SubApps.add(subAppName);
                final String filePath = rootPath + subAppName + "/";
                fileResourceMap(registry, subAppName, filePath);
            }
        }
    }

    /**
     * 静态资源请求映射
     *
     * @param registry
     * @param app
     * @param filePath
     */
    private void fileResourceMap(final ResourceHandlerRegistry registry, final String app, final String filePath) {
        registry.addResourceHandler(app + "/index.html")
                .addResourceLocations(filePath + "index.html")
                .setCacheControl(CacheControl.noStore());

        registry.addResourceHandler(app)
                .addResourceLocations(filePath + "index.html")
                .setCacheControl(CacheControl.noStore());

        registry.addResourceHandler(app + "/favicon.ico")
                .addResourceLocations(filePath + "favicon.ico")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        registry.addResourceHandler(app + "/assets/**")
                .addResourceLocations(filePath + "assets/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        registry.addResourceHandler(app + "/static/**")
                .addResourceLocations(filePath + "static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(1)));

        registry.addResourceHandler(app + "/public/**")
                .addResourceLocations(filePath + "public/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(1)));
    }
}