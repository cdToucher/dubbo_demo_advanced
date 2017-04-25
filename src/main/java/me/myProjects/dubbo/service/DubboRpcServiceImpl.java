package me.myProjects.dubbo.service;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chendong on 2017/4/25.
 *
 * rpc service
 */
@Service("dubboRpcService")
public class DubboRpcServiceImpl implements DubboService {

    @Override
    public String getJsonValue() {
        Map<String, String> map = new HashMap<>();
        map.put("rpc", "this is rpc jsonValue.");
        return JSON.toJSONString(map);
    }
}
