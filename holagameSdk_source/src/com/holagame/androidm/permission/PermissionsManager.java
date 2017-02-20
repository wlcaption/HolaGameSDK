package com.holagame.androidm.permission;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.Manifest;
import android.R.array;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
/**
 * 权限管理器，申请权限在这里执行,注意这个管理器只能处理普通的权限请求
 * 如果遇到特殊的权限请求，需特殊处理，比如现在的悬浮窗请求
 * @author zoulong
 *
 */
public class PermissionsManager {
	 private static final String TAG = PermissionsManager.class.getSimpleName();
        /**申请权限队列*/
	    private final Set<String> mPendingRequests = new HashSet<String>(1);
	    /**所有权限队列*/
	    private final Set<String> mPermissions = new HashSet<String>(1);
	    /**特殊权限处理对象*/
	    private ArrayList<PermissionSpecialHandler> permissionSpecialHandlers = new ArrayList<PermissionSpecialHandler>();
	    
	    private final List<WeakReference<PermissionsResultAction>> mPendingActions = new ArrayList<WeakReference<PermissionsResultAction>>(1);

	    private static PermissionsManager mInstance = null;

	    public static PermissionsManager getInstance() {
	        if (mInstance == null) {
	            mInstance = new PermissionsManager();
	        }
	        return mInstance;
	    }

	    private PermissionsManager() {
	        initializePermissionsMap();
	    }

	    /**
	     * 此方法使用反射来读取清单类中的所有权限
	     */
	    private synchronized void initializePermissionsMap() {
	        Field[] fields = Manifest.permission.class.getFields();
	        for (Field field : fields) {
	            String name = null;
	            try {
	                name = (String) field.get("");
	            } catch (IllegalAccessException e) {
	                Log.e(TAG, "Could not access field", e);
	            }
	            mPermissions.add(name);
	        }
	    }

	    /**
	     * 此方法检索在应用程序清单中声明的所有权限。它返回一个非空数组
	     * @param activity 当前activity
	     * @return 返回在xml文件配置的所有权限数组
	     */
	    @NonNull
	    private synchronized String[] getManifestPermissions(@NonNull final Activity activity) {
	        PackageInfo packageInfo = null;
	        List<String> list = new ArrayList<String>(1);
	        try {
	            Log.d(TAG, activity.getPackageName());
	            packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
	        } catch (PackageManager.NameNotFoundException e) {
	            Log.e(TAG, "A problem occurred when retrieving permissions", e);
	        }
	        if (packageInfo != null) {
	            String[] permissions = packageInfo.requestedPermissions;
	            if (permissions != null) {
	                for (String perm : permissions) {
	                    Log.d(TAG, "xml配置的权限: " + perm);
	                    list.add(perm);
	                }
	            }
	        }
	        return list.toArray(new String[list.size()]);
	    }

	    /**
	     *将回调加入队列，一个权限申请可以有一个回调
	     * @param permissions 权限数组
	     * @param action      回调对象
	     */
	    private synchronized void addPendingAction(@NonNull String[] permissions,
	                                               @Nullable PermissionsResultAction action) {
	        if (action == null) {
	            return;
	        }
	        action.registerPermissions(permissions);
	        //创建弱应用对象，当系统中action对面只被WeakReference指向时，将被GC回收。
	        mPendingActions.add(new WeakReference<PermissionsResultAction>(action));
	    }

	    /**
	     * 删除回调，让系统回收
	     * @param 传入的回调
	     */
	    private synchronized void removePendingAction(@Nullable PermissionsResultAction action) {
	        for (Iterator<WeakReference<PermissionsResultAction>> iterator = mPendingActions.iterator();
	             iterator.hasNext(); ) {
	            WeakReference<PermissionsResultAction> weakRef = iterator.next();
	            if (weakRef.get() == action || weakRef.get() == null) {
	                iterator.remove();
	            }
	        }
	    }

	    /**
	     * 检测是否获取到权限
	     *
	     * @param context 上下文
	     * @param permission 权限名称
	     * @return 是否获取到权限，获取到则返回true反之者返回false
	     */
	    @SuppressWarnings("unused")
	    public synchronized boolean hasPermission(@Nullable Context context, @NonNull String permission) {
	    	PermissionSpecialHandler permissionsHandler = isSpecialPermission(permission);
	    	if(permissionsHandler != null){
	    		return permissionsHandler.hasPermission();
	    	}
	        return context != null && (ActivityCompat.checkSelfPermission(context, permission)
	                == PackageManager.PERMISSION_GRANTED || !mPermissions.contains(permission));
	    }

	    /**
	     *检测是否获取到数组中所有权限
	     * @param context  上下文
	     * @param permissions 要检测的数组
	     * @return true 是否获取到权限，获取到则返回true反之者返回false
	     */
	    @SuppressWarnings("unused")
	    public synchronized boolean hasAllPermissions(@Nullable Context context, @NonNull String[] permissions) {
	        if (context == null) {
	            return false;
	        }
	        boolean hasAllPermissions = true;
	        for (String perm : permissions) {
	            hasAllPermissions &= hasPermission(context, perm);
	        }
	        return hasAllPermissions;
	    }

	    /**
	     * 此方法将请求在应用程序清单中声明的所有权限
	     *
	     * @param activity the Activity necessary to request and check permissions.
	     * @param action   the PermissionsResultAction used to notify you of permissions being accepted.
	     */
	    @SuppressWarnings("unused")
	    public synchronized void requestAllManifestPermissionsIfNecessary(final @Nullable Activity activity,
	                                                                      final @Nullable PermissionsResultAction action) {
	        if (activity == null) {
	            return;
	        }
	        String[] perms = getManifestPermissions(activity);
	        requestPermissionsIfNecessaryForResult(activity, perms, action);
	    }

	    /**
	     *发起权限请求(Actvity页面的发起，Fragment页面中由于回调问题，不能使用这个方法)
	     * @param activity    当前Actvity
	     * @param permissions 传入的权限列表
	     * @param action      回调
	     */
	    @SuppressWarnings("unused")
	    public synchronized void requestPermissionsIfNecessaryForResult(@Nullable Activity activity,
	                                                                    @NonNull String[] permissions,
	                                                                    @Nullable PermissionsResultAction action) {
	        if (activity == null) {
	            return;
	        }
	        addPendingAction(permissions, action);
	        //判断是否是6.0以上的系统
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
	        	//版本小于androidM
	            doPermissionWorkBeforeAndroidM(activity, permissions, action);
	        } else {
	            List<String> permList = getPermissionsListToRequest(activity, permissions, action);
	            if (permList.isEmpty()) {
	                //清空回调
	                removePendingAction(action);
	            } else {
	                String[] permsToRequest = permList.toArray(new String[permList.size()]);
	                mPendingRequests.addAll(permList);
	                //向系统发起权限请求
	                ActivityCompat.requestPermissions(activity, permsToRequest, 1);
	                //特殊权限处理请求
	                specialPermissionsHandle(permsToRequest);
	            }
	        }
	    }
	    
        /**
         * 特殊权限的申请
         * @param permsToRequest 获取权限列表
         */
	    private void specialPermissionsHandle(String[] permsToRequest) {
	    	//如果没有特殊权限处理则返回
	    	if(permissionSpecialHandlers.size()==0){
	    		return;
	    	}
            //遍历特殊权限，与获取到的配置权限做对比，如果有这用特殊权限处理处理
	        for (int i = 0; i < permissionSpecialHandlers.size(); i++) {
	        	PermissionSpecialHandler permissionHandler = permissionSpecialHandlers.get(i);
		    	for(int n=0;n<permsToRequest.length;n++){
		    		if(permsToRequest[n].equals(permissionHandler.getPermissionName()) && 
		    				!permissionHandler.hasPermission()){
		    			permissionHandler.HandlerPermission();
		    			break;
		    		}
		    	}
	        }
		}

		/**
         *发起权限请求(fragment页面的发起，Actvity页面中由于回调问题，不能使用这个方法)
	     * @param fragment    当前fragment，
	     * @param permissions 传入的权限列表
	     * @param action      回调
	     */
	    @SuppressWarnings("unused")
	    public synchronized void requestPermissionsIfNecessaryForResult(@NonNull Fragment fragment,
	                                                                    @NonNull String[] permissions,
	                                                                    @Nullable PermissionsResultAction action) {
	        Activity activity = fragment.getActivity();
	        if (activity == null) {
	            return;
	        }
	        addPendingAction(permissions, action);
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
	            doPermissionWorkBeforeAndroidM(activity, permissions, action);
	        } else {
	            List<String> permList = getPermissionsListToRequest(activity, permissions, action);
	            if (permList.isEmpty()) {
	                //if there is no permission to request, there is no reason to keep the action int the list
	                removePendingAction(action);
	            } else {
	                String[] permsToRequest = permList.toArray(new String[permList.size()]);
	                mPendingRequests.addAll(permList);
	                fragment.requestPermissions(permsToRequest, 1);
	            }
	        }
	    }

	    /**
	     * 通知PermissionsManager权限已经发送变化
	     *
	     * @param permissions 变化的权限
	     * @param results  请求的结果
	     */
	    @SuppressWarnings("unused")
	    public synchronized void notifyPermissionsChange(@NonNull String[] permissions, @NonNull int[] results) {
	        int size = permissions.length;
	        if (results.length < size) {
	            size = results.length;
	        }
	        Iterator<WeakReference<PermissionsResultAction>> iterator = mPendingActions.iterator();
	        while (iterator.hasNext()) {
	            PermissionsResultAction action = iterator.next().get();
	            for (int n = 0; n < size; n++) {
	                if (action == null || action.onResult(permissions[n], results[n])) {
	                    iterator.remove();
	                    break;
	                }
	            }
	        }
	        for (int n = 0; n < size; n++) {
	            mPendingRequests.remove(permissions[n]);
	        }
	    }

	    /**
	     *在androidM前的请求，直接回调成功或者失败
	     * @param activity    当前activity
	     * @param permissions 权限列表
	     * @param action      回调
	     */
	    private void doPermissionWorkBeforeAndroidM(@NonNull Activity activity,
	                                                @NonNull String[] permissions,
	                                                @Nullable PermissionsResultAction action) {
	        for (String perm : permissions) {
	            if (action != null) {
	                if (!mPermissions.contains(perm)) {
	                    action.onResult(perm, Permissions.NOT_FOUND);
	                } else if (ActivityCompat.checkSelfPermission(activity, perm)
	                        != PackageManager.PERMISSION_GRANTED) {
	                    action.onResult(perm, Permissions.DENIED);
	                } else {
	                    action.onResult(perm, Permissions.GRANTED);
	                }
	            }
	        }
	    }

	    /**
	     *去掉已经获取过的权限
	     * @param activity    当前activity
	     * @param permissions 读取出来的所有的权限列表
	     * @param action      回调
	     * @return 返回没有获取到权限的名称
	     */
	    @NonNull
	    private List<String> getPermissionsListToRequest(@NonNull Activity activity,
	                                                     @NonNull String[] permissions,
	                                                     @Nullable PermissionsResultAction action) {
	        List<String> permList = new ArrayList<String>(permissions.length);
	        for (String perm : permissions) {
	            if (!mPermissions.contains(perm)) {
	                if (action != null) {
	                    action.onResult(perm, Permissions.NOT_FOUND);
	                }
	            } else if (ActivityCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
	                if (!mPendingRequests.contains(perm)) {
	                    permList.add(perm);
	                }
	            } else {
	                if (action != null) {
	                    action.onResult(perm, Permissions.GRANTED);
	                }
	            }
	        }
	        return permList;
	    }

	    /**
	     * 添加特殊权限声明，和特殊权限的处理方法，注意在这添加的权限声明，一定要在配置文件中添加，不然会申请权限失败
	     * @param permission 特殊权限名称
	     */
	    public void addSpecialPermissions(PermissionSpecialHandler permissionSpecialManager){
	    	permissionSpecialHandlers.add(permissionSpecialManager);
	    }
	    
	    /**
	     * 判断一个权限是否是特殊权限
	     * @return 如果是则返回该特殊权限处理器
	     */
	    public PermissionSpecialHandler isSpecialPermission(String permission){
	    	for (int i=0;i<permissionSpecialHandlers.size();i++) {
	    		PermissionSpecialHandler permissionsHandler = permissionSpecialHandlers.get(0);
				if(permissionsHandler.getPermissionName().equals(permission)){
					return permissionsHandler;
				}
			}
	    	return null;
	    }
	}