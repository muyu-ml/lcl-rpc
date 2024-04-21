package com.lcl.lclrpc.core.config;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Data
@Slf4j
public class ApolloChangeListener implements ApplicationContextAware {
    ApplicationContext applicationContext;

    @ApolloConfigChangeListener({"application","shuzhi.customer123","shuzhi.provider123"})
    private void changeHander(ConfigChangeEvent changeEvent) {
        for(String key : changeEvent.changedKeys()) {
            ConfigChange change = changeEvent.getChange(key);
            log.info("Apollo配置发生变化，变化的key为：{}，内容为：{}", key, change.toString());
        }

        applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));

    }
}
