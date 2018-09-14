package com.eaju.dbmanager;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.eaju.dbmanager.config.DBManagerConfig;
import com.eaju.dbmanager.factory.DaoManagerFactory;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.List;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-09-14 11:00
 */
public class App extends Application {
    private final String dataBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/a/database/";

    private File file = new File(dataBasePath, "yianju.db");

    @Override
    public void onCreate() {
        super.onCreate();
        applyPermission(this);
    }

    private void applyPermission(final Context app) {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        DBManagerConfig.Builder builder = new DBManagerConfig.Builder(app);
                        builder.setdbSavePath(file);
                        DBManagerConfig build = builder.build();
                        DaoManagerFactory.getInstance(build);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        System.out.println(data.toString());
                    }
                }).start();
    }
}
