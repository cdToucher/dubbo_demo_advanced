package me.myProjects.dubbo.demo.simpledemo;

import org.springframework.context.annotation.*;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * this is a easiest dubbo demo
 */
@PropertySource("classpath:application.properties")
@ImportResource("classpath:simple/dubbo-provider.xml")
@ComponentScan("me.myProjects.dubbo.service")
public class SimpleDemoProvider {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext configApplicationContext =
                new AnnotationConfigApplicationContext(SimpleDemoProvider.class);
        configApplicationContext.start();

        System.out.println("start provider success!");
    }

}
