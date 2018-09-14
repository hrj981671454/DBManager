package com.eaju.dbmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.eaju.dbmanager.bean.User;
import com.eaju.dbmanager.dao.UserDao;
import com.eaju.dbmanager.factory.DaoManagerFactory;
import com.eaju.inject.ContentView;
import com.eaju.inject.OnClick;
import com.eaju.inject.ViewInject;
import com.eaju.inject.ViewInjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.btn_insert)
    AppCompatButton insert;

    @ViewInject(R.id.btn_update)
    AppCompatButton update;
    @ViewInject(R.id.btn_delete)
    AppCompatButton delete;
    @ViewInject(R.id.btn_select)
    AppCompatButton select;
    @ViewInject(R.id.tvText)
    ListView        listView;
    private UserDao mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);
        mDao = DaoManagerFactory.getInstance().getDBHelper(UserDao.class, User.class);
    }

    @OnClick({
            R.id.btn_select,
            R.id.btn_delete,
            R.id.btn_insert,
            R.id.btn_update
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select:
                ArrayList<User> query = mDao.query(new User());

                List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < query.size(); i++) {
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("name", query.get(i).getPhoneNumber());
                    item.put("password", query.get(i).getPassword());
                    item.put("age", query.get(i).token);
                    data.add(item);
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                        data,
                        R.layout.item_list,
                        new String[]{"name", "password", "age"},
                        new int[]{R.id.tvName, R.id.tvPassword, R.id.tvAge});
                listView.setAdapter(simpleAdapter);
                break;
            case R.id.btn_delete:
                User users = new User();
                users.setPhoneNumber("151102726040");
                mDao.delete(users);
                break;
            case R.id.btn_insert:
                for (int i = 0; i < 100; i++) {
                    User user = new User();
                    user.setPhoneNumber("15110272604" + i);
                    user.setPassword("123456");
                    user.setLogin(true);
                    user.setCreateTime(System.currentTimeMillis());
                    user.setToken("123456789798465132");
                    user.setOutLoginTime(0L);
                    mDao.insert(user);
                }
                break;
            case R.id.btn_update:
                User userFrom = new User();
                userFrom.setPhoneNumber("151102726042");


                User userWhere = new User();
                userWhere.setPhoneNumber("888888888");

                mDao.update(userFrom, userWhere);
                break;
        }
    }
}
