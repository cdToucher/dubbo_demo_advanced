package me.myProjects.dubbo.demo.javaConfigDemo;

import com.alibaba.dubbo.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * 使用 API 直接编程
 */
@Component
public class Consumer {

    private final ApplicationConfig config;

    @Autowired
    public Consumer(ApplicationConfig config) {
        this.config = config;
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext configApplicationContext =
                new AnnotationConfigApplicationContext(DubboDemoConsumerConfig.class);
        configApplicationContext.start();

        ApplicationConfig config = configApplicationContext.getBean(Consumer.class).config;
        System.out.println(config.getRegistry().getAddress());
        System.out.println("start consumer success!");
        System.out.println(System.in.read());
    }
}