package com.xxl.job.core.util;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.config.ApplicationContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @Author HanKeQi
 * @Date 2020/4/15 11:58 上午
 * @Version 1.0
 **/
public class RestTemplateClient<T> {

    // 执行器执行接口
    public final static String executorApi = "/v1/executor/api";

    public final static String adminList = "/v1/executor/list";

    public final static String callBackApi = AdminBiz.MAPPING;

    public static <T> ReturnT<String> getPostObject(String url, Map<?, ?> params){
        HttpHeaders headers = new HttpHeaders(); // http请求头
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8); // 请求头设置属性
        HttpEntity<Map<?, ?>> requestEntity = new HttpEntity<>(params,headers);
        RestTemplate restTemplate = getRestTemplateNacosJob();
        ReturnT<String> returnT = restTemplate.postForObject(url, requestEntity, ReturnT.class);
        return returnT;
    }



    public static <T> ReturnT<String> getPost(String url, Map<?, ?> params){
        HttpEntity<Map<?,?>> requestEntity = null;
        if (!CollectionUtils.isEmpty(params)){
            requestEntity = new HttpEntity<>(params);
        }

        RestTemplate restTemplate = getRestTemplateNacosJob();
        ReturnT<String> returnT = restTemplate.postForObject(url, requestEntity, ReturnT.class);
        return returnT;
    }

    public static <T> ReturnT<String> getPostList(String url, List<?> list){
        HttpHeaders headers = new HttpHeaders(); // http请求头
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8); // 请求头设置属性
        HttpEntity<List<?>> requestEntity = new HttpEntity<>(list,headers);
        RestTemplate restTemplate = getRestTemplateNacosJob();
        ReturnT<String> returnT = restTemplate.postForObject(url, requestEntity, ReturnT.class);
        return returnT;
    }

    /**
     * 必须使用
     * sel com.xxl.job.core.config.JobConfiguration
     * @return
     */
    private static RestTemplate getRestTemplateNacosJob(){
        RestTemplate restTemplate = (RestTemplate)ApplicationContextHolder.getBean("restTemplateNacosJob");
        return restTemplate;
    }



}
