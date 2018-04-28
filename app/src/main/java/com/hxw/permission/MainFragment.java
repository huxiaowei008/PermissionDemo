package com.hxw.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * @author hxw on 2018/4/27.
 */
public class MainFragment extends Fragment {

    private static final int RC_SMS_PERM = 122;

    private void smsTask() {
        //第一步:先判断是否有权限
        if (PermissionUtils.hasPermissions(getActivity(), Manifest.permission.READ_SMS)) {
            Toast.makeText(getActivity(), "TODO: Camera things", Toast.LENGTH_LONG).show();
        } else {
            //第二步:判断是否需要解释(似乎可以不用这步)
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                showAlertDialog(RC_SMS_PERM, Manifest.permission.READ_SMS);
            } else {
                //第三步:不需要就申请
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, RC_SMS_PERM);
            }
        }
    }

    /**
     * 申请权限
     *
     * @param requestCode 请求码
     * @param perms       权限
     */
    private void directRequestPermissions(int requestCode,
                                          @NonNull String... perms) {
        requestPermissions(perms, requestCode);
    }

    private void showAlertDialog(final int requestCode,
                                 @NonNull final String... perms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("说明申请权限的原因");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //申请权限
                directRequestPermissions(requestCode, perms);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("fragment", "onRequestPermissionsResult");
        //在fragment中请求权限时,activity的onRequestPermissionsResult方法也会被调用到
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                //申请成功
                Toast.makeText(getActivity(), "申请成功 requestCode:" + requestCode, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "申请失败 requestCode:" + requestCode, Toast.LENGTH_LONG).show();
                //检查是否有个权限永久被拒
                if (!shouldShowRequestPermissionRationale(perm)) {
                    showSettingDialog();
                }

            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_main, container);
        v.findViewById(R.id.button_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsTask();
            }
        });
        return v;
    }

    /**
     * 显示跳转设置的窗口
     */
    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("如果没有请求的权限，这个应用程序可能无法正常工作。打开app设置界面，修改app权限。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", getActivity().getPackageName(), null)));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
