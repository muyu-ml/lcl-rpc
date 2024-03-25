package com.lcl.lclrpc.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class ScanPackageUtils {
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public static List<Class<?>> scanPackage(String[] basePackages, Predicate<Class<?>> predicate) {
        List<Class<?>> result = new ArrayList<>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        for (String basePackage : basePackages) {
            if(StringUtils.isBlank(basePackage)){
                continue;
            }
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage)) + "/" + DEFAULT_RESOURCE_PATTERN;
            try {
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        String className = metadataReader.getClassMetadata().getClassName();
                        Class<?> aClass = Class.forName(className);
                        if (predicate.test(aClass)) {
                            result.add(aClass);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String packages = "com.lcl.lclrpc.core";
        log.info("scan all class for package:{}", packages);
        List<Class<?>> classes = scanPackage(packages.split(","), aClass -> true);
        classes.forEach(aClass -> log.info("class:{}", aClass));
        log.info("");
        log.info("");
        log.info("scan all class with @Configuration for packages:{} ", packages);
        scanPackage(packages.split(","), aClass -> aClass.isAnnotationPresent(Configuration.class))
                .forEach(aClass -> log.info("class:{}", aClass));
    }
}
