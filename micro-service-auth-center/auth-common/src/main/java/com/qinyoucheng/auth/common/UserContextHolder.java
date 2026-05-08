package com.qinyoucheng.auth.common;

public class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    public static UserContext get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static Long getUserId() {
        UserContext context = get();
        return context != null ? context.getUserId() : null;
    }

    public static String getUsername() {
        UserContext context = get();
        return context != null ? context.getUsername() : null;
    }

    public static String getAppKey() {
        UserContext context = get();
        return context != null ? context.getAppKey() : null;
    }
}
