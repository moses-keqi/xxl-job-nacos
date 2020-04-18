package com.xxl.job.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HanKeQi
 * @Description
 * @date 2020/4/13 1:01 PM
 **/
public class SerializeUtil {
    private static Logger logger = LoggerFactory.getLogger(SerializeUtil.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> byte[] serialize(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            logger.error("jackson param serialize format error : {}", e.toString());
        }
        return null;
    }

    public static <T> Object deserialize(byte[] bytes, Class<T> clazz)  {
        try {
            Object t = objectMapper.readValue(bytes, clazz);
            return t;
        } catch (Exception e) {
            logger.error("jackson param deserialize format error : {}", e.toString());
        }
        return null;
    }
}
