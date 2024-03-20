package com.lcl.lclrpc.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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


    @Nullable
    public static Object castMethodReturnType(Method method, Object data) {

        Class<?> type = method.getReturnType();
        System.out.println("method.getReturnType() = " + type);
        if (data instanceof JSONObject jsonResult) {
            if (Map.class.isAssignableFrom(type)) {
                Map resultMap = new HashMap();
                Type genericReturnType = method.getGenericReturnType();
                System.out.println(genericReturnType);
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Class<?> keyType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
                    Class<?> valueType = (Class<?>)parameterizedType.getActualTypeArguments()[1];
                    System.out.println("keyType  : " + keyType);
                    System.out.println("valueType: " + valueType);
                    jsonResult.entrySet().stream().forEach(
                            e -> {
                                Object key = cast(e.getKey(), keyType);
                                Object value = cast(e.getValue(), valueType);
                                resultMap.put(key, value);
                            }
                    );
                }
                return resultMap;
            }
            return jsonResult.toJavaObject(type);
        } else if (data instanceof JSONArray jsonArray) {
            Object[] array = jsonArray.toArray();
            if (type.isArray()) {
                Class<?> componentType = type.getComponentType();
                Object resultArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                        Array.set(resultArray, i, array[i]);
                    } else {
                        Object castObject = cast(array[i], componentType);
                        Array.set(resultArray, i, castObject);
                    }
                }
                return resultArray;
            } else if (List.class.isAssignableFrom(type)) {
                List<Object> resultList = new ArrayList<>(array.length);
                Type genericReturnType = method.getGenericReturnType();
                System.out.println(genericReturnType);
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    System.out.println(actualType);
                    for (Object o : array) {
                        resultList.add(cast(o, (Class<?>) actualType));
                    }
                } else {
                    resultList.addAll(Arrays.asList(array));
                }
                return resultList;
            } else {
                return null;
            }
        } else {
            return cast(data, type);
        }
    }
}
