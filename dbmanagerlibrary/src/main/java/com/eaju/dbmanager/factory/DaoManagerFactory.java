package com.eaju.dbmanager.factory;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.eaju.dbmanager.base.BaseDao;
import com.eaju.dbmanager.config.DBManagerConfig;
import com.eaju.dbmanager.util.FileUtil;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-07-18 15:33
 */
public class DaoManagerFactory {

    private DBManagerConfig dbManagerConfig;

    //单例对象
    private static volatile DaoManagerFactory mInstance;


    private String path;

    private SQLiteDatabase sqLiteDatabase;


    private Map<String, BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    /**
     * 获取单例方法
     * 第一次调用
     * @param dbManagerConfig
     * @return
     */
    public static DaoManagerFactory getInstance(DBManagerConfig dbManagerConfig) {
        if (mInstance == null) {
            synchronized (DaoManagerFactory.class) {
                if (mInstance == null) {
                    mInstance = new DaoManagerFactory(dbManagerConfig);
                }
            }
        }
        return mInstance;
    }


    /**
     * 第二次获取单例
     * @return
     */
    public static DaoManagerFactory getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("DaoManagerFactory is not initialized" + "\n" + " DaoManagerFactory没有初始化，请初始化后调用");
        }
        return mInstance;
    }

    private DaoManagerFactory(DBManagerConfig dbManagerConfig) {
        File dbSavePath = dbManagerConfig.getDbSavePath();
        this.path = dbSavePath.getAbsolutePath();
        Boolean fileIsExist = FileUtil.isFileExist(path);
        if (!fileIsExist) {
            FileUtil.createFile(path);
        }
        openDatabase();
    }

    public synchronized <T extends BaseDao<M>, M> T getDBHelper(Class<T> clazz, Class<M> entity) {
        BaseDao baseDao = null;
        if (map.get(clazz.getSimpleName()) != null) {
            return (T) map.get(clazz.getSimpleName());
        }
        try {
            baseDao = clazz.newInstance();
            baseDao.init(entity, sqLiteDatabase);
            map.put(clazz.getSimpleName(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

    /**
     * 可用于数据库分库等操作
     * @param path 自定义数据库保存位置
     * @param clazz 
     * @param entity
     * @param <T>
     * @param <M>
     * @return
     */
    public synchronized <T extends BaseDao<M>, M> T getDataHelper(String path, Class<T> clazz, Class<M> entity) {
        String value = null;
        if (!TextUtils.isEmpty(path)) {
            value = path;
        } else {
            value = dbManagerConfig.getDbSavePath().getAbsolutePath();
        }
        Boolean fileIsExist = FileUtil.isFileExist(value);

        if (!fileIsExist) {
            FileUtil.createFile(value);
        }
        SQLiteDatabase userDatabase = SQLiteDatabase.openOrCreateDatabase(value, null);
        BaseDao baseDao = null;
        try {
            baseDao = clazz.newInstance();
            baseDao.init(entity, userDatabase);
            map.put(clazz.getSimpleName(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }


    private void openDatabase() {
        this.sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(path, null);
    }
}
