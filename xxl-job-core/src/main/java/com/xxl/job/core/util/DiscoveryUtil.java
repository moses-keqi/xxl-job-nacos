package com.xxl.job.core.util;

import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;

import java.util.*;



/**
 * JOB ADMIN CONSOLE SERVICE LIST
 * @author HanKeQi
 * @Description
 * @date 2020/4/13 1:01 PM
 **/
public class DiscoveryUtil {

    private static Logger logger = LoggerFactory.getLogger(DiscoveryUtil.class);

    public static Map<String, List<String>> adminServicesList = new HashMap<>();

    private static NacosDiscoveryClient nacosDiscoveryClient;

    public DiscoveryUtil(NacosDiscoveryClient nacosDiscoveryClient) {
        this.nacosDiscoveryClient = nacosDiscoveryClient;
    }

    /**
     * add JOB ADMIN service list
     * @param name 名称
     */
    public static void addList(String name) {

        List<ServiceInstance> servicesByDiscovery = getServicesByDiscovery(name);
        if (!CollectionUtils.isEmpty(servicesByDiscovery)) {
            List<String> serviceList = new ArrayList<>();
            servicesByDiscovery.forEach(instanceInfo -> {
                //String service = instanceInfo.getHost() + ":" + instanceInfo.getPort();
                String service = instanceInfo.getServiceId();
                serviceList.add(service);
            });
            adminServicesList.put(name, serviceList);
            logger.info("registry scheduled success : {}", name);
        }
    }

    /**
     * 获取注册中心中指定注册名称的所有服务实例
     * 支持eureka nacos
     *
     * @param appName 应用实例名
     * @return List<InstanceInfo>
     */
    public static List<ServiceInstance> getServicesByDiscovery(String appName) {
        List<ServiceInstance> instances = null;
        try {
            instances = nacosDiscoveryClient.getInstances(appName);
        } catch (Exception e){
            logger.error("service first discovery fail : {}", e.toString());
        }

        return instances;
    }

    /**
     * Determines if the remote host already exists
     * @param host admin console address
     * @return true | false
     */
    public static boolean hostExist(String host){
        for (String admin : adminServicesList.keySet()) {
            List<String> urls = adminServicesList.get(admin);

            List<String> ipList = new ArrayList<>(3);
            for (String url : urls) {
                String[] split = url.split(":");
                ipList.add(split[0]);
            }

            if (ipList.contains(host)) {
                return true;
            }
        }
        return false;
    }

}
