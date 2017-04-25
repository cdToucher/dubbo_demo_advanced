package me.myProjects.dubbo.demo.javaConfigDemo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * java config demo
 */
@Configuration
@PropertySource(value = "classpath:application.properties")
@ComponentScans({@ComponentScan("me.myProjects.dubbo.demo.javaConfigDemo"), @ComponentScan("me.myProjects.dubbo.restService")})
public class DubboDemoConsumerConfig {

    @Value("${application.name}")
    private String APPLICATION_NAME;

    @Value("${zk.address}")
    private String REGISTRY_ADDRESS;

    private final static String ANNOTATION_PACKAGE = "me.myProjects.dubbo.javaConfigDemo";


    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(APPLICATION_NAME);
        return applicationConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(REGISTRY_ADDRESS);
        return registryConfig;
    }

    @Bean
    public AnnotationBean annotationBean() {
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(ANNOTATION_PACKAGE);
        return annotationBean;
    }

}
