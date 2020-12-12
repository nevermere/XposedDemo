package com.android.xposed.hook;

import com.sky.xposed.javax.MethodHook;
import com.sky.xposed.javax.XposedPlus;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 描述：XposedUtil
 *
 * @author fzJiang
 * @date 2020-12-03 9:57.
 */
public class XposedUtil {

    public static void init(XC_LoadPackage.LoadPackageParam param, MethodHook.ThrowableCallback callback) {
        XposedPlus.setDefaultInstance(new XposedPlus.Builder(param).throwableCallback(callback).build());
    }

    public static MethodHook findMethod(Class<?> clazz, String methodName, Object... parameterTypes) {
        return XposedPlus.get().findMethod(clazz, methodName, parameterTypes);
    }

    public static MethodHook findMethod(String className, String methodName, Object... parameterTypes) {
        return XposedPlus.get().findMethod(className, methodName, parameterTypes);
    }

    public static MethodHook findConstructor(Class<?> clazz, Object... parameterTypes) {
        return XposedPlus.get().findConstructor(clazz, parameterTypes);
    }

    public static MethodHook findConstructor(String className, Object... parameterTypes) {
        return XposedPlus.get().findConstructor(className, parameterTypes);
    }

    public static Class<?> findClass(String className) {
        return XposedPlus.get().findClass(className);
    }

    public static Object callMethod(Class<?> clazz, String methodName, Object... args) {
        return (Object) XposedHelpers.callMethod(clazz, methodName, args);
    }

    public static Object callStaticMethod(String className, String methodName, Object... args) {
        return (Object) XposedHelpers.callStaticMethod(XposedUtil.findClass(className), methodName, args);
    }

    public static Object callStaticMethod(Class<?> clazz, String methodName, Object... args) {
        return (Object) XposedHelpers.callStaticMethod(clazz, methodName, args);
    }
}
