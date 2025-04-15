package com.flower.spirit.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class BeanUtil{
	
	public static String json ="";
    /**
     * 大小写可以忽略
     * 下划线 _ 被忽略
     * NULL值和空字符串不会覆盖新值
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyPropertiesIgnoreCase(Object source, Object target) {
        Map<String, Field> sourceMap = CacheFieldMap.getFieldMap(source.getClass());
        CacheFieldMap.getFieldMap(target.getClass()).values().forEach((it) -> {
            Field field = sourceMap.get(it.getName().toLowerCase().replace("_", ""));
            if (field != null) {
                it.setAccessible(true);
                field.setAccessible(true);
                try {
                    //忽略null和空字符串
                    if(field.get(source)!=null)
                    it.set(target, field.get(source));
                } catch (IllegalAccessException e) {
                    
                }
            }
        });
        return (T) target;
    }
 

    
    private static class CacheFieldMap {
        private static Map<String, Map<String, Field>> cacheMap = new HashMap<>();
 
        private static Map<String, Field> getFieldMap(Class clazz) {
            Map<String, Field> result = cacheMap.get(clazz.getName());
            if (result == null) {
                synchronized (CacheFieldMap.class) {
                    if (result == null) {
                        Map<String, Field> fieldMap = new HashMap<>();
                        for (Field field : clazz.getDeclaredFields()) {
                            fieldMap.put(field.getName().toLowerCase().replace("_", ""), field);
                        }
                        cacheMap.put(clazz.getName(), fieldMap);
                        result = cacheMap.get(clazz.getName());
                    }
                }
            }
            return result;
        }
    }
    
    /**
     * 忽略大小写转换bean类型
     *
     * @param obj 转换的源对象
     * @param clz 目标对象
     * @return 转换后的对象
     */
    public static <T> T transferObjectIgnoreCase(Object obj, Class clz) {
      T result = null;
      try {
        if (obj != null && !obj.equals("")) {
          result = (T) clz.newInstance();
          //获取目标类的属性集合
          Map<String, Field> destPropertyMap = new HashMap<>();
          for (Field curField : clz.getDeclaredFields()) {
            destPropertyMap.put(curField.getName().toLowerCase(), curField);
          }
          //拷贝属性
          for (Field curField : obj.getClass().getDeclaredFields()) {
            Field targetField = destPropertyMap.get(curField.getName().toLowerCase());
            if (targetField != null) {
              targetField.setAccessible(true);
              curField.setAccessible(true);
              targetField.set(result, curField.get(obj));
            }
          }
        }
      } catch (Exception e1) {
        return null;
      }
      return result;
    }
    public static Object beanTobeanLowerCase(Object object, Class<?> cls) throws Exception {
        Object obj = cls.newInstance();
        if (object != null) {
            Class<?> clsOld = object.getClass();
            Field[] fieldsOld = clsOld.getDeclaredFields();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field fieldOld : fieldsOld) {
                String fieldNameOld = fieldOld.getName().replace("-", "");
                fieldNameOld = fieldNameOld.replace("", "");
                int modOld = fieldOld.getModifiers();
                if (Modifier.isPrivate(modOld) && !Modifier.isFinal(modOld)) {
                    fieldOld.setAccessible(true);
                    for (Field field : fields) {
                        String fieldName = field.getName().replace("-", "");
                        fieldName = field.getName().replace("", "");
                        int mod = field.getModifiers();
                        if (Modifier.isPrivate(mod) && !Modifier.isFinal(mod)) {
                            field.setAccessible(true);
                            if (fieldNameOld.equalsIgnoreCase(fieldName)) {
                                field.set(obj, fieldOld.get(object));
                            }
                        }
                    }
                }
            }
        }
        return obj;
    }
    


}