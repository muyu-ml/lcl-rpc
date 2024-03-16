package com.lcl.lclrpc.core.util;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

public class TypeUtils {

    /**
     * 类型转换
     * @param origin
     * @param type
     * @return
     */
    public static Object cast(Object origin, Class<?> type) {
        if(origin == null){
            return null;
        }
        Class<?> aClass = origin.getClass();
        if(type.isAssignableFrom(aClass)){
            return origin;
        }

        if(origin instanceof HashMap map){
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        if(type.isArray()){
            if(origin instanceof List list){
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(resultArray, i, Array.get(origin, i));
            }
            return resultArray;
        }

        if(type == Integer.class || type == int.class){
            return Integer.parseInt(origin.toString());
        }
        if(type == Long.class || type == long.class){
            return Long.parseLong(origin.toString());
        }
        if(type == Double.class || type == double.class){
            return Double.parseDouble(origin.toString());
        }
        if(type == Float.class || type == float.class){
            return Float.parseFloat(origin.toString());
        }
        if(type == Short.class || type == short.class){
            return Short.parseShort(origin.toString());
        }
        if(type == Byte.class || type == byte.class){
            return Byte.parseByte(origin.toString());
        }
        if(type == Boolean.class || type == boolean.class){
            return Boolean.parseBoolean(origin.toString());
        }
        if(type == Character.class || type == char.class){
            return origin.toString().charAt(0);
        }
        if(type == String.class){
            return origin.toString();
        }

        return null;
    }
}
