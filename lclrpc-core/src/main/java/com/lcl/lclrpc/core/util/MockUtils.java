package com.lcl.lclrpc.core.util;

import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class MockUtils {
    public static Object mock(Class type) {
        if(type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return 1;
        } else if(type.equals(Long.class) || type.equals(Long.TYPE)) {
            return 10000L;
        } else if(Number.class.isAssignableFrom(type)) {
            return 1;
        } else if(type.equals(Float.class) || type.equals(Float.TYPE)) {
            return 1.0f;
        } else if(type.equals(Short.class) || type.equals(Short.TYPE)) {
            return (short) 1;
        } else if(type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return (byte) 1;
        } else if(type.equals(Character.class) || type.equals(Character.TYPE)) {
            return 'a';
        } else if(type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
            return true;
        } else if(type.equals(String.class)) {
            return "this is a mock string";
        }
        return mockPojo(type);

    }

    @SneakyThrows
    private static Object mockPojo(Class type) {
        Object result = type.getDeclaredConstructor().newInstance();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            field.set(result, mock(field.getType()));
        }
        return result;
    }

//    public static void main(String[] args) {
//        System.out.println(mock(UserDTO.class));
//    }
//
//    @Data
//    public static class UserDTO {
//        private String name;
//        private Integer age;
//
//        @Override
//        public String toString() {
//            return "UserDTO(name=" + this.getName() + ", age=" + this.getAge() + ")";
//        }
//
//    }
}
