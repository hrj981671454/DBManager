package com.eaju.dbmanager.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-07-18 14:55
 */
public interface IBaseDao<T> {
    Long insert(T t);

    int update(T t, T where);

    /**
     * 查询全部
     * @param where
     * @return
     */
    ArrayList<T> queryAll(T where);

    /**
     * 按照部分条件查询
     * @param where
     * @return
     */
    ArrayList<T> query(T where);


    /**
     * 多条件查询
     * @param where
     * @param orderBy
     * @param startIndex
     * @param limit
     * @return
     */
    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

    boolean deleteAll(T where);

    boolean delete(T where);
}
