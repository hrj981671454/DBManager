package com.eaju.dbmanager.config;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-09-14 10:12
 */
public class DBManagerConfig {
    private Context context;

    private File dbSavePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

    private DBManagerConfig(Context context) {
    }


    public static class Builder {
        private DBManagerConfig config;
        private Context         context;

        public Builder(Context context) {
            config = new DBManagerConfig(context);
            this.context = context;
        }

        /**
         * 设置数据库保存位置
         * @param dbSavePath
         * @return
         */
        public Builder setdbSavePath(File dbSavePath) {
            config.dbSavePath = dbSavePath;
            return this;//链式编程
        }

        public DBManagerConfig build() {
            return config;
        }
    }


    public File getDbSavePath() {
        return dbSavePath;
    }
}
