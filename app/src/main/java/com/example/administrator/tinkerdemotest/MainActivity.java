package com.example.administrator.tinkerdemotest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.tinkerdemotest.utils.PermissionHelper;
import com.example.administrator.tinkerdemotest.utils.PermissionInterface;
import com.example.administrator.tinkerdemotest.utils.PermissionUtil;
import com.example.administrator.tinkerdemotest.utils.SDcardUtil;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.tinkerdemotest.utils.PermisionUtils.verifyStoragePermissions;
import static com.example.administrator.tinkerdemotest.utils.SDcardUtil.getDefaultFilePath;
import static com.example.administrator.tinkerdemotest.utils.SDcardUtil.getSdCardPath;
import static com.example.administrator.tinkerdemotest.utils.SDcardUtil.isSdCardExist;


/**
 * 测试Tinker 热修复
 */

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    private static final String FILE_END = ".apk"; //文件后缀名
    private String mPatchFileDir; //patch要保存的文件夹
    private String mFilePtch; //patch文件保存路径

    //权限检测请求码
    private static final int PERMISSION_REQUEST_CODE2 = 12345;
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
//            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(this,"我改版本了。。。。",Toast.LENGTH_LONG).show();

    }


    public void onMyClick(View view) {
        verifyStoragePermissions(this);
//        mPatchFileDir = getExternalCacheDir().getAbsolutePath() + "/tpatch/patch_signed";
//        mFilePtch=mPatchFileDir.concat(FILE_END);/storage/emulated/0/patch_signed_7zip.apk

        if (Tinker.isTinkerInstalled()) {
            if (isSdCardExist()) {
                              TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed.apk");
                Toast.makeText(this, "修复", Toast.LENGTH_SHORT).show();
//                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                Toast.makeText(this, "SD卡不存在", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "没找到tinker", Toast.LENGTH_SHORT).show();
        }
    }


    public void onRead(View view) {
//        initSdcard();
        if (Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请CALL_PHONE权限
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE2);
            }
        }
    }

    public void onWrite(View view) {

    }
    private void initSdcard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    checkPermissions(needPermissions);
          int resulet = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            );
            if (PackageManager.PERMISSION_GRANTED == resulet) {
                Toast.makeText(MainActivity.this, "授权通过", Toast.LENGTH_SHORT).show();
//                Intent openCameraIntent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(openCameraIntent2, 10);
            } else {
                Toast.makeText(MainActivity.this, "授权没有通过", Toast.LENGTH_SHORT).show();
                checkPermissions(needPermissions);
            }

        } else {
            Intent openCameraIntent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(openCameraIntent2, 10);
        }
    }
    /**
     * 检测权限
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissionList = findDeniedPermissions(permissions);
        if (null != needRequestPermissionList && needRequestPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissionList.toArray(new String[needRequestPermissionList.size()]),
                    PERMISSION_REQUEST_CODE2);
        }
    }
    /**
     * 获取权限集中需要申请权限的列表
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissionList.add(perm);
            }
        }
        return needRequestPermissionList;
    }
    //检测用户是否开启权限
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSION_REQUEST_CODE2) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
            } else {
//                Intent openCameraIntent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(openCameraIntent2, 10);
                Toast.makeText(getApplicationContext(),"SD卡已授权",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 显示提示信息
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.map_notifyTitle);
        builder.setMessage(R.string.map_notifyMsg);
        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.map_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_SHORT).show();
                    }
                });

        builder.setPositiveButton(R.string.map_setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    /**
     * 检测是否所有的权限都已经授权
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
