package me.myProjects.dubbo.demo.simpledemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Created by chendong on 2017/4/25.
 */
@Configuration
@PropertySource(value = "classpath:application.properties")
@ComponentScans({@ComponentScan("me.myProjects.dubbo.demo.simpledemo")
        , @ComponentScan("me.myProjects.dubbo.service")})
public class ApplicationConfig {

    private final Environment environment;

    @Value("${zk.address}")
    private String address;

    @Autowired
    public ApplicationConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        System.out.println(address);
        System.out.println(environment.getProperty("dubbo.protocol.port"));
        System.out.println(Arrays.toString(environment.getActiveProfiles()));
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
