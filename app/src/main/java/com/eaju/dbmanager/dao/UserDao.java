package com.eaju.dbmanager.dao;

import android.util.Log;

import com.eaju.dbmanager.base.BaseDao;
import com.eaju.dbmanager.bean.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-07-18 16:00
 */
public class UserDao extends BaseDao<User> {
    @Override
    public String createDataBase() {
        return "create table if not exists tb_user(phoneNumber TEXT," +
                "password TEXT," +
                "isLogin TEXT," +
                "token TEXT," +
                "loginTime INTEGER," +
                "outLoginTime INTEGER," +
                "createTime INTEGER" + ")";
    }

    @Override
    public Long insert(User entity) {
        List<User> list = query(new User());
        User where = null;
        for (User user : list) {
            where = new User();
            where.setPhoneNumber(user.getPhoneNumber());
            user.setLogin(false);
            update(user, where);
        }
        Log.i(TAG, "用户" + entity.getPhoneNumber() + "登录");
        entity.setLogin(true);
        return super.insert(entity);
    }


    public User queryLoginStatus() {
        User user = new User();
        user.setLogin(true);
        ArrayList<User> query = queryAll(user);
        if (query.size() > 0) {
            User loginSuccess = query.get(0);
            return loginSuccess;
        }
        return null;
    }

}
