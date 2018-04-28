package com.hxw.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;

/**
 * @author hxw on 2018/4/28.
 */
public class PermissionUtils {

    /**
     * 检查权限
     * 有权限: PackageManager.PERMISSION_GRANTED
     * 无权限: PackageManager.PERMISSION_DENIED
     *
     * @param context 上下文
     * @param perms   权限
     * @return 是否有权限 {@code true} 有权限 {@code false} 无权限
     */
    public static boolean hasPermissions(@NonNull Context context,
                                         @Size(min = 1) @NonNull String... perms) {
        //版本6.0以下不需要请求权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        //是否有权限
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
