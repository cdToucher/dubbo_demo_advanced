package me.myProjects.dubbo.demo.javaConfigDemo;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.RegistryFactory;
import com.alibaba.dubbo.rpc.service.GenericService;
import me.myProjects.dubbo.service.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * 直接使用 建议使用xml 格式，代码不入侵 配置方便
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
/*
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
        Registry registry = registryFactory.getRegistry(URL.valueOf("zookeeper://10.20.153.10:2181"));
        registry.register(URL.valueOf("memcached://10.20.153.11/com.foo.BarService?category=providers&dynamic=false&application=foo"));*/

        ApplicationConfig config = configApplicationContext.getBean(Consumer.class).config;
        RegistryConfig registryConfig = configApplicationContext.getBean(RegistryConfig.class);

        // 泛化引用
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(config);
        reference.setRegistry(registryConfig); // 多个注册中心可以用setRegistries()
        reference.setInterface(DubboService.class);
        reference.setVersion("1.0.0");
        // 和本地bean一样使用xxxService
        DubboService service = (DubboService)reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用

        System.out.println(config.getRegistry().getAddress());
        System.out.println("start consumer success!");
        System.out.println(System.in.read());
    }
}