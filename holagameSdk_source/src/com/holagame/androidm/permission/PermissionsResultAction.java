package com.holagame.androidm.permission;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * 权限请求回调
 * @author zoulong
 */
public abstract class PermissionsResultAction {

    private static final String TAG = PermissionsResultAction.class.getSimpleName();
    private final Set<String> mPermissions = new HashSet<String>(1);
    private Looper mLooper = Looper.getMainLooper();
    public PermissionsResultAction() {}

    @SuppressWarnings("unused")
    public PermissionsResultAction(@NonNull Looper looper) {mLooper = looper;}

    /**
     * 获取到权限
     */
    public abstract void onGranted();

    /**
     *获取权限失败，或者说是被用户拒绝的时候
     * @param 失败权限的名称
     */
    public abstract void onDenied(String permission);

    @SuppressWarnings("WeakerAccess")
    @CallSuper
    protected synchronized final boolean onResult(final @NonNull String permission, int result) {
        if (result == PackageManager.PERMISSION_GRANTED) {
            return onResult(permission, Permissions.GRANTED);
        } else {
            return onResult(permission, Permissions.DENIED);
        }

    }

    /**
     *回调接口，回调给当前的PermissionsResultAction，经过处理后回调给界面，
     * @param permission 发生改变的权限接口
     * @param result     返回结果
     * @return 回调完成返回true否则返回false
     */
    @SuppressWarnings("WeakerAccess")
    @CallSuper
    protected synchronized final boolean onResult(final @NonNull String permission, Permissions result) {
        mPermissions.remove(permission);
        if (result == Permissions.GRANTED) {
            if (mPermissions.isEmpty()) {
                new Handler(mLooper).post(new Runnable() {
                    @Override
                    public void run() {
                        onGranted();
                    }
                });
                return true;
            }
        } else if (result == Permissions.DENIED) {
            new Handler(mLooper).post(new Runnable() {
                @Override
                public void run() {
                    onDenied(permission);
                }
            });
            return true;
        } else if (result == Permissions.NOT_FOUND) {
                if (mPermissions.isEmpty()) {
                    new Handler(mLooper).post(new Runnable() {
                        @Override
                        public void run() {
                            onGranted();
                        }
                    });
                    return true;
                }
        }
        return false;
    }

    /**
     * 去掉权限列表中重复的
     * @param 所有的权限队列
     */
    @SuppressWarnings("WeakerAccess")
    @CallSuper
    protected synchronized final void registerPermissions(@NonNull String[] perms) {
        Collections.addAll(mPermissions, perms);
    }
}