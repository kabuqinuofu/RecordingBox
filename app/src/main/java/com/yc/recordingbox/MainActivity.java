package com.yc.recordingbox;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.yc.recordlibrary.box.RecordListener;
import com.yc.recordlibrary.box.RecordNoticeBox;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * 测试示例
 */
public class MainActivity extends AppCompatActivity {

    public static String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionGen.needPermission(this, 100, PERMISSIONS);
    }

    @PermissionSuccess(requestCode = 100)
    public void onPermissionSuccess() {
        findViewById(R.id.record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordNoticeBox.newBox(MainActivity.this)
                        .setFileDir("mine_record")
                        .setLimitTime(20)
                        .setRecordListener(new RecordListener() {
                            @Override
                            public void finish(String filePath, long time) {
                                Toast.makeText(MainActivity.this, "时长：" + time + "**" + filePath, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void cancle() {
                                Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_SHORT).show();
                            }
                        }).setCancelable(true)
                        .setCanceledOnTouchOutside(true).create().show();
            }
        });
    }

    @PermissionFail(requestCode = 100)
    private void onPermissionFail() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        this.startActivity(intent);
        MainActivity.this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}
