package com.lcl.lclrpc.core.cluster;

import com.lcl.lclrpc.core.api.Router;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 灰度路由
 */
@Slf4j
@Data
public class GrayRouter implements Router<InstanceMeta> {

    private int grayRatio;
    private final Random random = new Random();

    public GrayRouter(int grayRatio){
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {

        // 只有一个节点，直接返回
        if(providers == null || providers.size() == 1){
            return providers;
        }

        List<InstanceMeta> normalNodes = new ArrayList<>();
        List<InstanceMeta> grayNodes = new ArrayList<>();

        providers.stream().forEach( p -> {
            if("true".equals(p.getParameters().get("gray"))){
                grayNodes.add(p);
            }else {
                normalNodes.add(p);
            }
        });

        log.debug("grayRouter normalNodes: {}, grayNodes: {}, grayRatio：{}", normalNodes.size(), grayNodes.size(), grayRatio);

        // 如果正常节点没有服务提供，则返回灰度节点，反之亦然
        if(normalNodes.isEmpty() || grayNodes.isEmpty()){
            return providers;
        }

        // 如果灰度小于等于0，则返回正常节点；如果灰度大于等于100，则返回灰度节点
        if(grayRatio <= 0){
            return normalNodes;
        } else if(grayRatio >= 100) {
            return grayNodes;
        }

        // 如果灰度在0-100之间，则按照灰度比例返回
        // 有两种方式：
        //      方式一：虚拟节点，但是有个前提，LB的算法必须是均匀分布的（线性均匀的），不太常用
        //      方式二：按照权重
        if(random.nextInt(100) < grayRatio){
            log.debug("grayRouter grayNodes ====>>>  {},", normalNodes);
            return grayNodes;
        }else {
            log.debug("grayRouter normalNodes ====>>>  {}", normalNodes);
            return normalNodes;
        }
    }
}
