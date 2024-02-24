package com.github.apengda.springbootplus.core.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.apengda.springbootplus.core.config.support.CurrentUserInfoParamResolver;
import com.github.apengda.springbootplus.core.config.support.PageRequestParamResolver;
import com.github.apengda.springbootplus.core.config.support.WebappFile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
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
    private final PageRequestParamResolver pageRequestParamResolver;
    private final CurrentUserInfoParamResolver userInfoParamResolver;

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
        resolvers.add(pageRequestParamResolver);
        resolvers.add(userInfoParamResolver);
    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        if(interceptorRegistryListeners == null){
//            return;
//        }
//        for(InterceptorRegistryListener listener: interceptorRegistryListeners){
//            listener.addInterceptors(registry);
//        }
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "file:" + configProperties.getStaticPath().trim();
        if (!path.endsWith("/")) {
            path += "/";
        }
        registry.addResourceHandler("/index.html")
                .addResourceLocations(path + "index.html")
                .setCacheControl(CacheControl.noStore());

        registry.addResourceHandler("/data.html")
                .addResourceLocations(path + "data.html")
                .setCacheControl(CacheControl.noStore());

        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations(path + "favicon.ico")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        registry.addResourceHandler("/assets/**")
                .addResourceLocations(path + "assets/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        registry.addResourceHandler("/static/**")
                .addResourceLocations(path + "static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(1)));

        registry.addResourceHandler("/public/**")
                .addResourceLocations(path + "public/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(1)));

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
                addWebappResource(f.getName(), registry, rootPath);
            }
        }
    }

    protected void addWebappResource(final String subAppName,
                                     final ResourceHandlerRegistry registry,
                                     final String webapp) {
        WebappFile.SubApps.add(subAppName);
        log.info("add sub  app:{}", subAppName);

        registry.addResourceHandler("/" + subAppName + "/*.html")
                .addResourceLocations(webapp + subAppName + "/")
                .setCacheControl(CacheControl.noStore());

        registry.addResourceHandler("/" + subAppName + "/assets/**")
                .addResourceLocations(webapp + subAppName + "/assets/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        registry.addResourceHandler("/" + subAppName + "/static/**")
                .addResourceLocations(webapp + subAppName + "/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(1)));

        registry.addResourceHandler("/" + subAppName + "/public/**")
                .addResourceLocations(webapp + subAppName + "/public/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(1)));
    }
}