package me.myProjects.dubbo.demo.restDemo;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * this is a easiest dubbo demo
 */
@PropertySource(value = "classpath:application.properties")
@ImportResource(value = "classpath:rest/dubbo-consumer.xml")
@ComponentScans({@ComponentScan("me.myProjects.dubbo.restDemo")
        , @ComponentScan("me.myProjects.dubbo.restService")})
public class RestDemoConsumer {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext configApplicationContext =
                new AnnotationConfigApplicationContext(RestDemoConsumer.class);
        configApplicationContext.start();

        System.out.println("start consumer success!");
        System.out.println(System.in.read());
    }
}
