package me.myProjects.dubbo.demo.localDemo;

import me.myProjects.dubbo.service.DubboService;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/26.
 */
@PropertySource(value = "classpath:application.properties")
@ImportResource(value = "classpath:localDemo/dubbo-local.xml")
@ComponentScan("me.myProjects.dubbo.service")
public class LocalTest {

    @Bean
    public static PropertySourcesPlaceholderConfigurer getConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext(LocalTest.class);
        configApplicationContext.start();

        DubboService service = configApplicationContext.getBean(DubboService.class);
        System.out.println(service.getJsonValue());

        System.in.read();
    }

}
