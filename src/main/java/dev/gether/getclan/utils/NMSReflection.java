package dev.gether.getclan.utils;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSReflection {

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    public static void setValue(Object packet, String fieldName, Object value) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(packet, value);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static Method getMethod(Class<?> clazz, String nameMethod) {
        try {
            return clazz.getMethod(nameMethod);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String nameField) {
        try {
            return clazz.getField(nameField );
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getServerVersion() {
        return org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static Object getObject(Object object, Method getHandle) {
        try {
            return getHandle.invoke(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

}
