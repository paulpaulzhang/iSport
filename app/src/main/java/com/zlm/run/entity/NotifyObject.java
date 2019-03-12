package com.zlm.run.entity;

import android.app.Activity;
import android.support.annotation.DrawableRes;

import com.zlm.run.activity.BaseActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.entity
 * 文件名：   NotifyObject
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/19 10:49
 * 描述：     TODO 通知数据序列化
 */
public class NotifyObject implements Serializable {
    public Integer id;
    public String content;
    public String param;
    public String title;
    public Long firstTime;
    public Class<? extends BaseActivity> activityClass;
    public List<Long> times = new ArrayList<>();

    public static byte[] toBytes(NotifyObject obj) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        String content = null;
        oos = new ObjectOutputStream(bout);
        oos.writeObject(obj);
        oos.close();
        byte[] bytes = bout.toByteArray();
        bout.close();
        return bytes;
    }

    public static NotifyObject from(String content) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bin = new ByteArrayInputStream(content.getBytes("ISO-8859-1"));
        ObjectInputStream ois;
        NotifyObject obj;

        ois = new ObjectInputStream(bin);
        obj = (NotifyObject) ois.readObject();
        ois.close();
        bin.close();
        return obj;
    }

    public static String to(NotifyObject obj) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        String content;
        oos = new ObjectOutputStream(bout);
        oos.writeObject(obj);
        oos.close();
        byte[] bytes = bout.toByteArray();
        content = new String(bytes, "ISO-8859-1");
        bout.close();
        return content;
    }
}