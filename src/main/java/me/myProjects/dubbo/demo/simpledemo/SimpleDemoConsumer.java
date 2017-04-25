package me.myProjects.dubbo.demo.simpledemo;

import me.myProjects.dubbo.service.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * this is a easiest dubbo demo
 */
@PropertySource(value = "classpath:application.properties")
@ImportResource(value = "classpath:simple/dubbo-consumer.xml")
public class SimpleDemoConsumer {

    private final DubboService dubboService;

    @Autowired
    public SimpleDemoConsumer(DubboService dubboService) {
        this.dubboService = dubboService;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext configApplicationContext =
                new AnnotationConfigApplicationContext(SimpleDemoConsumer.class);
        configApplicationContext.start();

        SimpleDemoConsumer consumer = configApplicationContext.getBean(SimpleDemoConsumer.class);
        System.out.println("start consumer success!");

        String value = consumer.dubboService.getJsonValue();
        System.out.println(value);
    }

}
