package com.example.administrator.tinkerdemotest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;


/**
 * 测试Tinker 热修复
 */

public class MainActivity extends AppCompatActivity {
    private static final String FILE_END = ".apk"; //文件后缀名
    private String mPatchFileDir; //patch要保存的文件夹
    private String mFilePtch; //patch文件保存路径
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(this,"我改版本了。。。。",Toast.LENGTH_LONG).show();
    }


    public void onMyClick(View view) {
//        mPatchFileDir = getExternalCacheDir().getAbsolutePath() + "/tpatch/patch_signed";
//        mFilePtch=mPatchFileDir.concat(FILE_END);

        if (Tinker.isTinkerInstalled()) {
            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed.apk");
            Toast.makeText(this, "修复", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没找到tinker", Toast.LENGTH_SHORT).show();
        }
    }
}
