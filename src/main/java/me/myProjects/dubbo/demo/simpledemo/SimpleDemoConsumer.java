package me.myProjects.dubbo.demo.simpledemo;

import me.myProjects.dubbo.service.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * this is a easiest dubbo demo
 */
@PropertySource("classpath:application.properties")
@ImportResource("classpath:simple/dubbo-consumer.xml")
@ComponentScan("me.myProjects.dubbo.demo")
public class SimpleDemoConsumer {

    private final DubboService dubboService;

    @Autowired
    public SimpleDemoConsumer(DubboService dubboService) {
        this.dubboService = dubboService;
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext configApplicationContext =
                new AnnotationConfigApplicationContext(SimpleDemoConsumer.class);
        configApplicationContext.start();

        System.out.println("start consumer success!");

        String value = configApplicationContext.getBean(SimpleDemoConsumer.class).dubboService.getJsonValue();
        System.out.println(value);
    }

}
