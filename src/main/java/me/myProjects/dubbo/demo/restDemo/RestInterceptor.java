package me.myProjects.dubbo.demo.restDemo;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by chendong on 2017/4/25.
 */
@Provider
@Component
public class RestInterceptor implements ContainerRequestFilter {

    @Context // 在 service 中不支持此 注释
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println(requestContext.getCookies());
    }
}