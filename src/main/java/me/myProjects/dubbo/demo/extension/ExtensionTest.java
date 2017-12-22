package me.myProjects.dubbo.demo.extension;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.container.Container;

/**
 * Created by chendong on 2017/12/22.
 *
 *
 * haven't find a right way to replay the rest protocol yet
 */
public class ExtensionTest {

    public static void main(String[] args) {
        ExtensionLoader<Container> extensionLoader = ExtensionLoader.getExtensionLoader(Container.class);
        extensionLoader.getExtension("rest");
    }
}
