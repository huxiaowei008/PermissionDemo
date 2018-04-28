package com.hxw.permission;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String[] LOCATION_AND_CONTACTS =
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS};
    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraTask();
            }
        });

        findViewById(R.id.button_location_and_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationAndContactsTask();
            }
        });
    }

    /**
     * 判断是否有必要向用户解释为什么要这项权限
     *
     * @param perms 权限
     * @return 是否需要解释 {@code true} 需要 {@code false} 不需要
     */
    private boolean shouldShowRationale(@NonNull Activity activity,
                                        @Size(min = 1) @NonNull String... perms) {
        for (String perm : perms) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                return true;
            }
        }
        return false;
    }

    public void cameraTask() {
        //第一步:先判断是否有权限
        if (PermissionUtils.hasPermissions(MainActivity.this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "TODO: Camera things", Toast.LENGTH_LONG).show();
        } else {
            //第二步:判断是否需要解释(似乎可以不用这步)
            if (shouldShowRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                showAlertDialog(RC_CAMERA_PERM, Manifest.permission.CAMERA);
            } else {
                //第三步:不需要就申请
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, RC_CAMERA_PERM);
            }
        }

    }

    public void locationAndContactsTask() {
        if (PermissionUtils.hasPermissions(MainActivity.this, LOCATION_AND_CONTACTS)) {
            Toast.makeText(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
        } else {
            //第二步:判断是否需要解释(似乎可以不用这步)
            if (shouldShowRationale(MainActivity.this, LOCATION_AND_CONTACTS)) {
                showAlertDialog(RC_LOCATION_CONTACTS_PERM, LOCATION_AND_CONTACTS);
            } else {
                //第三步:不需要就申请
                ActivityCompat.requestPermissions(MainActivity.this,
                        LOCATION_AND_CONTACTS, RC_LOCATION_CONTACTS_PERM);
            }
        }
    }

    private void showAlertDialog(final int requestCode,
                                 @NonNull final String... perms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    /**
     * 申请权限
     *
     * @param requestCode 请求码
     * @param perms       权限
     */
    private void directRequestPermissions(int requestCode,
                                          @NonNull String... perms) {
        ActivityCompat.requestPermissions(MainActivity.this, perms, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("activity", "onRequestPermissionsResult");
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                //申请成功
                Toast.makeText(this, "申请成功 requestCode:" + requestCode, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "申请失败 requestCode:" + requestCode, Toast.LENGTH_LONG).show();
                //第四步:检查是否有个权限永久被拒
                if (somePermissionPermanentlyDenied(perm)) {
                    showSettingDialog();
                }
            }
        }
    }

    /**
     * 检查被拒绝权限列表中的至少一个权限是否已被永久拒绝（用户点击“永不再询问”）
     *
     * @return {@code true} 如果列表中至少有一个权限被永久拒绝
     */
    private boolean somePermissionPermanentlyDenied(@NonNull String perms) {
        return !ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, perms);
    }

    /**
     * 显示跳转设置的窗口
     */
    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("如果没有请求的权限，这个应用程序可能无法正常工作。打开app设置界面，修改app权限。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", getPackageName(), null)));
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
