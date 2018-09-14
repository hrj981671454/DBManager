package com.eaju.dbmanager.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-07-18 15:36
 */
public class SQLUtil {

    /**
     * 通过SQL语句获得对应的VO。注意：Cursor的字段名或者别名一定要和VO的成员名一样
     * @param db
     * @param sql
     * @param clazz
     * @return
     */
    public static Object sql2VO(SQLiteDatabase db, String sql, Class clazz) {
        Cursor c = db.rawQuery(sql, null);
        return cursor2VO(c, clazz);
    }

    /**
     * 通过SQL语句获得对应的VO。注意：Cursor的字段名或者别名一定要和VO的成员名一样
     * @param db
     * @param sql
     * @param selectionArgs
     * @param clazz
     * @return
     */
    public static Object sql2VO(SQLiteDatabase db, String sql,
                                String[] selectionArgs, Class clazz) {
        Cursor c = db.rawQuery(sql, selectionArgs);
        return cursor2VO(c, clazz);
    }

    /**
     * 通过SQL语句获得对应的VO的List。注意：Cursor的字段名或者别名一定要和VO的成员名一样
     * @param db
     * @param sql
     * @param clazz
     * @return
     */
    public static List sql2VOList(SQLiteDatabase db, String sql, Class clazz) {
        Cursor c = db.rawQuery(sql, null);
        return cursor2VOList(c, clazz);
    }

    /**
     * 通过SQL语句获得对应的VO的List。注意：Cursor的字段名或者别名一定要和VO的成员名一样
     * @param db
     * @param sql
     * @param selectionArgs
     * @param clazz
     * @return
     */
    public static List sql2VOList(SQLiteDatabase db, String sql,
                                  String[] selectionArgs, Class clazz) {
        Cursor c = db.rawQuery(sql, selectionArgs);
        return cursor2VOList(c, clazz);
    }

    /**
     * 通过Cursor转换成对应的VO。注意：Cursor里的字段名（可用别名）必须要和VO的属性名一致
     * @param c
     * @param clazz
     * @return
     */
    public static Object cursor2VO(Cursor c, Class clazz) {
        if (c == null) {
            return null;
        }
        Object obj;
        int i = 1;
        try {
            c.moveToNext();
            obj = cursorToList(c, clazz);

            return obj;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("ERROR @：cursor2VO");
            return null;
        } finally {
            c.close();
        }
    }


    /**
     * 通过Cursor转换成对应的VO集合。注意：Cursor里的字段名（可用别名）必须要和VO的属性名一致
     * @param c
     * @param clazz
     * @return
     */
    public static ArrayList cursor2VOList(Cursor c, Class clazz) {
        if (c == null) {
            return null;
        }
        ArrayList list = new ArrayList();
        Object obj;
        try {
            while (c.moveToNext()) {
                obj = cursorToList(c, clazz);
                list.add(obj);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR @：cursor2VOList");
            return list;
        } finally {
            c.close();
        }
    }


    private static Object cursorToList(Cursor cursor, Class clazz) throws Exception {
        Object obj = clazz.newInstance();

        String[] columnNames = cursor.getColumnNames();

        //找到当前Class下所有的private或者public变量（全局变量）
        Field[] declaredFields = clazz.getFields();

        //变量所有变量
        for (Field field : declaredFields) {
            Class<? extends Object> typeClass = field.getType();// 属性类型

            for (int i = 0; i < columnNames.length; i++) {

                //获取到每个字段
                String columnName = columnNames[i];

                typeClass = getBasicClass(typeClass);

                //判断是否为基本数据类型
                boolean isBasicType = isBasicType(typeClass);

                if (isBasicType) {
                    //判空
                    if (!TextUtils.isEmpty(columnName)) {

                        if (columnName.equalsIgnoreCase(field.getName())) {
                            Object value;
                            if (typeClass.equals(Integer.class)) {
                                value = cursor.getInt(cursor.getColumnIndex(columnName));
                            } else if (typeClass.equals(String.class)) {
                                value = cursor.getString(cursor.getColumnIndex(columnName)) + "";
                            } else if (typeClass.equals(Double.class)) {
                                value = cursor.getDouble(cursor.getColumnIndex(columnName));
                            } else if (typeClass.equals(Short.class)) {
                                value = cursor.getShort(cursor.getColumnIndex(columnName));
                            } else if (typeClass.equals(Long.class)) {
                                value = cursor.getLong(cursor.getColumnIndex(columnName));
                            } else if (typeClass.equals(Float.class)) {
                                value = cursor.getFloat(cursor.getColumnIndex(columnName));
                            } else if (typeClass.equals(byte[].class)) {
                                value = cursor.getBlob(cursor.getColumnIndex(columnName));
                            } else {
                                continue;
                            }
                            if (null != value) {
                                field.setAccessible(true);

                                field.set(obj, value);
                            }
                        }
                    }
                }
            }
        }
        return obj;
    }


    /**
     * 判断是不是基本类型
     * @param typeClass
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static boolean isBasicType(Class typeClass) {
        if (typeClass.equals(Integer.class) || typeClass.equals(Long.class)
                || typeClass.equals(Float.class)
                || typeClass.equals(Double.class)
                || typeClass.equals(Boolean.class)
                || typeClass.equals(Byte.class)
                || typeClass.equals(Short.class)
                || typeClass.equals(String.class)) {

            return true;

        } else {
            return false;
        }
    }

    /**
     * 获得包装类
     * @param typeClass
     * @return
     */
    @SuppressWarnings("all")
    public static Class<? extends Object> getBasicClass(Class typeClass) {
        Class clazz = basicMap.get(typeClass);
        if (clazz == null) {
            clazz = typeClass;
        }
        return clazz;
    }

    @SuppressWarnings("rawtypes")
    private static Map<Class, Class> basicMap = new HashMap<Class, Class>();

    static {
        basicMap.put(int.class, Integer.class);
        basicMap.put(long.class, Long.class);
        basicMap.put(float.class, Float.class);
        basicMap.put(double.class, Double.class);
        basicMap.put(boolean.class, Boolean.class);
        basicMap.put(byte.class, Byte.class);
        basicMap.put(short.class, Short.class);
    }


    public static ContentValues getContentValues(HashMap<String, String> map) {

        ContentValues contentValues = new ContentValues();

        Set<String> keys = map.keySet();

        Iterator<String> iterator = keys.iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();

            String value = map.get(key);

            if (!TextUtils.isEmpty(value)) {
                contentValues.put(key, value);
            }
        }

        return contentValues;
    }


    public static class Condition {
        public String   whereClause;
        public String[] whereArgs;
        public String[] kets;

        public Condition(HashMap<String, String> map) {
            ArrayList arrayList = new ArrayList();
            ArrayList whereKey = new ArrayList();
            StringBuilder builder = new StringBuilder();
            builder.append(" 1=1 ");
            Set<String> keys = map.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                whereKey.add(key);
                String value = map.get(key);
                if (!TextUtils.isEmpty(value)) {
                    builder.append(" and " + key + " =? ");
                    arrayList.add(value);
                }
            }
            this.whereClause = builder.toString();
            this.whereArgs = (String[]) arrayList.toArray(new String[arrayList.size()]);
            this.kets = (String[]) whereKey.toArray(new String[whereKey.size()]);
        }
    }

}