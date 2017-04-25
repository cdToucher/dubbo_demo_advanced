package me.myProjects.dubbo.demo.simpledemo;

import org.springframework.context.annotation.*;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * this is a easiest dubbo demo
 */
@Configuration
@ImportResource("classpath:/simple/dubbo-provider.xml")
public class SimpleDemoProvider {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext configApplicationContext =
                new AnnotationConfigApplicationContext(ApplicationConfig.class);
        configApplicationContext.start();

        System.out.println(" provider started!");
        System.out.println(System.in.read());
    }

}
