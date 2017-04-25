package me.myProjects.dubbo.restService;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chendong on 2017/4/25.
 * <p>
 * rpc service
 */
@Service("restService")
@Path("/")
public class DubboRestServiceImpl implements DubboService {

    @GET
    @Path("test")
    @Produces({"application/json;charset=utf-8"})
    @Override
    public String getJsonValue() {
        Map<String, String> map = new HashMap<>();
        map.put("rpc", "this is rpc jsonValue.");
        return JSON.toJSONString(map);
    }
}
