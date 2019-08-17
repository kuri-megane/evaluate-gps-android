package io.github.kuri_megane.evaluate_gps_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

// import androidx.core.app.ActivityCompat;
// import android.support.v7.app.AppCompatActivity;
// import android.support.annotation.NonNull;
// import android.support.v4.app.ActivityCompat;
// import android.support.v4.content.ContextCompat;
// import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MULTI_PERMISSIONS = 101;

    private TextView textView;
    private StorageReadWrite fileReadWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        fileReadWrite = new StorageReadWrite(context);

        // Android 6, API 23以上でパーミッシンの確認
        if (Build.VERSION.SDK_INT >= 23) {
            checkMultiPermissions();
        }

        startLocationService();

    }

    // 位置情報許可の確認、外部ストレージのPermissionにも対応できるようにしておく
    private void checkMultiPermissions() {

        // 位置情報の Permission
        int permissionLocation = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        );
        // 外部ストレージ書き込みの Permission
        int permissionExtStorage = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );

        ArrayList<String> reqPermissions = new ArrayList<>();

        // 位置情報の Permission が許可されているか確認
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 外部ストレージ書き込みが許可されているか確認
        if (permissionExtStorage != PackageManager.PERMISSION_GRANTED) {
            reqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // 未許可
        if (!reqPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    reqPermissions.toArray(new String[0]),
                    REQUEST_MULTI_PERMISSIONS
            );
        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults
    ) {

        if (requestCode == REQUEST_MULTI_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    // 位置情報
                    if (permissions[i].
                            equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            toastMake("位置情報の許可がないので計測できません");
                        }
                    }
                    // 外部ストレージ
                    else if (permissions[i].
                            equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            toastMake("外部書込の許可がないので書き込みできません");
                        }
                    }
                }
            }
        }
    }

    private void startLocationService() {
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.log_text);

        Button buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), LocationService.class);

                // API 26 以降
                if (Build.VERSION.SDK_INT >= 26) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }

                // Activityを終了させる
                // finish();
            }
        });

        Button buttonLog = findViewById(R.id.button_log);
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(fileReadWrite.readFile());
            }
        });

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Serviceの停止
                Intent intent = new Intent(getApplication(), LocationService.class);
                stopService(intent);

                fileReadWrite.clearFile();
                textView.setText("");
            }
        });
    }

    // トーストの生成
    private void toastMake(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        // 位置調整
        toast.setGravity(Gravity.CENTER, 0, 200);
        toast.show();
    }
}
