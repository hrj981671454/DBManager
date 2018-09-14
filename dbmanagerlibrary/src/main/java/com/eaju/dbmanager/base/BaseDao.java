package com.eaju.dbmanager.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.eaju.dbmanager.annotation.DbField;
import com.eaju.dbmanager.annotation.DbTable;
import com.eaju.dbmanager.util.SQLUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-07-18 15:40
 */
public abstract class BaseDao<T> implements IBaseDao<T> {

    private SQLiteDatabase sqLiteDatabase;
    private boolean isInit = false;
    private String             tableName;
    private Class<T>           entityClass;
    private Map<String, Field> cacheMap;

    public synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if (!isInit) {
            this.entityClass = entity;
            this.sqLiteDatabase = sqLiteDatabase;
            if (entity.getAnnotation(DbTable.class) == null) {
                tableName = entity.getClass().getSimpleName();
            } else {
                tableName = entity.getAnnotation(DbTable.class).value();
            }
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            if (!TextUtils.isEmpty(createDataBase())) {
                sqLiteDatabase.execSQL(createDataBase());
            }
            cacheMap = new HashMap<>();
            initCacheMap();

            isInit = true;
        }
        return isInit;
    }


    protected void initCacheMap() {
        //获取前1个
        String splite = "select * from " + this.tableName + " limit 1,0";
        Cursor cursor = null;
        try {
            cursor = this.sqLiteDatabase.rawQuery(splite, null);
            String[] columnNames = cursor.getColumnNames();
            Field[] fields = entityClass.getFields();

            for (String columnName : columnNames) {
                for (Field field : fields) {
                    String filedName;
                    field.setAccessible(true);
                    if (null != field.getAnnotation(DbField.class)) {
                        filedName = field.getAnnotation(DbField.class).value();
                    } else {
                        filedName = field.getName();
                    }
                    if (columnName.equals(filedName)) {
                        cacheMap.put(columnName, field);
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public abstract String createDataBase();

    @Override
    public Long insert(T t) {
        HashMap<String, String> map = getValues(t);
        ContentValues contentValues = SQLUtil.getContentValues(map);
        Long result = sqLiteDatabase.insert(tableName, null, contentValues);
        return result;
    }

    @Override
    public int update(T entity, T where) {
        int result = -1;
        HashMap<String, String> values = getValues(entity);
        SQLUtil.Condition condition = new SQLUtil.Condition(getValues(where));
        result = sqLiteDatabase.update(tableName, SQLUtil.getContentValues(values), condition.whereClause, condition.whereArgs);
        return result;
    }


    @Override
    public ArrayList<T> queryAll(T where) {
        return query(where, null, null, null);
    }


    @Override
    public ArrayList<T> query(T where) {
        SQLUtil.Condition condition = new SQLUtil.Condition(getValues(where));

        Cursor cursor = sqLiteDatabase.query(tableName,
                condition.kets,
                condition.whereClause,
                condition.whereArgs,
                null,
                null,
                null);
        ArrayList<T> mList = new ArrayList<>(cursor.getCount());

        if (cursor.getCount() == 0) {
            return mList;
        }
        mList = SQLUtil.cursor2VOList(cursor, where.getClass());

        return mList;
    }


    @Override
    public ArrayList<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        HashMap map = getValues(where);

        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }

        SQLUtil.Condition condition = new SQLUtil.Condition(map);
        Cursor cursor = sqLiteDatabase.query(tableName,
                null,
                condition.whereClause,
                condition.whereArgs,
                null,
                null,
                orderBy,
                limitString);
        return SQLUtil.cursor2VOList(cursor, where.getClass());
    }


    @Override
    public boolean deleteAll(T where) {
        int delete = sqLiteDatabase.delete(tableName, null, null);
        return delete != 0;
    }

    @Override
    public boolean delete(T where) {
        SQLUtil.Condition condition = new SQLUtil.Condition(getValues(where));
        int delete = sqLiteDatabase.delete(tableName, condition.whereClause, condition.whereArgs);
        return delete != 0;
    }


    private ArrayList<String> getKeys(T entity) {
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        ArrayList<String> keysList = new ArrayList<>();
        while (fieldIterator.hasNext()) {
            Field colmunToFiled = fieldIterator.next();
            String cacheKey = null;
            if (null != colmunToFiled.getAnnotation(DbField.class)) {
                cacheKey = colmunToFiled.getAnnotation(DbField.class).value();
            } else {
                cacheKey = colmunToFiled.getName();
            }
            keysList.add(cacheKey);
        }
        return keysList;
    }


    private HashMap<String, String> getValues(T entity) {
        HashMap<String, String> result = new HashMap<>();
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()) {
            Field colmunToFiled = fieldIterator.next();
            String cacheKey = null;
            String cacheValue = null;
            if (null != colmunToFiled.getAnnotation(DbField.class)) {
                cacheKey = colmunToFiled.getAnnotation(DbField.class).value();
            } else {
                cacheKey = colmunToFiled.getName();
            }
            try {
                if (null == colmunToFiled.get(entity)) {
                    continue;
                }
                cacheValue = colmunToFiled.get(entity).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.put(cacheKey, cacheValue);
        }
        return result;
    }
}
