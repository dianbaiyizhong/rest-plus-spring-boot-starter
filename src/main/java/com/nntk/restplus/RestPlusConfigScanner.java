package com.nntk.restplus;

import com.nntk.restplus.annotation.RestPlus;
import com.nntk.restplus.aop.RestPlusAopProxyFactory;
import com.nntk.restplus.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 这种扫描方式已无效，不删掉是因为作为日后参考
 */
@Component
@Deprecated
public class RestPlusConfigScanner implements BeanDefinitionRegistryPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(RestPlusConfigScanner.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        run(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }


    private Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public void run(BeanDefinitionRegistry registry) {

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false) {
                    @Override
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        return super.isCandidateComponent(beanDefinition) || beanDefinition.getMetadata().isAbstract();
                    }
                };
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestPlus.class, true, true));
        // 获取springboot启动类的package，指定扫描特定package下的文件，这样可以加快启动速度
        String packageName = deduceMainApplicationClass().getPackage().getName();
        log.info("rest plus scan packages:{}", packageName);
        if (packageName.contains("junit")) {
            // 如果是单元测试，那就扫描全路径
            packageName = "com.nntk";
        }
        Set<? extends Class<?>> scanPackage = scanner.findCandidateComponents(packageName)
                .stream().map(BeanDefinition::getBeanClassName)
                .map(e -> {
                    try {
                        return Class.forName(e);
                    } catch (ClassNotFoundException aE) {
                        throw new RuntimeException(aE);
                    }
                }).collect(Collectors.toSet());


        for (Class<?> cls : scanPackage) {
            log.info("register:{}", cls);
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
            definition.setBeanClass(RestPlusAopProxyFactory.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            String beanName = cls.getSimpleName() + "RestPlusClient";
            registry.registerBeanDefinition(beanName, definition);
        }

        printLogo();
    }

    private void printLogo() {
        String logo = ResourceUtil.readStr("restplus.txt");
        System.out.println(logo);
        System.out.println(getColoredString(32, 2, "(v1.0.0)"));
    }


    public static String getColoredString(int color, int fontType, String content) {
        return String.format("\033[%d;%dm%s\033[0m", color, fontType, content);
    }


}
