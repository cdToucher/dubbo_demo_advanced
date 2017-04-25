package me.myProjects.dubbo.demo.restDemo;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * this is a easiest dubbo demo
 */
@PropertySource("classpath:application.properties")
@ImportResource("classpath:/simple/dubbo-provider.xml")
@ComponentScan("me.myProjects.dubbo.service")
public class RestDemoProvider {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext configApplicationContext =
                new AnnotationConfigApplicationContext(RestDemoProvider.class);
        configApplicationContext.start();

        System.out.println(" provider started!");
        System.out.println(System.in.read());
    }

}
